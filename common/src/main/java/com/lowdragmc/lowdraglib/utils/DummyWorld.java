package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.scene.ParticleManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

/**
 * Author: KilaBash
 * Date: 2022/04/26
 * Description:
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DummyWorld extends Level {

    protected DummyChunkSource chunkProvider = new DummyChunkSource(this);

    public DummyWorld(Level level) {
        super((WritableLevelData) level.getLevelData(), level.dimension(), level.dimensionTypeRegistration(), level::getProfiler,
                true, false, 0, 0);
    }

    @Override
    public boolean setBlock(BlockPos pPos, BlockState pState, int pFlags, int pRecursionLeft) {
        return false;
    }

    @Override
    public void setBlockEntity(BlockEntity pBlockEntity) {
    }

    @Override
    public BlockState getBlockState(BlockPos pPos) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public void playSound(@Nullable Player pPlayer,
                          double pX, double pY, double pZ, SoundEvent pSound,
                          SoundSource pCategory, float pVolume, float pPitch) {

    }

    @Override
    public void playSound(@Nullable Player pPlayer,
                          Entity pEntity, SoundEvent pEvent,
                          SoundSource pCategory, float pVolume, float pPitch) {

    }

    @Override
    public String gatherChunkSourceStats() {
        return "";
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pPos) {
        return null;
    }

    @Override
    public float getShade(Direction direction, boolean b) {
        switch (direction) {
            case DOWN:
            case UP:
                return 0.9F;
            case NORTH:
            case SOUTH:
                return 0.8F;
            case WEST:
            case EAST:
                return 0.6F;
            default:
                return 1.0F;
        }
    }

    @Override
    public LevelLightEngine getLightEngine() {
        if (LDLib.isClient()) {
            return Minecraft.getInstance().level.getLightEngine();
        }
        return null;
    }

    @Override
    public Holder<Biome> getBiome(BlockPos pPos) {
        return super.getBiome(pPos.offset(Vec3i.ZERO));
    }

    @Override
    public int getBrightness(LightLayer pLightType, BlockPos pBlockPos) {
        return 15;
    }

    @Override
    public int getRawBrightness(@Nonnull BlockPos pos, int p_226659_2_) {
        return 15;
    }

    @Override
    public boolean canSeeSky(@Nonnull BlockPos pos) {
        return true;
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        return Biomes.bootstrap(BuiltinRegistries.BIOME);
    }


    @Override
    public Entity getEntity(int id) {
        return null;
    }

    @Override
    public MapItemSavedData getMapData(String mapName) {
        return null;
    }

    @Override
    public void setMapData(String pMapId, MapItemSavedData pData) {

    }

    @Override
    public int getFreeMapId() {
        return 0;
    }

    @Override
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public RecipeManager getRecipeManager() {
        return null;
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return null;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return null;
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return null;
    }

    @Override
    public boolean isLoaded(BlockPos p_195588_1_) {
        return true;
    }

    @Override
    public ChunkSource getChunkSource() {
        return chunkProvider;
    }

    @Override
    public void levelEvent(@Nullable Player pPlayer, int pType, BlockPos pPos, int pData) {

    }

    @Override
    public void gameEvent(GameEvent event, Vec3 position, GameEvent.Context context) {

    }

    @Override
    public void gameEvent(@Nullable Entity pEntity, GameEvent pEvent, BlockPos pPos) {

    }

    @Override
    public RegistryAccess registryAccess() {
        return null;
    }

    @Override
    public List<? extends Player> players() {
        return Collections.emptyList();
    }

    @Override
    public FluidState getFluidState(BlockPos pPos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public void playSeededSound(@org.jetbrains.annotations.Nullable Player player, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, long seed) {

    }

    @Override
    public void playSeededSound(@org.jetbrains.annotations.Nullable Player player, Entity entity, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, long seed) {

    }

    @Environment(EnvType.CLIENT)
    private ParticleManager particleManager;

    @Environment(EnvType.CLIENT)
    public void setParticleManager(ParticleManager particleManager) {
        this.particleManager = particleManager;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public ParticleManager getParticleManager() {
        return particleManager;
    }
}
