package com.lowdragmc.lowdraglib.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class TagUtils {

    public static CompoundTag getOrCreateTag(CompoundTag compoundTag, String key) {
        if (!compoundTag.contains(key)) {
            compoundTag.put(key, new CompoundTag());
        }
        return compoundTag.getCompound(key);
    }

    public static Tag getTagExtended(CompoundTag compoundTag, String key) {
        return getTagExtended(compoundTag, key, false);
    }

    public static Tag getTagExtended(CompoundTag compoundTag, String key, boolean create) {
        if(compoundTag == null) {
            if(create) {
                throw new NullPointerException("CompoundTag is null");
            }
            return null;
        }
        String[] keys = key.split("\\.");
        CompoundTag current = compoundTag;
        for (int i = 0; i < keys.length - 1; i++) {
            if(create) {
                current = getOrCreateTag(current, keys[i]);
            } else {
                if(!current.contains(keys[i])) {
                    return null;
                }
                current = current.getCompound(keys[i]);
            }
        }
        return current.get(keys[keys.length - 1]);
    }

    public static <T extends Tag> T getTagExtended(CompoundTag compoundTag, String key, T defaultValue) {
        var tag = getTagExtended(compoundTag, key, false);
        if(tag == null) {
            return defaultValue;
        }
        return (T) tag;
    }

    public static void setTagExtended(CompoundTag compoundTag, String key, Tag tag) {
        String[] keys = key.split("\\.");
        CompoundTag current = compoundTag;
        for (int i = 0; i < keys.length - 1; i++) {
            current = getOrCreateTag(current, keys[i]);
        }
        current.put(keys[keys.length - 1], tag);
    }
}
