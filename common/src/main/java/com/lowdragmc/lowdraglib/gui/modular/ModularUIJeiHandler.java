package com.lowdragmc.lowdraglib.gui.modular;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModularUIJeiHandler implements IGuiContainerHandler<ModularUIGuiContainer>, IGhostIngredientHandler<ModularUIGuiContainer>{

    @Override
    @Nonnull
    public List<Rect2i> getGuiExtraAreas(@Nonnull ModularUIGuiContainer containerScreen) {
        return containerScreen.getGuiExtraAreas();
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(ModularUIGuiContainer gui, double mouseX, double mouseY) {
        return gui.modularUI.mainGroup.getIngredientOverMouse(mouseX, mouseY);
    }

    @Nonnull
    @Override
    public <I> List<Target<I>> getTargets(ModularUIGuiContainer gui, @Nonnull I ingredient, boolean doStart) {
        List<com.lowdragmc.lowdraglib.gui.ingredient.Target> targets = gui.modularUI.mainGroup.getPhantomTargets(ingredient);
        if (targets.isEmpty()) return Collections.emptyList();
        return targets.stream().map(target-> new Target<I>() {
            @Nonnull
            @Override
            public Rect2i getArea() {
                return target.getArea();
            }

            @Override
            public void accept(I i) {
                target.accept(i);
            }
        }).collect(Collectors.toList());
    }


    @Override
    public void onComplete() {
    }
}
