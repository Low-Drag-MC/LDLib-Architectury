package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BlockSelectorWidget extends WidgetGroup {
    private Consumer<BlockState> onBlockStateUpdate;
    private Block block;
    private final IItemHandlerModifiable handler;
    private final TextFieldWidget blockField;
    private final Map<Property, Comparable> properties;

    public BlockSelectorWidget(int x, int y, int width, boolean isState) {
        super(x, y, width, 20);
        setClientSideWidget();
        properties = new HashMap<>();
        blockField = (TextFieldWidget) new TextFieldWidget(22, 0, width - (isState ?  46 : 26), 20, null, s -> {
            if (s != null && !s.isEmpty()) {
                Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(s));
                if (this.block != block) {
                    this.block = block;
                    onUpdate();
                }
            }
        }).setResourceLocationOnly().setHoverTooltips("ldlib.gui.tips.block_selector");

        addWidget(new PhantomSlotWidget(handler = new ItemStackHandler(1), 0, 1, 1)
                .setClearSlotOnRightClick(true)
                .setChangeListener(() -> {
                    ItemStack stack = handler.getStackInSlot(0);
                    if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem itemBlock)) {
                        var fluidTransfer = FluidTransferHelper.getFluidTransfer(handler, 0);
                        if (fluidTransfer != null) {
                            if (fluidTransfer.getTanks() > 0) {
                                var fluid = fluidTransfer.getFluidInTank(0).getFluid();
                                setBlock(fluid.defaultFluidState().createLegacyBlock());
                                onUpdate();
                                return;
                            }
                        }
                        if (block != null){
                            setBlock(null);
                            onUpdate();
                        }
                    } else {
                        setBlock(itemBlock.getBlock().defaultBlockState());
                        onUpdate();
                    }
                }).setBackgroundTexture(new ColorBorderTexture(1, -1)));
        addWidget(blockField);
        if (isState) {
            addWidget(new ButtonWidget(width - 21, 0, 20, 20, new ItemStackTexture(Items.BLACK_WOOL, Items.WHITE_WOOL, Items.BLUE_WOOL, Items.GREEN_WOOL, Items.YELLOW_WOOL), cd -> {
                DraggableScrollableWidgetGroup group;
                new DialogWidget(getGui().mainGroup, isClientSideWidget)
                        .setOnClosed(this::onUpdate)
                        .addWidget(group = new DraggableScrollableWidgetGroup(0, 0, getGui().mainGroup.getSize().width, getGui().mainGroup.getSize().height)
                                .setYScrollBarWidth(4).setYBarStyle(null, new ColorRectTexture(-1))
                                .setXScrollBarHeight(4).setXBarStyle(null, new ColorRectTexture(-1))
                                .setBackground(new ColorRectTexture(0x8f222222)));
                int i = properties.size() - 1;
                for (Map.Entry<Property, Comparable> entry : properties.entrySet()) {
                    Property property = entry.getKey();
                    Comparable value = entry.getValue();
                    group.addWidget(new SelectorWidget(3, 3 + i * 20, 100, 15, property.getPossibleValues().stream().map(v -> property.getName((Comparable) v)).toList(), -1)
                            .setValue(property.getName(value))
                            .setOnChanged(newValue -> properties.put(property, (Comparable) property.getValue(newValue).get()))
                            .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                            .setBackground(new ColorRectTexture(0xffaaaaaa)));
                    group.addWidget(new LabelWidget(105, 6 + i * 20, property.getName()));
                    i--;
                }
            }).setHoverBorderTexture(1, -1).setHoverTooltips("ldlib.gui.tips.block_meta"));
        }
    }

    public BlockState getBlock() {
        BlockState state;
        if (block == null) {
            state = null;
        } else {
            state = block.defaultBlockState();
            for (Map.Entry<Property, Comparable> entry : properties.entrySet()) {
                state = state.setValue(entry.getKey(), entry.getValue());
            }
        }
        return state;
    }

    public BlockSelectorWidget setBlock(BlockState blockState) {
        properties.clear();
        if (blockState == null) {
            block = null;
            handler.setStackInSlot(0, ItemStack.EMPTY);
            blockField.setCurrentString("");
        } else {
            block = blockState.getBlock();
            new ItemStack(block);
            handler.setStackInSlot(0, new ItemStack(block));
            blockField.setCurrentString(BuiltInRegistries.BLOCK.getKey(block));
            for (Property<?> property : blockState.getBlock().getStateDefinition().getProperties()) {
                properties.put(property, blockState.getValue(property));
            }
        }
        return this;
    }

    public BlockSelectorWidget setOnBlockStateUpdate(Consumer<BlockState> onBlockStateUpdate) {
        this.onBlockStateUpdate = onBlockStateUpdate;
        return this;
    }

    private void onUpdate() {
        handler.setStackInSlot(0, block == null ? ItemStack.EMPTY : new ItemStack(block));
        if (onBlockStateUpdate != null) {
            onBlockStateUpdate.accept(getBlock());
        }
    }
}
