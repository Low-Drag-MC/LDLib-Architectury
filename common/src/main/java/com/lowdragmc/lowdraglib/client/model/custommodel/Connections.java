package com.lowdragmc.lowdraglib.client.model.custommodel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author KilaBash
 * @date 2023/3/24
 * @implNote Connections
 */
public class Connections {
    byte connections = 0;

    private Connections(Connection... connections) {
        for (Connection connection : connections) {
            add(connection);
        }
    }

    public static Connections of(Connection... connections) {
       return new Connections(connections);
    }

    public boolean contains(Connection connection) {
        return (connections & (1 << (connection.ordinal()))) > 0;
    }

    public void add(Connection connection) {
        this.connections |= 1 << (connection.ordinal());
    }

    public boolean isEmpty() {
        return connections == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connections that = (Connections) o;

        return connections == that.connections;
    }

    @Override
    public int hashCode() {
        return connections;
    }

    public static Connections checkConnections(BlockAndTintGetter level, BlockPos pos, @Nonnull BlockState state, @Nullable Direction side) {
        Connections connections = Connections.of();
        if (side != null) {
            pos = pos.immutable();
            for (var connection : Connection.values()) {
                var offset = connection.transform(pos, side);
                var adjacent = level.getBlockState(offset);
                if (ICTMPredicate.getPredicate(state).isConnected(level, state, pos, adjacent, offset, side)) {
                    connections.add(connection);
                }
            }
        }
        return connections;
    }
}
