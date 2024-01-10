package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegisterClient;

/**
 * @author KilaBash
 * @date 2023/6/13
 * @implNote ILDLRegisterClient
 */
public interface ILDLRegisterClient extends ILDLRegister {
    @Override
    default boolean isLDLRegister() {
        return getClass().isAnnotationPresent(LDLRegisterClient.class);
    }

    @Override
    @Deprecated
    default LDLRegister getRegisterUI() {
        throw new IllegalCallerException("call a client only register");
    }

    default LDLRegisterClient getRegisterUIClient() {
        return getClass().getAnnotation(LDLRegisterClient.class);
    }


    @Override
    default String name() {
        if (isLDLRegister()) {
            return getRegisterUIClient().name();
        }
        throw new RuntimeException("not registered %s".formatted(getClass()));
    }

    @Override
    default String group() {
        if (isLDLRegister()) {
            return getRegisterUIClient().group();
        }
        throw new RuntimeException("not registered ui %s".formatted(getClass()));
    }

}
