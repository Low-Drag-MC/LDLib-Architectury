package com.lowdragmc.lowdraglib.test;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.lowdraglib.misc.ItemStackTransfer;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

public class TestXEIWidgetGroup extends WidgetGroup {
    public TestXEIWidgetGroup() {
        super(0, 0, 170, 60);
        setClientSideWidget();
        var input = new SlotWidget(new ItemStackTransfer(new ItemStack(Items.APPLE, 10)), 0, 20, 20, false, false)
                .setBackgroundTexture(new ResourceTexture("ldlib:textures/gui/slot.png"))
                .setIngredientIO(IngredientIO.INPUT);
        var output = new SlotWidget(new ItemStackTransfer(new ItemStack(Items.DIAMOND, 23)), 0, 130, 20, false, false)
                .setBackgroundTexture(new ResourceTexture("ldlib:textures/gui/slot.png"))
                .setIngredientIO(IngredientIO.OUTPUT);

        var both = new SlotWidget(new ItemStackTransfer(new ItemStack(Items.ANDESITE, 23)), 0, 60, 20, false, false)
                .setBackgroundTexture(new ResourceTexture("ldlib:textures/gui/slot.png"))
                .setIngredientIO(IngredientIO.BOTH);

        var inputFluid = new TankWidget(new FluidStorage(FluidStack.create(Fluids.WATER, 1000)), 20, 40, 20, 20, false, false)
                .setBackground(new ResourceTexture("ldlib:textures/gui/fluid_slot.png"))
                .setIngredientIO(IngredientIO.INPUT);
        var outputFluid = new TankWidget(new FluidStorage(FluidStack.create(Fluids.LAVA, 1000)), 130, 40, 20, 20, false, false)
                .setBackground(new ResourceTexture("ldlib:textures/gui/fluid_slot.png"))
                .setIngredientIO(IngredientIO.OUTPUT);
        addWidget(input);
        addWidget(output);
        addWidget(both);
        addWidget(inputFluid);
        addWidget(outputFluid);
    }
}
