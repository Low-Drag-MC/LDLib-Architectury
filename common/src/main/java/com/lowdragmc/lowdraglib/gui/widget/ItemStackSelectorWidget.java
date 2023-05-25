package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.misc.ItemStackTransfer;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Consumer;

public class ItemStackSelectorWidget extends WidgetGroup {
    private Consumer<ItemStack> onItemStackUpdate;
    private final IItemTransfer handler;
    private final TextFieldWidget itemField;
    private ItemStack item = ItemStack.EMPTY;

    public ItemStackSelectorWidget(int x, int y, int width) {
        super(x, y, width, 20);
        setClientSideWidget();
        itemField = (TextFieldWidget) new TextFieldWidget(22, 0, width - 46, 20, null, s -> {
            if (s != null && !s.isEmpty()) {
                Item item = Registry.ITEM.get(new ResourceLocation(s));
                if (!ItemStack.isSameItemSameTags(item.getDefaultInstance(), this.item)) {
                    this.item = item.getDefaultInstance();
                    onUpdate();
                }
            }
        }).setResourceLocationOnly().setHoverTooltips("ldlib.gui.tips.item_selector");

        addWidget(new PhantomSlotWidget(handler = new ItemStackTransfer(1), 0, 1, 1)
                .setClearSlotOnRightClick(true)
                .setChangeListener(() -> {
                    setItemStack(handler.getStackInSlot(0));
                    onUpdate();
                }).setBackgroundTexture(new ColorBorderTexture(1, -1)));
        addWidget(itemField);

        addWidget(new ButtonWidget(width - 21, 0, 20, 20, null, cd -> {
            if (item.isEmpty()) return;
            TextFieldWidget nbtField;
            new DialogWidget(getGui().mainGroup, isClientSideWidget)
                    .setOnClosed(this::onUpdate)
                    .addWidget(nbtField = new TextFieldWidget(10, 10, getGui().mainGroup.getSize().width - 50, 20, null, s -> {
                        try {
                            item.setTag(TagParser.parseTag(s));
                            onUpdate();
                        } catch (CommandSyntaxException ignored) {

                        }
                    }));
            if (item.hasTag()) {
                nbtField.setCurrentString(item.getTag().toString());
            }
        })
                .setButtonTexture(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("NBT", -1).setDropShadow(true))
                .setHoverBorderTexture(1, -1).setHoverTooltips("ldlib.gui.tips.item_tag"));
    }

    public ItemStack getItemStack() {
        return item;
    }

    public ItemStackSelectorWidget setItemStack(ItemStack itemStack) {
        item = Objects.requireNonNullElse(itemStack, ItemStack.EMPTY).copy();
        handler.setStackInSlot(0, item);
        itemField.setCurrentString(Registry.ITEM.getKey(item.getItem()).toString());
        return this;
    }

    public ItemStackSelectorWidget setOnItemStackUpdate(Consumer<ItemStack> onItemStackUpdate) {
        this.onItemStackUpdate = onItemStackUpdate;
        return this;
    }

    private void onUpdate() {
        handler.setStackInSlot(0, item);
        if (onItemStackUpdate != null) {
            onItemStackUpdate.accept(item);
        }
    }
}
