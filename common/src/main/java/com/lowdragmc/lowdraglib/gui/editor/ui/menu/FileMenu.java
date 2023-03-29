package com.lowdragmc.lowdraglib.gui.editor.ui.menu;

import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.annotation.RegisterUI;
import com.lowdragmc.lowdraglib.gui.editor.data.Project;
import com.lowdragmc.lowdraglib.gui.editor.runtime.UIDetector;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.widget.DialogWidget;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author KilaBash
 * @date 2022/12/17
 * @implNote FileMenu
 */
@RegisterUI(name = "file", group = "menu", priority = 101)
public class FileMenu extends MenuTab {

    protected TreeBuilder.Menu createMenu() {
        var fileMenu = TreeBuilder.Menu.start()
                .branch("ldlib.gui.editor.menu.new", this::newProject)
                .crossLine()
                .leaf(Icons.OPEN_FILE, "ldlib.gui.editor.menu.open", this::openProject)
                .leaf(Icons.SAVE, "ldlib.gui.editor.menu.save", this::saveProject)
                .crossLine()
                .branch(Icons.IMPORT, "ldlib.gui.editor.menu.import", menu -> {
                    menu.leaf("ldlib.gui.editor.menu.resource", this::importResource);
                })
                .branch(Icons.EXPORT, "ldlib.gui.editor.menu.export", menu -> {
                    menu.leaf("ldlib.gui.editor.menu.resource", this::exportResource);
                });
        var currentProject = editor.getCurrentProject();
        if (currentProject != null) {
            currentProject.attachMenu(editor, "file", fileMenu);
        }
        return fileMenu;
    }


    private void exportResource() {
        var resources = editor.getResourcePanel().getResources();
        if (resources != null) {
            DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.save_resource", editor.getWorkSpace(), false,
                    DialogWidget.suffixFilter(".resource"), r -> {
                        if (r != null && !r.isDirectory()) {
                            if (!r.getName().endsWith(".resource")) {
                                r = new File(r.getParentFile(), r.getName() + ".resource");
                            }
                            try {
                                NbtIo.write(resources.serializeNBT(), r);
                            } catch (IOException ignored) {
                                // TODO
                            }
                        }
                    });
        }
    }

    private void importResource() {
        var currentProject = editor.getCurrentProject();
        if (currentProject != null) {
            DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.load_resource", editor.getWorkSpace(), true,
                    DialogWidget.suffixFilter(".resource"), r -> {
                        if (r != null && r.isFile()) {
                            try {
                                var tag = NbtIo.read(r);
                                if (tag != null) {
                                    editor.getResourcePanel().loadResource(currentProject.loadResources(tag), true);
                                }
                            } catch (IOException ignored) {
                                // TODO
                            }
                        }
                    });
        }
    }

    private void newProject(TreeBuilder.Menu menu) {
        for (var project : UIDetector.REGISTER_PROJECTS) {
            menu = menu.leaf(project.getTranslateKey(), () -> editor.loadProject(project.newEmptyProject()));
        }
    }

    private void saveProject() {
        var project = editor.getCurrentProject();
        if (project != null) {
            String suffix = "." + project.getSuffix();
            DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.save_project", editor.getWorkSpace(), false,
                    DialogWidget.suffixFilter(suffix), file -> {
                        if (file != null && !file.isDirectory()) {
                            if (!file.getName().endsWith(suffix)) {
                                file = new File(file.getParentFile(), file.getName() + suffix);
                            }
                            project.saveProject(file);
                        }
                    });
        }
    }

    private void openProject() {
        var suffixes = UIDetector.REGISTER_PROJECTS.stream().map(Project::getSuffix).collect(Collectors.toSet());
        DialogWidget.showFileDialog(editor, "ldlib.gui.editor.tips.load_project", editor.getWorkSpace(), true,
                node -> {
                    if (node.isLeaf() && node.getContent().isFile()) {
                        String file = node.getContent().getName().toLowerCase();
                        for (String suffix : suffixes) {
                            if (file.endsWith(suffix.toLowerCase())) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return true;
                }, r -> {
                    if (r != null && r.isFile()) {
                        String file = r.getName().toLowerCase();
                        for (var project : UIDetector.REGISTER_PROJECTS) {
                            if (file.endsWith("." + project.getSuffix())) {
                                var p = project.loadProject(r);
                                if (p != null) {
                                    editor.loadProject(p);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

}
