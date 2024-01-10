package com.lowdragmc.lowdraglib.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;

/**
 * @author KilaBash
 * @date 2022/12/2
 * @implNote IGuiTextureTypeAdapter
 */
public class IGuiTextureTypeAdapter implements TypeAdapterFactory {
    public static final IGuiTextureTypeAdapter INSTANCE = new IGuiTextureTypeAdapter();

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!IGuiTexture.class.isAssignableFrom(type.getRawType())) return null;
        return (TypeAdapter<T>) new IGuiTextureAdapter(gson);
    }

    private static final class IGuiTextureAdapter extends TypeAdapter<IGuiTexture> {

        private final Gson gson;

        private IGuiTextureAdapter(final Gson gson) {
            this.gson = gson;
        }

        @Override
        public void write(final JsonWriter out, final IGuiTexture value) {
            if (value == null) {
                gson.toJson(JsonNull.INSTANCE, out);
                return;
            }
            gson.toJson(SimpleIGuiTextureJsonUtils.toJson(value), out);
        }

        @Override
        public IGuiTexture read(final JsonReader in) {
            final JsonElement jsonElement = gson.fromJson(in, JsonElement.class);
            if (!jsonElement.isJsonObject()) return null;
            return SimpleIGuiTextureJsonUtils.fromJson(jsonElement.getAsJsonObject());
        }

    }
}
