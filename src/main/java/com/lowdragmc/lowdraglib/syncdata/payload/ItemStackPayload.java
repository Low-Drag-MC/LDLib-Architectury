package com.lowdragmc.lowdraglib.syncdata.payload;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ItemStackPayload extends ObjectTypedPayload<ItemStack> {

    @Override
    public void writePayload(RegistryFriendlyByteBuf buf) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, payload);
    }

    @Override
    public void readPayload(RegistryFriendlyByteBuf buf) {
        payload = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        return payload.saveOptional(provider);
    }

    @Override
    public void deserializeNBT(Tag tag, HolderLookup.Provider provider) {
        payload = ItemStack.OPTIONAL_CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY);
    }
}

