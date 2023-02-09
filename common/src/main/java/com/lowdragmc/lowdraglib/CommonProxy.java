package com.lowdragmc.lowdraglib;

import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.factory.UIEditorFactory;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.networking.LDLNetworking;


public class CommonProxy {
    public static void init() {
        LDLNetworking.init();
        UIFactory.register(BlockEntityUIFactory.INSTANCE);
        UIFactory.register(HeldItemUIFactory.INSTANCE);
        UIFactory.register(UIEditorFactory.INSTANCE);
        UIDetector.init();
    }
}
