package com.lowdragmc.lowdraglib;

import com.lowdragmc.lowdraglib.gui.factory.UIEditorFactory;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

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
                        .then(Commands.literal("copy_block_tag")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            var pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
                                            var world = context.getSource().getLevel();
                                            var blockEntity = world.getBlockEntity(pos);
                                            if (blockEntity != null) {
                                                var tag = blockEntity.saveWithoutMetadata();
                                                var value = NbtUtils.structureToSnbt(tag);
                                                context.getSource().sendSuccess(Component.literal("[Copy to clipboard]").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value))).append(NbtUtils.toPrettyComponent(tag)), true);
                                            } else {
                                                context.getSource().sendSuccess(Component.literal("No block entity at " + pos).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
                                            }
                                            return 1;
                                        }))
                        )
                        .then(Commands.literal("copy_entity_tag")
                                .then(Commands.argument("entity", EntityArgument.entity())
                                        .executes(context -> {
                                            var entity = EntityArgument.getEntity(context, "entity");
                                            var tag = entity.saveWithoutId(new CompoundTag());
                                            var value = NbtUtils.structureToSnbt(tag);
                                            context.getSource().sendSuccess(() -> Component.literal("[Copy to clipboard]").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value))).append(NbtUtils.toPrettyComponent(tag)), true);
                                            return 1;
                                        }))
                        )
        );
    }
}
