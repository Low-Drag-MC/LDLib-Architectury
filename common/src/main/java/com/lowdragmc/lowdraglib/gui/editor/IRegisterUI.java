package com.lowdragmc.lowdraglib.gui.editor;

import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import net.minecraft.network.chat.Component;

/**
 * @author KilaBash
 * @date 2022/12/17
 * @implNote IRegisterUI
 */
public interface IRegisterUI {

    /**
     * Whether element is registered
     */
    default boolean isRegisterUI() {
        return getClass().isAnnotationPresent(RegisterUI.class);
    }

    default RegisterUI getRegisterUI() {
        return getClass().getAnnotation(RegisterUI.class);
    }

    default String name() {
        if (isRegisterUI()) {
            return getRegisterUI().name();
        }
        throw new RuntimeException("not registered ui %s".formatted(getClass()));
    }

    default String group() {
        if (isRegisterUI()) {
            return getRegisterUI().group();
        }
        throw new RuntimeException("not registered ui %s".formatted(getClass()));
    }

    default String getTranslateKey() {
        return "ldlib.gui.editor.register.%s.%s".formatted(group(), name());
    }

    default Component getChatComponent() {
        return Component.translatable(getTranslateKey());
    }
}
