package com.lowdragmc.lowdraglib.syncdata.rpc;

import net.minecraft.world.entity.player.Player;

public interface RPCSender {
    boolean isServer();

    record ClientRPCSender(Player player) implements RPCSender {
        @Override
        public boolean isServer() {
            return false;
        }
    }

    static RPCSender ofClient(Player player) {
        return new ClientRPCSender(player);
    }

    static RPCSender ofServer() {
        return () -> true;
    }


}
