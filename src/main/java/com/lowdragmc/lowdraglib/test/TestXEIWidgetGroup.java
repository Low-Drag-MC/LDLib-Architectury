package com.lowdragmc.lowdraglib.test;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

public class TestXEIWidgetGroup extends WidgetGroup {
    public TestXEIWidgetGroup() {
        super(0, 0, 170, 60);
        setClientSideWidget();
        var input = new SlotWidget(new ItemStackHandler(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.APPLE, 10))), 0, 20, 20, false, false)
                .setBackgroundTexture(new ResourceTexture("ldlib:textures/gui/slot.png"))
                .setIngredientIO(IngredientIO.INPUT);
        var output = new SlotWidget(new ItemStackHandler(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.DIAMOND, 23))), 0, 130, 20, false, false)
                .setBackgroundTexture(new ResourceTexture("ldlib:textures/gui/slot.png"))
                .setIngredientIO(IngredientIO.OUTPUT);

        var both = new SlotWidget(new ItemStackHandler(NonNullList.of(ItemStack.EMPTY, new ItemStack(Items.ANDESITE, 23))), 0, 60, 20, false, false)
                .setBackgroundTexture(new ResourceTexture("ldlib:textures/gui/slot.png"))
                .setIngredientIO(IngredientIO.BOTH);

        FluidTank inputTank = new FluidTank(1000);
        inputTank.setFluid(new FluidStack(Fluids.WATER, 1000));
        var inputFluid = new TankWidget(inputTank, 20, 40, 20, 20, false, false)
                .setBackground(new ResourceTexture("ldlib:textures/gui/fluid_slot.png"))
                .setIngredientIO(IngredientIO.INPUT);

        FluidTank outputTank = new FluidTank(1000);
        outputTank.setFluid(new FluidStack(Fluids.LAVA, 1000));
        var outputFluid = new TankWidget(outputTank, 130, 40, 20, 20, false, false)
                .setBackground(new ResourceTexture("ldlib:textures/gui/fluid_slot.png"))
                .setIngredientIO(IngredientIO.OUTPUT);
        addWidget(input);
        addWidget(output);
        addWidget(both);
        addWidget(inputFluid);
        addWidget(outputFluid);
    }
}
