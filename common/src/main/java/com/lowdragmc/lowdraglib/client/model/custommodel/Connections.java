package com.lowdragmc.lowdraglib.client.model.custommodel;

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

}
