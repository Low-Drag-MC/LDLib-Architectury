package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.client.scene.ParticleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: KilaBash
 * Date: 2022/04/26
 * Description:
 */
@OnlyIn(Dist.CLIENT)
public class FacadeBlockWorld extends DummyWorld {

    public final Level world;
    public final BlockPos pos;
    public final BlockState state;
    public final BlockEntity tile;

    public FacadeBlockWorld(Level world, BlockPos pos, BlockState state, BlockEntity tile) {
        super(world);
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.tile = tile;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(@Nonnull BlockPos pPos) {
        return world == null ? super.getBlockEntity(pPos) : pPos.equals(pos) ? tile : world.getBlockEntity(pPos);
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public BlockState getBlockState(BlockPos pPos) {
        return  world == null ? super.getBlockState(pPos) : pPos.equals(pos) ? state : world.getBlockState(pPos);
    }

    @Override
    @Nonnull
    public LevelLightEngine getLightEngine() {
        return world == null ? super.getLightEngine() : world.getLightEngine();
    }

    @Override
    public int getBrightness(@Nonnull LightLayer lightType, @Nonnull BlockPos pos) {
        return  world == null ? super.getBrightness(lightType, pos) : world.getBrightness(lightType, pos);
    }

    @Override
    public int getBlockTint(@Nonnull BlockPos blockPos, @Nonnull ColorResolver colorResolver) {
        return  world == null ? super.getBlockTint(blockPos, colorResolver) : world.getBlockTint(blockPos, colorResolver);
    }

    @Override
    public boolean canSeeSky(@Nonnull BlockPos pos) {
        return  world == null ? super.canSeeSky(pos) : world.canSeeSky(pos);
    }

    @Nonnull
    @Override
    public DimensionType dimensionType() {
        return  world == null ? super.dimensionType() : world.dimensionType();
    }

    @Override
    public boolean isEmptyBlock(@Nonnull BlockPos pPos) {
        return  world == null ? super.isEmptyBlock(pPos) : !pPos.equals(pos) && world.isEmptyBlock(pPos);
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public Holder<Biome> getBiome(@Nonnull BlockPos pos) {
        return  world == null ? super.getBiome(pos) : world.getBiome(pos);
    }

//    @Override
//    public @Nullable BlockEntity getExistingBlockEntity(BlockPos pos) {
//        return  world == null ? super.getExistingBlockEntity(pos) : pos.equals(this.pos) ? tile : world.getExistingBlockEntity(pos);
//    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setParticleManager(@Nonnull ParticleManager particleManager) {
        super.setParticleManager(particleManager);
        if (world instanceof DummyWorld dummyWorld) {
            dummyWorld.setParticleManager(particleManager);
        }
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public ParticleManager getParticleManager() {
        ParticleManager particleManager = super.getParticleManager();
        if (particleManager == null && world instanceof DummyWorld dummyWorld) {
            return dummyWorld.getParticleManager();
        }
        return particleManager;
    }
}
