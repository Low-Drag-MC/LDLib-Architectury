package com.lowdragmc.lowdraglib.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IModIngredientRegistration;

import javax.annotation.Nonnull;
import java.util.Collection;

public abstract class AbstractIngredient<T> implements IIngredientType<T>, IIngredientHelper<T>, IIngredientRenderer<T> {
    
    public void registerIngredients(IModIngredientRegistration registry) {
        registry.register(this, getAllIngredients(), this, this);
    }

    @Override
    @Nonnull
    public IIngredientType<T> getIngredientType() {
        return this;
    }

    public abstract Collection<T> getAllIngredients();
}
