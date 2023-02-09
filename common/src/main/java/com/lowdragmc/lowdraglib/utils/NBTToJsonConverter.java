package com.lowdragmc.lowdraglib.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.*;

import java.util.Set;

/**
 * @author KilaBash
 * @date 2022/10/22
 * @implNote NBTToJsonConverter
 * @copyright https://github.com/MineMaarten/PneumaticCraft/blob/master/src/pneumaticCraft/common/util/NBTToJsonConverter.java
 */
public class NBTToJsonConverter{

    public static JsonElement getObject(CompoundTag tag) {
        Set<String> keys = tag.getAllKeys();
        JsonObject jsonRoot = new JsonObject();
        for (String key : keys) {
            JsonElement value;
            Tag nbt = tag.get(key);
            if (nbt instanceof CompoundTag) {
                value = getObject((CompoundTag)nbt);
            } else if (nbt instanceof NumericTag) {
                value = new JsonPrimitive(((NumericTag)nbt).getAsDouble());
            } else if (nbt instanceof StringTag) {
                value = new JsonPrimitive(nbt.getAsString());
            } else {
                JsonArray array;
                if (nbt instanceof ListTag tagList) {
                    array = new JsonArray();

                    for(int i = 0; i < tagList.size(); ++i) {
                        if (tagList.getElementType() == 10) {
                            array.add(getObject(tagList.getCompound(i)));
                        } else if (tagList.getElementType() == 8) {
                            array.add(new JsonPrimitive(tagList.getString(i)));
                        }
                    }
                    value = array;
                } else {
                    if (!(nbt instanceof IntArrayTag intArray)) {
                        byte var10002 = nbt.getId();
                        throw new IllegalArgumentException("NBT to JSON converter doesn't support the nbt tag: " + var10002 + ", tag: " + nbt);
                    }

                    array = new JsonArray();

                    for (int i : intArray.getAsIntArray()) {
                        array.add(new JsonPrimitive(i));
                    }
                    value = array;
                }
            }
            jsonRoot.add(key, value);
        }

        return jsonRoot;
    }
}
