package com.lowdragmc.lowdraglib.gui.editor.ui.resource;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.editor.data.resource.Resource;
import com.lowdragmc.lowdraglib.gui.editor.ui.Editor;
import com.lowdragmc.lowdraglib.gui.editor.ui.ResourcePanel;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.TreeBuilder;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.Tag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/12/3
 * @implNote ResourceContainer
 */
@Accessors(chain = true)
public class ResourceContainer<T, C extends Widget> extends WidgetGroup {
    @Getter
    protected final ResourcePanel panel;
    @Getter
    protected final Resource<T> resource;
    @Getter
    protected final Map<String, C> widgets;
    protected DraggableScrollableWidgetGroup container;
    @Setter @Getter
    protected Function<String, C> widgetSupplier;
    @Setter
    protected Function<String, T> onAdd;
    @Setter
    protected Predicate<String> onRemove;
    @Setter
    protected Consumer<String> onEdit;
    protected Function<String, Object> draggingMapping;
    protected Function<Object, IGuiTexture> draggingRenderer;
    @Setter
    protected Supplier<String> nameSupplier;
    @Setter
    protected Predicate<String> renamePredicate;

    @Getter @Nullable
    protected String selected;

    public ResourceContainer(Resource<T> resource, ResourcePanel panel) {
        super(3, 0, panel.getSize().width - 6, panel.getSize().height - 14);
        setClientSideWidget();
        this.widgets = new HashMap<>();
        this.panel = panel;
        this.resource = resource;
    }

    public <D> ResourceContainer<T, C> setDragging(Function<String, D> draggingMapping, Function<D, IGuiTexture> draggingRenderer) {
        this.draggingMapping = draggingMapping::apply;
        this.draggingRenderer = o -> draggingRenderer.apply((D) o);
        return this;
    }

    @Override
    public void initWidget() {
        Size size = getSize();
        container = new DraggableScrollableWidgetGroup(1, 2, size.width - 2, size.height - 2);
        container.setYScrollBarWidth(4).setYBarStyle(null, ColorPattern.T_WHITE.rectTexture().setRadius(2));
        addWidget(container);
        reBuild();
        super.initWidget();
    }

    public void reBuild() {
        selected = null;
        container.clearAllWidgets();
        int width = getSize().getWidth();
        int x = 1;
        int y = 3;
        for (Map.Entry<String, T> entry : resource.allResources()) {
            var widget = widgetSupplier.apply(entry.getKey());
            widgets.put(entry.getKey(), widget);
            Size size = widget.getSize();
            SelectableWidgetGroup selectableWidgetGroup = new SelectableWidgetGroup(0, 0, size.width, size.height + 14);
            selectableWidgetGroup.setDraggingProvider(draggingMapping == null ? entry::getValue : () -> draggingMapping.apply(entry.getKey()), (c, p) -> draggingRenderer.apply(c));
            selectableWidgetGroup.addWidget(widget);
            selectableWidgetGroup.addWidget(new ImageWidget(0, size.height + 3, size.width, 10, new TextTexture(entry.getKey()).setWidth(size.width).setType(TextTexture.TextType.ROLL)));
            selectableWidgetGroup.setOnSelected(s -> selected = entry.getKey());
            selectableWidgetGroup.setOnUnSelected(s -> selected = null);
            selectableWidgetGroup.setSelectedTexture(ColorPattern.T_GRAY.rectTexture());
            size = selectableWidgetGroup.getSize();

            if (size.width >= width - 5) {
                selectableWidgetGroup.setSelfPosition(new Position(0, y));
                y += size.height + 3;
            } else if (size.width < width - 5 - x) {
                selectableWidgetGroup.setSelfPosition(new Position(x, y));
                x += size.width + 3;
            } else {
                y += size.height + 3;
                x = 1;
                selectableWidgetGroup.setSelfPosition(new Position(x, y));
                x += size.width + 3;
            }
            container.addWidget(selectableWidgetGroup);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var result = super.mouseClicked(mouseX, mouseY, button);
        if (button == 1 && isMouseOverElement(mouseX, mouseY)) {
            panel.getEditor().openMenu(mouseX, mouseY, getMenu());
            return true;
        }
        return result;
    }

    protected TreeBuilder.Menu getMenu() {
        var menu = TreeBuilder.Menu.start();
        if (onEdit != null) {
            menu.leaf(Icons.EDIT_FILE, "ldlib.gui.editor.menu.edit", this::editResource);
        }
        menu.leaf("ldlib.gui.editor.menu.rename", this::renameResource);
        menu.crossLine();
        menu.leaf(Icons.COPY, "ldlib.gui.editor.menu.copy", this::copy);
        menu.leaf(Icons.PASTE, "ldlib.gui.editor.menu.paste", this::paste);
        if (onAdd != null) {
            menu.leaf(Icons.ADD_FILE, "ldlib.gui.editor.menu.add_resource", this::addNewResource);
        }
        menu.leaf(Icons.REMOVE_FILE, "ldlib.gui.editor.menu.remove", this::removeSelectedResource);
        return menu;
    }

    protected void paste() {
        panel.getEditor().ifCopiedPresent(resource.name(), c -> {
            var value = getResource().deserialize((Tag)c);
            resource.addResource(genNewFileName(), value);
            reBuild();
        });
    }

    protected void copy() {
        if (selected != null) {
            panel.getEditor().setCopy(resource.name(), resource.serialize(resource.getResource(selected)));
        }
    }

    protected void renameResource() {
        if (selected != null) {
            DialogWidget.showStringEditorDialog(Editor.INSTANCE, LocalizationUtils.format("ldlib.gui.editor.tips.rename") + " " + LocalizationUtils.format(resource.name()), selected, s -> {
                if (resource.hasResource(s)) {
                    return false;
                }
                if (renamePredicate != null) {
                    return renamePredicate.test(s);
                }
                return true;
            }, s -> {
                if (s == null) return;
                var stored =  resource.removeResource(selected);
                resource.addResource(s, stored);
                reBuild();
            });
        }
    }

    protected void editResource() {
        if (onEdit != null && selected != null) {
            onEdit.accept(selected);
        }
    }

    protected String genNewFileName() {
        String randomName = "new ";
        if (nameSupplier != null) {
            randomName = nameSupplier.get();
        } else {
            int i = 0;
            while (resource.hasResource(randomName + i)) {
                i++;
            }
            randomName += i;
        }
        return randomName;
    }

    protected void addNewResource() {
        if (onAdd != null) {
            String randomName = genNewFileName();
            resource.addResource(randomName, onAdd.apply(randomName));
            reBuild();
        }
    }

    protected void removeSelectedResource() {
        if (selected == null) return;
        if (onRemove == null || onRemove.test(selected)) {
            resource.removeResource(selected);
            reBuild();
        }
    }

}
