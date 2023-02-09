package com.lowdragmc.lowdraglib.json;

import com.google.gson.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Type;

public class FluidStackTypeAdapter implements JsonDeserializer<FluidStack>, JsonSerializer<FluidStack> {

    public static final FluidStackTypeAdapter INSTANCE = new FluidStackTypeAdapter();

    private FluidStackTypeAdapter() { }

    @Override
    public FluidStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return FluidStack.loadFluidStackFromNBT(TagParser.parseTag(json.getAsString()));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public JsonElement serialize(FluidStack src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.writeToNBT(new CompoundTag()).toString());
    }
}
