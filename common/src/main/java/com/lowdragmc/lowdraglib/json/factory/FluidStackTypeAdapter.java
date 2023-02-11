package com.lowdragmc.lowdraglib.json.factory;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

public class FluidStackTypeAdapter implements TypeAdapterFactory {
    public static final FluidStackTypeAdapter INSTANCE = new FluidStackTypeAdapter();

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!FluidStack.class.isAssignableFrom(type.getRawType())) return null;
        return (TypeAdapter<T>) new IFluidStackAdapter(gson);
    }

    private static final class IFluidStackAdapter extends TypeAdapter<FluidStack> {

        private final Gson gson;

        private IFluidStackAdapter(final Gson gson) {
            this.gson = gson;
        }

        @Override
        public void write(final JsonWriter out, final FluidStack value) {
            if (value == null) {
                gson.toJson(JsonNull.INSTANCE, out);
                return;
            }
            gson.toJson(new JsonPrimitive(value.saveToTag(new CompoundTag()).toString()), out);
        }

        @Override
        public FluidStack read(final JsonReader in) {
            final JsonElement jsonElement = gson.fromJson(in, JsonElement.class);
            if (!jsonElement.isJsonObject()) return null;
            try {
                return FluidStack.loadFromTag(TagParser.parseTag(jsonElement.getAsString()));
            } catch (CommandSyntaxException e) {
                return FluidStack.empty();
            }
        }

    }

}
