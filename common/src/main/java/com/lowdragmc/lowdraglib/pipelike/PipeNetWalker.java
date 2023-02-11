package com.lowdragmc.lowdraglib.pipelike;


import com.lowdragmc.lowdraglib.LDLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This is a helper class to get information about a pipe net
 * <p>The walker is written that it will always find the shortest path to any destination
 * <p>On the way it can collect information about the pipes and it's neighbours
 * <p>After creating a walker simply call {@link #traversePipeNet()} to start walking, then you can just collect the data
 * <p><b>Do not walk a walker more than once</b>
 */
public abstract class PipeNetWalker {

    private PipeNetWalker root;
    private final Level level;
    private final Set<Long> walked = new HashSet<>();
    private final List<Direction> pipes = new ArrayList<>();
    private List<PipeNetWalker> walkers;
    private final BlockPos.MutableBlockPos currentPos;
    private int walkedBlocks;
    private boolean invalid;
    private boolean running;

    protected PipeNetWalker(Level Level, BlockPos sourcePipe, int walkedBlocks) {
        this.level = Objects.requireNonNull(Level);
        this.walkedBlocks = walkedBlocks;
        this.currentPos = sourcePipe.mutable();
        this.root = this;
    }

    /**
     * Creates a sub walker
     * Will be called when a pipe has multiple valid pipes
     *
     * @param Level        Level
     * @param nextPos      next pos to check
     * @param walkedBlocks distance from source in blocks
     * @return new sub walker
     */
    protected abstract PipeNetWalker createSubWalker(Level Level, BlockPos nextPos, int walkedBlocks);

    /**
     * You can increase walking stats here. for example
     *
     * @param pipeTile current checking pipe
     * @param pos      current pipe pos
     */
    protected abstract void checkPipe(BlockEntity pipeTile, BlockPos pos);

    /**
     * Checks the neighbour of the current pos
     *
     * @param pipePos         current pos
     * @param faceToNeighbour face to neighbour
     * @param neighbourTile   neighbour tile
     */
    protected abstract void checkNeighbour(BlockEntity pipeTile, BlockPos pipePos, Direction faceToNeighbour, @Nullable BlockEntity neighbourTile);

    /**
     * If the pipe is valid to perform a walk on
     *
     * @param currentPipe     current pipe
     * @param neighbourPipe   neighbour pipe to check
     * @param pipePos         current pos (tile.getPipePos() != pipePos)
     * @param faceToNeighbour face to pipeTile
     * @return if the pipe is valid
     */
    protected abstract boolean isValidPipe(BlockEntity currentPipe, BlockEntity neighbourPipe, BlockPos pipePos, Direction faceToNeighbour);

    /**
     * If the pipe is connected to the side
     * @param currentPipe current pipe
     * @param side face to neighbour
     * @return if is connected
     */
    protected abstract boolean isConnected(BlockEntity currentPipe, Direction side);

    /**
     * Called when a sub walker is done walking
     *
     * @param subWalker the finished sub walker
     */
    protected void onRemoveSubWalker(PipeNetWalker subWalker) {
    }

    public void traversePipeNet() {
        traversePipeNet(32768);
    }

    /**
     * Starts walking the pipe net and gathers information.
     *
     * @param maxWalks max walks to prevent possible stack overflow
     * @throws IllegalStateException if the walker already walked
     */
    public void traversePipeNet(int maxWalks) {
        if (invalid)
            throw new IllegalStateException("This walker already walked. Create a new one if you want to walk again");
        int i = 0;
        running = true;
        while (running && !walk() && i++ < maxWalks) ;
        running = false;
        root.walked.clear();
        if (i >= maxWalks)
            LDLib.LOGGER.warn("The walker reached the maximum amount of walks {}", i);
        invalid = true;
    }

    private boolean walk() {
        if (walkers == null) {
            checkPos();

            if (pipes.size() == 0)
                return true;
            if (pipes.size() == 1) {
                currentPos.move(pipes.get(0));
                walkedBlocks++;
                return !isRunning();
            }

            walkers = new ArrayList<>();
            for (Direction side : pipes) {
                PipeNetWalker walker = Objects.requireNonNull(createSubWalker(level, currentPos.relative(side), walkedBlocks + 1), "Walker can't be null");
                walker.root = root;
                walkers.add(walker);
            }
        }
        Iterator<PipeNetWalker> iterator = walkers.iterator();
        while (iterator.hasNext()) {
            PipeNetWalker walker = iterator.next();
            if (walker.walk()) {
                onRemoveSubWalker(walker);
                iterator.remove();
            }
        }

        return !isRunning() || walkers.size() == 0;
    }

    private void checkPos() {
        pipes.clear();
        BlockEntity pipeTile = level.getBlockEntity(currentPos);
        if (pipeTile == null) {
            if (walkedBlocks == 1) {
                // if it is the first block, it wasn't already checked
                LDLib.LOGGER.warn("First PipeTile is null during walk");
                return;
            } else
                throw new IllegalStateException("PipeTile was not null last walk, but now is");
        }
        checkPipe(pipeTile, currentPos);
        root.walked.add(pipeTile.getBlockPos().asLong());

        // check for surrounding pipes and item handlers
        for (Direction accessSide : Direction.values()) {
            //skip sides reported as blocked by pipe network
            BlockEntity otherPipe = level.getBlockEntity(currentPos.immutable().relative(accessSide));
            if (!isConnected(pipeTile, accessSide) || isWalked(otherPipe))
                continue;

            if (isValidPipe(pipeTile, otherPipe, currentPos, accessSide)) {
                pipes.add(accessSide);
                continue;
            }
            checkNeighbour(pipeTile, currentPos, accessSide, otherPipe);
        }
    }

    protected boolean isWalked(BlockEntity pipe) {
        return root.walked.contains(pipe.getBlockPos().asLong());
    }

    /**
     * Will cause the root walker to stop after the next walk
     */
    public void stop() {
        root.running = false;
    }

    public boolean isRunning() {
        return root.running;
    }

    public Level getWorld() {
        return level;
    }

    public BlockPos getCurrentPos() {
        return currentPos;
    }

    public int getWalkedBlocks() {
        return walkedBlocks;
    }

    public boolean isRoot() {
        return this.root == this;
    }
}
