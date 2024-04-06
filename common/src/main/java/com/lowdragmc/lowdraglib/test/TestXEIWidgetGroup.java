package com.lowdragmc.lowdraglib.test;

import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.lowdraglib.misc.ItemStackTransfer;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.utils.TagOrCycleFluidTransfer;
import com.lowdragmc.lowdraglib.utils.TagOrCycleItemStackTransfer;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class TestXEIWidgetGroup extends WidgetGroup {
    public TestXEIWidgetGroup() {
        super(0, 0, 170, 60);
        setClientSideWidget();
        var input1 = new SlotWidget(new ItemStackTransfer(new ItemStack(Items.APPLE, 10)), 0, 20, 20, false, false)
                .setBackgroundTexture(SlotWidget.ITEM_SLOT_TEXTURE)
                .setIngredientIO(IngredientIO.INPUT);
        List<Either<List<Pair<TagKey<Item>, Integer>>, List<ItemStack>>> itemsList = List.of(Either.left(List.of(Pair.of(ItemTags.DIRT, 5))));
        var input2 = new SlotWidget(new TagOrCycleItemStackTransfer(itemsList), 0, 20, 0, false, false)
                .setBackgroundTexture(SlotWidget.ITEM_SLOT_TEXTURE)
                .setIngredientIO(IngredientIO.INPUT);
        var output = new SlotWidget(new ItemStackTransfer(new ItemStack(Items.DIAMOND, 23)), 0, 130, 20, false, false)
                .setBackgroundTexture(SlotWidget.ITEM_SLOT_TEXTURE)
                .setIngredientIO(IngredientIO.OUTPUT);

        var both = new SlotWidget(new ItemStackTransfer(new ItemStack(Items.ANDESITE, 23)), 0, 60, 20, false, false)
                .setBackgroundTexture(SlotWidget.ITEM_SLOT_TEXTURE)
                .setIngredientIO(IngredientIO.BOTH);

        var inputFluid = new TankWidget(new FluidStorage(FluidStack.create(Fluids.WATER, 1000)), 20, 40, 20, 20, false, false)
                .setBackground(TankWidget.FLUID_SLOT_TEXTURE)
                .setIngredientIO(IngredientIO.INPUT);
        var outputFluid = new TankWidget(new FluidStorage(FluidStack.create(Fluids.LAVA, 1000)), 130, 40, 20, 20, false, false)
                .setBackground(TankWidget.FLUID_SLOT_TEXTURE)
                .setIngredientIO(IngredientIO.OUTPUT)
                .setXEIChance(0.01f);

        List<Either<List<Pair<TagKey<Fluid>, Long>>, List<FluidStack>>> fluidList = List.of(Either.left(List.of(Pair.of(FluidTags.LAVA, 10000L))));
        var catalystFluid = new TankWidget(new TagOrCycleFluidTransfer(fluidList), 0, 110, 40, 20, 20, false, false)
                .setBackground(TankWidget.FLUID_SLOT_TEXTURE)
                .setIngredientIO(IngredientIO.CATALYST)
                .setXEIChance(0.01f);
        addWidget(input1);
        addWidget(input2);
        addWidget(output);
        addWidget(both);
        addWidget(inputFluid);
        addWidget(outputFluid);
        addWidget(catalystFluid);
    }
}
