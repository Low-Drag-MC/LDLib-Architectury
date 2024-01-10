package com.lowdragmc.lowdraglib.gui.editor.ui.menu;

import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.data.IProject;
import com.lowdragmc.lowdraglib.gui.editor.runtime.AnnotationDetector;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.widget.DialogWidget;
import lombok.Setter;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author KilaBash
 * @date 2022/12/17
 * @implNote FileMenu
 */
@LDLRegister(name = "file", group = "editor", priority = 101)
public class FileMenu extends MenuTab {
    @Setter
    protected Predicate<IProject> projectFilter = project -> project.group().startsWith(editor.name());

    protected TreeBuilder.Menu createMenu() {
        return TreeBuilder.Menu.start()
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
                                NbtIo.write(resources.serializeNBT(), r.toPath());
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
                                var tag = NbtIo.read(r.toPath());
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

    protected Predicate<IProject> getProjectPredicate() {
        return projectFilter;
    }

    private void newProject(TreeBuilder.Menu menu) {
        for (var project : AnnotationDetector.REGISTER_PROJECTS.stream().filter(getProjectPredicate()).toList()) {
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
                            project.saveProject(file.toPath());
                        }
                    });
        }
    }

    private void openProject() {
        var suffixes = AnnotationDetector.REGISTER_PROJECTS.stream().filter(getProjectPredicate()).map(IProject::getSuffix).collect(Collectors.toSet());
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
                        for (var project : AnnotationDetector.REGISTER_PROJECTS.stream().filter(getProjectPredicate()).toList()) {
                            if (file.endsWith("." + project.getSuffix())) {
                                var p = project.loadProject(r.toPath());
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
