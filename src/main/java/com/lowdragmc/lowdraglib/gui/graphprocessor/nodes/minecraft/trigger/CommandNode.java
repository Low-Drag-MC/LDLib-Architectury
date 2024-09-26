package com.lowdragmc.lowdraglib.gui.graphprocessor.nodes.minecraft.trigger;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.graphprocessor.annotation.InputPort;
import com.lowdragmc.lowdraglib.gui.graphprocessor.data.trigger.LinearTriggerNode;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@LDLRegister(name = "command", group = "graph_processor.node.minecraft")
public class CommandNode extends LinearTriggerNode {
    @InputPort
    public String command;
    @InputPort
    public Level level;
    @InputPort
    public Vector3f xyz;

    @Override
    public int getMinWidth() {
        return 100;
    }

    @Override
    public void process() {
        if (level instanceof ServerLevel serverLevel) {
            performCommand(serverLevel);
        }
    }

    public void performCommand(ServerLevel serverLevel) {
        if (this.command != null) {
            var minecraftServer = serverLevel.getServer();
            if (minecraftServer.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
                try {
                    var commandSourceStack = new CommandSourceStack(
                            CommandSource.NULL,
                            xyz == null ? Vec3.atLowerCornerOf(serverLevel.getSharedSpawnPos()) : new Vec3(xyz.x, xyz.y, xyz.z),
                            Vec2.ZERO, serverLevel, 5,
                            "Server", Component.literal("Server"),
                            minecraftServer, null);
                    minecraftServer.getCommands().performPrefixedCommand(commandSourceStack, this.command);
                } catch (Throwable var6) {
                    LDLib.LOGGER.error("Error while executing command: {}", this.command, var6);
                }
            }
        }
    }

}
