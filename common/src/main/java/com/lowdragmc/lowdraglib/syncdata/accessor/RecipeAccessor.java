package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

public class RecipeAccessor extends CustomObjectAccessor<Recipe> {

    public RecipeAccessor() {
        super(Recipe.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, Recipe value) {
        var type = Registry.RECIPE_TYPE.getKey(value.getType());
        var id = value.getId();
        CompoundTag tag = new CompoundTag();
        tag.putString("type", type == null ? "null" : type.toString());
        tag.putString("id", id.toString());
        return NbtTagPayload.of(tag);
    }

    @Override
    public Recipe deserialize(AccessorOp op, ITypedPayload<?> payload) {
        if (payload instanceof NbtTagPayload nbtTagPayload && nbtTagPayload.getPayload() instanceof CompoundTag tag) {
            var type = Registry.RECIPE_TYPE.get(new ResourceLocation(tag.getString("type")));
            var id = new ResourceLocation(tag.getString("id"));
            if (type != null) {
                RecipeManager recipeManager;
                if (LDLib.isRemote()) {
                    recipeManager = Minecraft.getInstance().getConnection().getRecipeManager();
                } else {
                    recipeManager = Platform.getMinecraftServer().getRecipeManager();
                }
                for (Recipe<?> recipe : recipeManager.getRecipes()) {
                    if (recipe.getType() == type && recipe.getId().equals(id)) {
                        return recipe;
                    }
                }
            }
        }
        return null;
    }
}
