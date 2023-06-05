package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.client.scene.ParticleManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
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

    public final Vector3 minPos = new Vector3(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    public final Vector3 maxPos = new Vector3(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

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
        minPos.x = (Math.min(minPos.x, pos.getX()));
        minPos.y = (Math.min(minPos.y, pos.getY()));
        minPos.z = (Math.min(minPos.z, pos.getZ()));
        maxPos.x = (Math.max(maxPos.x, pos.getX()));
        maxPos.y = (Math.max(maxPos.y, pos.getY()));
        maxPos.z = (Math.max(maxPos.z, pos.getZ()));
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
        renderedBlocks.put(pos, BlockInfo.fromBlockState(state));
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

    public Vector3 getSize() {
        return new Vector3(maxPos.x - minPos.x + 1, maxPos.y - minPos.y + 1, maxPos.z - minPos.z + 1);
    }

    public Vector3 getMinPos() {
        return minPos;
    }

    public Vector3 getMaxPos() {
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
}
