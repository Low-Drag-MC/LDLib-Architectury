package com.lowdragmc.lowdraglib.utils;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;

@LDLRegister(name = "number_converter", group = "type_adapter")
public class NumberTypeAdapters implements TypeAdapter.ITypeAdapter {

    @Override
    public void onRegister() {
        TypeAdapter.registerAdapter(float.class, int.class, Float::intValue);
        TypeAdapter.registerAdapter(int.class, float.class, Float::valueOf);
        TypeAdapter.registerAdapter(float.class, Float.class, o -> o);
        TypeAdapter.registerAdapter(int.class, Integer.class, o -> o);
        TypeAdapter.registerAdapter(int.class, Float.class, Integer::floatValue);
        TypeAdapter.registerAdapter(float.class, Integer.class, Float::intValue);
        TypeAdapter.registerAdapter(Float.class, float.class, Float::floatValue);
        TypeAdapter.registerAdapter(Integer.class, int.class, Integer::intValue);
        TypeAdapter.registerAdapter(Float.class, int.class, Float::intValue);
        TypeAdapter.registerAdapter(Integer.class, float.class, Float::valueOf);
        TypeAdapter.registerAdapter(Float.class, Integer.class, Float::intValue);
        TypeAdapter.registerAdapter(Integer.class, Float.class, Integer::floatValue);
    }
}
