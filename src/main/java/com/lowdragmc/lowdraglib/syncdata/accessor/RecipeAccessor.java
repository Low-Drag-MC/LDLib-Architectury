package com.lowdragmc.lowdraglib.syncdata.accessor;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

public class RecipeAccessor extends CustomObjectAccessor<RecipeHolder> {

    public RecipeAccessor() {
        super(RecipeHolder.class, true);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, RecipeHolder value) {
        var type = BuiltInRegistries.RECIPE_TYPE.getKey(value.value().getType());
        var id = value.id();
        CompoundTag tag = new CompoundTag();
        tag.putString("type", type == null ? "null" : type.toString());
        tag.putString("id", id.toString());
        return NbtTagPayload.of(tag);
    }

    @Override
    public RecipeHolder deserialize(AccessorOp op, ITypedPayload<?> payload) {
        if (payload instanceof NbtTagPayload nbtTagPayload && nbtTagPayload.getPayload() instanceof CompoundTag tag) {
            var type = BuiltInRegistries.RECIPE_TYPE.get(new ResourceLocation(tag.getString("type")));
            var id = new ResourceLocation(tag.getString("id"));
            if (type != null) {
                RecipeManager recipeManager;
                if (LDLib.isRemote()) {
                    recipeManager = Minecraft.getInstance().getConnection().getRecipeManager();
                } else {
                    recipeManager = Platform.getMinecraftServer().getRecipeManager();
                }
                for (RecipeHolder<?> recipe : recipeManager.getRecipes()) {
                    if (recipe.value().getType() == type && recipe.id().equals(id)) {
                        return recipe;
                    }
                }
            }
        }
        return null;
    }
}
