package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.editor.Icons;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.FileNode;
import com.lowdragmc.lowdraglib.gui.util.TreeNode;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DialogWidget extends WidgetGroup {
    protected boolean isParentInVisible;
    protected Runnable onClosed;
    @Setter
    protected boolean clickClose;

    public DialogWidget(WidgetGroup parent, boolean isClient) {
        this(0, 0, parent.getSize().width, parent.getSize().height);
        if (isClient) setClientSideWidget();
        if (autoAdd()) {
            parent.addWidget(this);
        }
    }

    public DialogWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    protected boolean autoAdd() {
        return true;
    }

    public DialogWidget setOnClosed(Runnable onClosed) {
        this.onClosed = onClosed;
        return this;
    }

    public DialogWidget setParentInVisible() {
        this.isParentInVisible = true;
        for (Widget widget : parent.widgets) {
            if (widget != this) {
                widget.setVisible(false);
                widget.setActive(false);
            }
        }
        return this;
    }

    public void close() {
        parent.waitToRemoved(this);
        if (isParentInVisible) {
            for (Widget widget : parent.widgets) {
                widget.setVisible(true);
                widget.setActive(true);
            }
        }
        if (onClosed != null) {
            onClosed.run();
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInBackground(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOverElement(mouseX, mouseY)) {
            if (clickClose) {
                close();
                return true;
            }
            return false;
        }
        super.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!super.keyPressed(keyCode, scanCode, modifiers) && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            writeClientAction(-1, x->{});
            close();
        }
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        super.keyReleased(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean charTyped(char codePoint, int modifiers) {
        super.charTyped(codePoint, modifiers);
        return true;
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        super.handleClientAction(id, buffer);
        if (id == -1) {
            close();
        }
    }

    public static Predicate<TreeNode<File, File>> suffixFilter(String... suffixes) {
        return node -> {
            for (String suffix : suffixes) {
                if (!(node.isLeaf() && node.getContent().isFile() && !node.getContent().getName().toLowerCase().endsWith(suffix.toLowerCase()))) {
                    return true;
                }
            }
            return false;
        };
    }

    public static WidgetGroup createContainer(DialogWidget dialog, int width, int height, String titleText) {
        WidgetGroup title, content;
        var size = dialog.getSize();
        int x = (size.width - width) / 2;
        int y = (size.height - height) / 2;
        dialog.addWidget(title = new WidgetGroup(x, y, width, 15));
        title.setBackground(new GuiTextureGroup(ColorPattern.RED.rectTexture().setTopRadius(5f), ColorPattern.GRAY.borderTexture(-1).setTopRadius(5f), new TextTexture(titleText).setWidth(width).setDropShadow(false).setType(TextTexture.TextType.ROLL)));
        dialog.addWidget(content = new WidgetGroup(x, y + 15, width, height - 15));
        content.setBackground(new GuiTextureGroup(ColorPattern.BLACK.rectTexture().setBottomRadius(5f), ColorPattern.GRAY.borderTexture(-1).setBottomRadius(5f)));
        return content;
    }

    public static TextFieldWidget createTextField(WidgetGroup parent, int x, int y, int width, int height) {
        TextFieldWidget textFieldWidget;
        parent.addWidget(new ImageWidget(x, y, width, height, ColorPattern.T_GRAY.rectTexture().setRadius(height / 2f)));
        parent.addWidget(textFieldWidget = new TextFieldWidget(x + 3, y, width - 6, height, null, null));
        textFieldWidget.setBordered(false);
        return textFieldWidget;
    }

    public static ButtonWidget createButton(WidgetGroup parent, int x, int y, int width, int height, String text, Runnable onClick) {
        ButtonWidget buttonWidget;
        parent.addWidget(buttonWidget = new ButtonWidget(x, y, width, height,
                new GuiTextureGroup(ColorPattern.T_GRAY.rectTexture().setRadius(height / 2f),
                        new TextTexture(text).setWidth(width).setDropShadow(false).setType(TextTexture.TextType.ROLL)),
                cd -> onClick.run())
                .setHoverTexture(new GuiTextureGroup(ColorPattern.GRAY.rectTexture().setRadius(height / 2f),
                        new TextTexture(text).setWidth(width).setDropShadow(false).setType(TextTexture.TextType.ROLL))));
        return buttonWidget;
    }

    public static DialogWidget showStringEditorDialog(WidgetGroup parent, String title, String initial, @Nullable Predicate<String> predicate, Consumer<String> result) {
        DialogWidget dialog = new DialogWidget(parent, true);
        var container = createContainer(dialog, 200, 100, title);
        var size = container.getSize();
        TextFieldWidget textFieldWidget = createTextField(container, 10, size.height / 2 - 10 -5, size.width - 20, 10);

        textFieldWidget.setCurrentString(initial);
        if (predicate != null) {
            textFieldWidget.setValidator(s -> predicate.test(s) ? s : textFieldWidget.getCurrentString());
        }

        createButton(container, ((size.width / 2) - 60) / 2, size.height / 2 + 20 - 7, 60, 15, "ldlib.gui.tips.confirm", () -> {
            dialog.close();
            if (result != null) result.accept(textFieldWidget.getCurrentString());
        });

        createButton(container, ((size.width / 2) - 60) / 2 + size.width / 2, size.height / 2 + 20 - 7, 60, 15, "ldlib.gui.tips.cancel", () -> {
            dialog.close();
            if (result != null) result.accept(null);
        });

        return dialog;
    }

    public static DialogWidget showFileDialog(WidgetGroup parent, String title, File dir, boolean isSelector, Predicate<TreeNode<File, File>> valid, Consumer<File> result) {
        DialogWidget dialog = new DialogWidget(parent, true);
        dialog.addWidget(new ImageWidget(0, 0, parent.getSize().width, parent.getSize().height, new ColorRectTexture(0x4f000000)));
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                return dialog;
            }
        }
        var container = createContainer(dialog, 300, 200, title);
        var size = container.getSize();

        AtomicReference<File> selected = new AtomicReference<>();
        selected.set(dir);
        container.addWidget(new TreeListWidget<>(3, 15, size.width - 6, size.height - 20 - 15, new FileNode(dir).setValid(valid), node -> selected.set(node.getKey()))
                .setKeyIconSupplier(file -> Icons.FOLDER)
                .setContentIconSupplier(file -> Icons.getIcon(file.getName().substring(file.getName().lastIndexOf('.') + 1)))
                .canSelectNode(true)
                .setBackground(ColorPattern.GRAY.borderTexture(-1)));

        createButton(container, ((size.width / 2) - 60) / 2, size.height - 20 + 3, 60, 15, "ldlib.gui.tips.confirm", () -> {
            dialog.close();
            if (result != null) result.accept(selected.get());
        });

        createButton(container, ((size.width / 2) - 60) / 2 + size.width / 2, size.height - 20 + 3, 60, 15, "ldlib.gui.tips.cancel", () -> {
            dialog.close();
            if (result != null) result.accept(null);
        });

        var textFieldWidget = createTextField(container, 3, 3, size.width - 6, 10);
        var rootPath = dir.toString();
        if (isSelector) {
            textFieldWidget.setTextSupplier(() -> {
                File file = selected.get();
                if (file != null && !file.isDirectory()) {
                    return selected.get().toString();
                }
                return "no file selected";
            });
            textFieldWidget.setTextResponder(res -> {
                if (!res.isEmpty() && res.startsWith(rootPath)) {
                    selected.set(new File(res));
                }
            });
        } else {
            textFieldWidget.setTextSupplier(() -> {
                File file = selected.get();
                if (file != null && !file.isDirectory()) {
                    return selected.get().getName();
                }
                return "";
            });
            textFieldWidget.setTextResponder(res -> {
                File file = selected.get();
                if (file == null) return;
                if (file.isDirectory()) {
                    selected.set(new File(file, res));
                } else {
                    selected.set(new File(file.getParent(), res));
                }
            });
        }

        // open folder
        container.addWidget(new ButtonWidget(3, size.height - 20 + 3, 15, 15,
                new GuiTextureGroup(ColorPattern.T_GRAY.rectTexture().setRadius(5), Icons.OPEN_FILE),
                cd -> {
                    File file = selected.get();
                    if (file != null) {
                        Util.getPlatform().openFile(file.isDirectory() ? file : file.getParentFile());
                    }
                })
                .setHoverTexture(new GuiTextureGroup(ColorPattern.GRAY.rectTexture().setRadius(5), Icons.OPEN_FILE))
                .setHoverTooltips("ldlib.gui.tips.open_folder"));
        return dialog;
    }
}
