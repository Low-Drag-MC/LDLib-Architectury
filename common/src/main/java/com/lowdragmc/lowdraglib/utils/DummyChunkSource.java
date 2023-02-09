package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.LDLib;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class DummyChunkSource extends ChunkSource {

    private final Level world;

    public DummyChunkSource(Level world) {
        this.world = world;
    }

    @Override
    public void tick(BooleanSupplier p_202162_, boolean p_202163_) {

    }

    @Nullable
    @Override
    public ChunkAccess getChunk(int pChunkX, int pChunkZ, ChunkStatus pRequiredStatus, boolean pLoad) {
        return null;
    }

    @Override
    @Nonnull
    public String gatherStats() {
        return "Dummy";
    }

    @Override
    public int getLoadedChunksCount() {
        return 0;
    }

    @Override
    @Nonnull
    public LevelLightEngine getLightEngine() {
        if (LDLib.isClient()) {
            return Minecraft.getInstance().level.getLightEngine();
        }
        return null;
    }

    @Override
    @Nonnull
    public BlockGetter getLevel() {
        return world;
    }

}
