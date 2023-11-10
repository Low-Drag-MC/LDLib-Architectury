package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.scene.ParticleManager;
import com.lowdragmc.lowdraglib.core.mixins.accessor.EntityAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.material.FluidState;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Author: KilaBash
 * Date: 2021/08/25
 * Description: TrackedDummyWorld. Used to build a Fake World.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TrackedDummyWorld extends DummyWorld {

    private Predicate<BlockPos> renderFilter;
    public final Level proxyWorld;
    public final Map<BlockPos, BlockInfo> renderedBlocks = new HashMap<>();
    public final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
    public final Map<Integer, Entity> entities =new Int2ObjectArrayMap<>();

    public final Vector3f minPos = new Vector3f(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    public final Vector3f maxPos = new Vector3f(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    public void setRenderFilter(Predicate<BlockPos> renderFilter) {
        this.renderFilter = renderFilter;
    }

    public TrackedDummyWorld(){
        super(Minecraft.getInstance().level);
        proxyWorld = null;
    }

    public TrackedDummyWorld(Level world){
        super(world);
        proxyWorld = world;
    }

    public void clear() {
        renderedBlocks.clear();
        blockEntities.clear();
        entities.clear();
    }

    public Map<BlockPos, BlockInfo> getRenderedBlocks() {
        return renderedBlocks;
    }

    public void addBlocks(Map<BlockPos, BlockInfo> renderedBlocks) {
        renderedBlocks.forEach(this::addBlock);
    }

    public void addBlock(BlockPos pos, BlockInfo blockInfo) {
        if (blockInfo.getBlockState().getBlock() == Blocks.AIR)
            return;
        this.renderedBlocks.put(pos, blockInfo);
        this.blockEntities.remove(pos);
        minPos.x = (Math.min(minPos.x, pos.getX()));
        minPos.y = (Math.min(minPos.y, pos.getY()));
        minPos.z = (Math.min(minPos.z, pos.getZ()));
        maxPos.x = (Math.max(maxPos.x, pos.getX()));
        maxPos.y = (Math.max(maxPos.y, pos.getY()));
        maxPos.z = (Math.max(maxPos.z, pos.getZ()));
    }

    public BlockInfo removeBlock(BlockPos pos) {
        this.blockEntities.remove(pos);
        return this.renderedBlocks.remove(pos);
    }

    // wth? mcp issue
    public void setInnerBlockEntity(@Nonnull BlockEntity pBlockEntity) {
        blockEntities.put(pBlockEntity.getBlockPos(), pBlockEntity);
    }

    @Override
    public void setBlockEntity(@Nonnull BlockEntity pBlockEntity) {
        blockEntities.put(pBlockEntity.getBlockPos(), pBlockEntity);
    }

    @Override
    public boolean setBlock(@Nonnull BlockPos pos, @Nonnull BlockState state, int a, int b) {
        this.renderedBlocks.put(pos, BlockInfo.fromBlockState(state));
        this.blockEntities.remove(pos);
        return true;
    }

    @Override
    public BlockEntity getBlockEntity(@Nonnull BlockPos pos) {
        if (renderFilter != null && !renderFilter.test(pos))
            return null;
        return proxyWorld != null ? proxyWorld.getBlockEntity(pos) : blockEntities.computeIfAbsent(pos, p -> renderedBlocks.getOrDefault(p, BlockInfo.EMPTY).getBlockEntity(this, p));
    }

    @Override
    public BlockState getBlockState(@Nonnull BlockPos pos) {
        if (renderFilter != null && !renderFilter.test(pos))
            return Blocks.AIR.defaultBlockState(); //return air if not rendering this com.lowdragmc.lowdraglib.test.block
        return proxyWorld != null ? proxyWorld.getBlockState(pos) : renderedBlocks.getOrDefault(pos, BlockInfo.EMPTY).getBlockState();
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        ((EntityAccessor) entity).invokeSetLevel(this);
        if (entity instanceof ItemFrame itemFrame)
            itemFrame.setItem(withUnsafeNBTDiscarded(itemFrame.getItem()));
        if (entity instanceof ArmorStand armorStand)
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values())
                armorStand.setItemSlot(equipmentSlot,
                        withUnsafeNBTDiscarded(armorStand.getItemBySlot(equipmentSlot)));
        entities.put(entity.getId(), entity);
        return true;
    }

    public static ItemStack withUnsafeNBTDiscarded(ItemStack stack) {
        if (stack.getTag() == null)
            return stack;
        ItemStack copy = stack.copy();
        stack.getTag()
                .getAllKeys()
                .stream()
                .filter(TrackedDummyWorld::isUnsafeItemNBTKey)
                .forEach(copy::removeTagKey);
        if (copy.getTag().isEmpty())
            copy.setTag(null);
        return copy;
    }

    public static boolean isUnsafeItemNBTKey(String name) {
        if (name.equals(EnchantedBookItem.TAG_STORED_ENCHANTMENTS))
            return false;
        if (name.equals("Enchantments"))
            return false;
        if (name.contains("Potion"))
            return false;
        if (name.contains("Damage"))
            return false;
        if (name.equals("display"))
            return false;
        return true;
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return super.getEntities();
    }

    @Override
    public Entity getEntity(int id) {
        for (Entity entity : entities.values()) {
            if (entity.getId() == id && entity.isAlive())
                return entity;
        }
        return super.getEntity(id);
    }

    public Vector3f getSize() {
        return new Vector3f(maxPos.x - minPos.x + 1, maxPos.y - minPos.y + 1, maxPos.z - minPos.z + 1);
    }

    public Vector3f getMinPos() {
        return minPos;
    }

    public Vector3f getMaxPos() {
        return maxPos;
    }

    @Override
    public ChunkSource getChunkSource() {
        return proxyWorld == null ? super.getChunkSource() : proxyWorld.getChunkSource();
    }

    @Override
    public FluidState getFluidState(BlockPos pPos) {
        return proxyWorld == null ? super.getFluidState(pPos) : proxyWorld.getFluidState(pPos);
    }

    @Override
    public int getBlockTint(@Nonnull BlockPos blockPos, @Nonnull ColorResolver colorResolver) {
        return  proxyWorld == null ? super.getBlockTint(blockPos, colorResolver) : proxyWorld.getBlockTint(blockPos, colorResolver);
    }

    @Nonnull
    @Override
    public Holder<Biome> getBiome(@Nonnull BlockPos pos) {
        return proxyWorld == null ? super.getBiome(pos) : proxyWorld.getBiome(pos);
    }

    @Override
    public void setParticleManager(ParticleManager particleManager) {
        super.setParticleManager(particleManager);
        if (proxyWorld instanceof DummyWorld dummyWorld) {
            dummyWorld.setParticleManager(particleManager);
        }
    }

    @Nullable
    @Override
    public ParticleManager getParticleManager() {
        ParticleManager particleManager = super.getParticleManager();
        if (particleManager == null && proxyWorld instanceof DummyWorld dummyWorld) {
            return dummyWorld.getParticleManager();
        }
        return particleManager;
    }

    public void tickWorld() {
        var iter = entities.values().iterator();
        while (iter.hasNext()) {
            var entity = iter.next();
            entity.tickCount++;
            entity.setOldPosAndRot();
            entity.tick();

            if (entity.getY() <= -.5f)
                entity.discard();

            if (!entity.isAlive())
                iter.remove();
        }

        for (var entry : renderedBlocks.entrySet()) {
            var blockState = entry.getValue().getBlockState();
            var blockEntity = getBlockEntity(entry.getKey());
            if (blockEntity != null && blockEntity.getType().isValid(blockState)) {
                try {
                    BlockEntityTicker ticker = blockState.getTicker(this, blockEntity.getType());
                    if (ticker != null) {
                        ticker.tick(this, entry.getKey(), blockState, blockEntity);
                    }
                } catch (Exception e) {
                    LDLib.LOGGER.error("error while update DummyWorld tick, pos {} type {}", entry.getKey(), blockEntity.getType(), e);
                }
            }
        }
    }

    public List<Entity> geAllEntities() {
        var entities = new ArrayList<>(this.entities.values());
        if (proxyWorld instanceof TrackedDummyWorld trackedDummyWorld)
            entities.addAll(trackedDummyWorld.geAllEntities());
        return entities;
    }
}
