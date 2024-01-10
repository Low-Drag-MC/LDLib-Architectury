package com.lowdragmc.lowdraglib.gui.editor.data;

import com.lowdragmc.lowdraglib.gui.editor.ILDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.ui.Editor;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;

import java.nio.file.Path;

/**
 * @author KilaBash
 * @date 2022/12/9
 * @implNote IProject
 */
public interface IProject extends ILDLRegister {

    Resources getResources();

    /**
     * Save project
     */
    void saveProject(Path file);

    /**
     * Load project from file. return null if loading failed
     */
    @Nullable
    IProject loadProject(Path file);

    IProject newEmptyProject();

    /**
     * Suffix name of this project
     */
    default String getSuffix() {
        return name();
    }

    /**
     * Fired when project is closed
     */
    default void onClosed(Editor editor) {
    }

    /**
     * Fired when project is opened
     */
    default void onLoad(Editor editor) {
        editor.getResourcePanel().loadResource(getResources(), false);
    }

    /**
     * Attach menu
     * @param name menu name
     * @param menu current menu
     */
    default void attachMenu(Editor editor, String name, TreeBuilder.Menu menu) {

    }

    /**
     * Load resource from nbt data
     */
    default Resources loadResources(CompoundTag tag) {
        return Resources.fromNBT(tag);
    }

}
