package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;
import java.util.function.Consumer;

public class FluidStackSelectorWidget extends WidgetGroup {
    private Consumer<FluidStack> onIFluidStackUpdate;
    private final IFluidStorage handler;
    private final TextFieldWidget fluidField;
    private FluidStack fluid = FluidStack.empty();

    public FluidStackSelectorWidget(int x, int y, int width) {
        super(x, y, width, 20);
        setClientSideWidget();
        fluidField = (TextFieldWidget) new TextFieldWidget(22, 0, width - 46, 20, null, s -> {
            if (s != null && !s.isEmpty()) {
                Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(s));
                if (fluid == null) {
                    fluid = FluidStack.empty().getFluid();
                }
                if (!this.fluid.isFluidEqual(FluidStack.create(fluid, 1000))) {
                    this.fluid = FluidStack.create(fluid, 1000);
                    onUpdate();
                }
            }
        }).setResourceLocationOnly().setHoverTooltips("ldlib.gui.tips.fluid_selector");

        addWidget(new PhantomFluidWidget(handler = new FluidStorage(1000),1, 1)
                .setIFluidStackUpdater(fluidStack -> {
                    setIFluidStack(fluidStack);
                    onUpdate();
                }).setBackground(new ColorBorderTexture(1, -1)));
        addWidget(fluidField);

        addWidget(new ButtonWidget(width - 21, 0, 20, 20, null, cd -> {
            if (fluid.isEmpty()) return;
            TextFieldWidget nbtField;
            new DialogWidget(getGui().mainGroup, isClientSideWidget)
                    .setOnClosed(this::onUpdate)
                    .addWidget(nbtField = new TextFieldWidget(10, 10, getGui().mainGroup.getSize().width - 50, 20, null, s -> {
                        try {
                            fluid.setTag(TagParser.parseTag(s));
                            onUpdate();
                        } catch (CommandSyntaxException ignored) {

                        }
                    }));
            if (fluid.hasTag()) {
                nbtField.setCurrentString(fluid.getTag().toString());
            }
        })
                .setButtonTexture(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("NBT", -1).setDropShadow(true))
                .setHoverBorderTexture(1, -1).setHoverTooltips("ldlib.gui.tips.fluid_tag"));
    }

    public FluidStack getIFluidStack() {
        return fluid;
    }

    public FluidStackSelectorWidget setIFluidStack(FluidStack fluidStack) {
        fluid = Objects.requireNonNullElse(fluidStack, FluidStack.empty()).copy();
        if (fluid != FluidStack.empty()) {
            fluid.setAmount(1000);
        }
        handler.setFluid(fluid);
        fluidField.setCurrentString(BuiltInRegistries.FLUID.getKey(fluid.getFluid()));
        return this;
    }

    public FluidStackSelectorWidget setOnIFluidStackUpdate(Consumer<FluidStack> onIFluidStackUpdate) {
        this.onIFluidStackUpdate = onIFluidStackUpdate;
        return this;
    }

    private void onUpdate() {
        handler.setFluid(fluid);
        if (onIFluidStackUpdate != null) {
            onIFluidStackUpdate.accept(fluid);
        }
    }
}
