package com.lowdragmc.lowdraglib.gui.editor.configurator;

import com.lowdragmc.lowdraglib.gui.editor.IRegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.runtime.ConfiguratorParser;

import java.util.HashMap;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote IConfigurable
 *
 * You may need to register it as a {@link RegisterUI}.
 * <br>
 * to de/serialize it.
 */
public interface IConfigurable extends IRegisterUI {

    /**
     * Add configurators into given group
     * @param father father group
     */
    default void buildConfigurator(ConfiguratorGroup father) {
        ConfiguratorParser.createConfigurators(father, new HashMap<>(), getClass(), this);
    }

}
