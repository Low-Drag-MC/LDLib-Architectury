package com.lowdragmc.lowdraglib.pipelike;

import com.lowdragmc.lowdraglib.LDLib;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This is a helper class to get information about a pipe net
 * <p>The walker is written that it will always find the shortest path to any destination
 * <p>On the way it can collect information about the pipes and it's neighbours
 * <p>After creating a walker simply call {@link #traversePipeNet()} to start walking, then you can just collect the data
 * <p><b>Do not walk a walker more than once</b>
 */
public abstract class PipeNetWalker<NodeDataType, Net extends PipeNet<NodeDataType>> {
    protected PipeNetWalker<NodeDataType, Net> root;
    protected final Net pipeNet;
    protected final Set<Long> walked = new HashSet<>();
    protected final List<Direction> pipes = new ArrayList<>();
    protected List<PipeNetWalker<NodeDataType, Net>> walkers;
    protected final BlockPos.MutableBlockPos currentPos;
    @Getter
    private int walkedBlocks;
    @Getter
    private boolean invalid;
    private boolean running;

    protected PipeNetWalker(Net pipeNet, BlockPos sourcePipe, int walkedBlocks) {
        this.pipeNet = pipeNet;
        this.walkedBlocks = walkedBlocks;
        this.currentPos = sourcePipe.mutable();
        this.root = this;
    }

    /**
     * Creates a sub walker
     * Will be called when a pipe has multiple valid pipes
     *
     * @param pipeNet      pipe net
     * @param nextPos      next pos to check
     * @param walkedBlocks distance from source in blocks
     * @return new sub walker
     */
    @Nonnull
    protected abstract PipeNetWalker<NodeDataType, Net> createSubWalker(Net pipeNet, BlockPos nextPos, int walkedBlocks);

    /**
     * Checks the neighbour of the current pos
     *
     * @param pipePos         current pos. Note!! its a mutable pos.
     * @param faceToNeighbour face to neighbour
     * @param pipeNode        pipeNode
     */
    protected void checkNeighbour(Node<NodeDataType> pipeNode, BlockPos pipePos, Direction faceToNeighbour) {

    }

    /**
     * You can increase walking stats here. for example
     *
     * @param pipeNode current checking pipe
     * @param pos      current pipe pos
     * @return should keep walking on this path
     */
    protected boolean checkPipe(Node<NodeDataType> pipeNode, BlockPos pos) {
        return true;
    }

    /**
     * Called when a sub walker is done walking
     *
     * @param subWalker the finished sub walker
     */
    protected void onRemoveSubWalker(PipeNetWalker<NodeDataType, Net> subWalker) {
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
        while (running && !walk() && i++ < maxWalks);
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
                var walker = createSubWalker(pipeNet, currentPos.relative(side), walkedBlocks + 1);
                walker.root = root;
                walkers.add(walker);
            }
        }

        Iterator<PipeNetWalker<NodeDataType, Net>> iterator = walkers.iterator();
        while (iterator.hasNext()) {
            PipeNetWalker<NodeDataType, Net> walker = iterator.next();
            if (walker.walk()) {
                onRemoveSubWalker(walker);
                iterator.remove();
            }
        }

        return !isRunning() || walkers.size() == 0;
    }

    private void checkPos() {
        pipes.clear();

        var pipeNode = pipeNet.getNodeAt(currentPos);

        if (pipeNode != null) {
            if (!checkPipe(pipeNet.getNodeAt(currentPos), currentPos)) {
                return;
            }

            root.walked.add(currentPos.asLong());

            // check for surrounding for next walk
            for (Direction accessSide : Direction.values()) {

                // is walked.
                if (isWalked(currentPos.relative(accessSide)) || pipeNode.isBlocked(accessSide)) {
                    continue;
                }

                // if neighbour is a connected node.
                if (pipeNet.isNodeConnectedTo(currentPos, accessSide)) {
                    pipes.add(accessSide);
                    continue;
                }

                checkNeighbour(pipeNode, currentPos, accessSide);
            }
        }
    }

    protected boolean isWalked(BlockPos pos) {
        return root.walked.contains(pos.asLong());
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

    public ServerLevel getLevel() {
        return pipeNet.getLevel();
    }

    public BlockPos getCurrentPos() {
        return currentPos.immutable();
    }

}
