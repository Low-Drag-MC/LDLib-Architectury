package com.lowdragmc.lowdraglib.client.fabric;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote ClientCommands
 */
@Environment(EnvType.CLIENT)
public class ClientCommandsImpl {

    public static LiteralArgumentBuilder createLiteral(String command) {
        return ClientCommandManager.literal(command);
    }

}
