package com.lowdragmc.lowdraglib.client;

import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.client.shader.management.ShaderManager;
import com.lowdragmc.lowdraglib.gui.compass.CompassManager;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.commands.Commands;

import java.util.List;

/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote ClientCommands
 */
@Environment(EnvType.CLIENT)
public class ClientCommands {

    @ExpectPlatform
    public static LiteralArgumentBuilder createLiteral(String command) {
        throw new AssertionError();
    }

    @SuppressWarnings("unchecked")
    public static <S> List<LiteralArgumentBuilder<S>> createClientCommands() {
        return List.of(
                (LiteralArgumentBuilder<S>) createLiteral("ldlib_client").then(createLiteral("reload_shader")
                        .executes(context -> {
                            Shaders.reload();
                            ShaderManager.getInstance().reload();
                            return 1;
                        })),
                (LiteralArgumentBuilder<S>) createLiteral("compass").then(createLiteral("dev_mode")
                        .then(Commands.argument("mode", BoolArgumentType.bool())
                                .executes(context -> {
                                    CompassManager.INSTANCE.devMode = BoolArgumentType.getBool(context, "mode");
                                    return 1;
                                })))
        );
    }
}
