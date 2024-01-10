package com.lowdragmc.lowdraglib.networking.both;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IRPCBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.rpc.RPCSender;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * a packet that contains payload for managed fields
 */
@NoArgsConstructor
public class PacketRPCMethodPayload extends PacketIntLocation implements CustomPacketPayload {
    public static ResourceLocation ID = LDLib.location("rpc_method_payload");
    private BlockEntityType<?> blockEntityType;
    private ITypedPayload<?>[] payloads;

    private String methodName;
    private int managedId;

    public PacketRPCMethodPayload(FriendlyByteBuf buffer) {
        decode(buffer);
    }

    public PacketRPCMethodPayload(int managedId, BlockEntityType<?> type, BlockPos pos, String methodName, ITypedPayload<?>[] payloads) {
        super(pos);
        this.managedId = managedId;
        blockEntityType = type;
        this.methodName = methodName;
        this.payloads = payloads;
    }

    public static PacketRPCMethodPayload of(IManaged managed, IRPCBlockEntity tile, String methodName, Object... args) {
        var index = Arrays.stream(tile.getRootStorage().getManaged()).toList().indexOf(managed);
        if (index < 0) {
            throw new IllegalArgumentException("No such rpc managed: " + methodName);
        }
        var rpcMethod = tile.getRPCMethod(managed, methodName);
        if (rpcMethod == null) {
            throw new IllegalArgumentException("No such RPC method: " + methodName);
        }
        var payloads = rpcMethod.serializeArgs(args);
        return new PacketRPCMethodPayload(index, tile.getBlockEntityType(), tile.getCurrentPos(), methodName, payloads);
    }

    public static void processPacket(@NotNull BlockEntity blockEntity, RPCSender sender, PacketRPCMethodPayload packet) {
        if (blockEntity.getType() != packet.blockEntityType) {
            LDLib.LOGGER.warn("Block entity type mismatch in rpc payload packet!");
            return;
        }
        if (!(blockEntity instanceof IRPCBlockEntity tile)) {
            LDLib.LOGGER.error("Received managed payload packet for block entity that does not implement IRPCBlockEntity: " + blockEntity);
            return;
        }
        if (tile.getRootStorage().getManaged().length >= packet.managedId) {
            LDLib.LOGGER.error("Received managed couldn't be found in IRPCBlockEntity: " + blockEntity);
            return;
        }
        var rpcMethod = tile.getRPCMethod(tile.getRootStorage().getManaged()[packet.managedId], packet.methodName);
        if (rpcMethod == null) {
            LDLib.LOGGER.error("Cannot find RPC method: " + packet.methodName);
            return;
        }

        rpcMethod.invoke(tile, sender, packet.payloads);

    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeVarInt(this.managedId);
        buf.writeResourceLocation(Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType)));
        buf.writeUtf(methodName);
        buf.writeVarInt(payloads.length);
        for (ITypedPayload<?> payload : payloads) {
            buf.writeByte(payload.getType());
            payload.writePayload(buf);
        }
    }

    public static PacketRPCMethodPayload decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        int managedId = buffer.readVarInt();

        BlockEntityType<?> blockEntityType = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(buffer.readResourceLocation());
        String methodName = buffer.readUtf();
        ITypedPayload<?>[] payloads = new ITypedPayload<?>[buffer.readVarInt()];
        for (int i = 0; i < payloads.length; i++) {
            byte id = buffer.readByte();
            var payload = TypedPayloadRegistries.create(id);
            payload.readPayload(buffer);
            payloads[i] = payload;
        }
        return new PacketRPCMethodPayload(managedId, blockEntityType, pos, methodName, payloads);
    }

    public static void executeClient(PacketRPCMethodPayload packet, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            if (context.level().isEmpty()) {
                return;
            }
            BlockEntity tile = context.level().get().getBlockEntity(packet.pos);
            if (tile == null) {
                return;
            }
            processPacket(tile, RPCSender.ofServer(), packet);
        });
    }

    public static void executeServer(PacketRPCMethodPayload packet, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            var player = context.player().orElse(null);
            if (player == null) {
                LDLib.LOGGER.error("Received rpc payload packet from client with no player!");
                return;
            }
            var level = player.level();
            if (!level.isLoaded(packet.pos)) return;
            BlockEntity tile = level.getBlockEntity(packet.pos);
            if (tile == null) {
                return;
            }
            processPacket(tile, RPCSender.ofClient(player), packet);
        });
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
