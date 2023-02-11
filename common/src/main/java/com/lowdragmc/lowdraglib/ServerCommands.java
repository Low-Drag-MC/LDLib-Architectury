package com.lowdragmc.lowdraglib;

import com.lowdragmc.lowdraglib.gui.factory.UIEditorFactory;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

/**
 * @author KilaBash
 * @date 2023/2/9
 * @implNote ServerCommands
 */
public class ServerCommands {
    public static List<LiteralArgumentBuilder<CommandSourceStack>> createServerCommands() {
        return List.of(
                Commands.literal("ldlib")
                        .then(Commands.literal("ui_editor")
                                .executes(context -> {
                                    UIEditorFactory.INSTANCE.openUI(UIEditorFactory.INSTANCE, context.getSource().getPlayerOrException());
                                    return 1;
                                })
                        )
        );
    }
}
