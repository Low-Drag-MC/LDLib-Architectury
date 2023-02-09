package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.util.FileNode;
import com.lowdragmc.lowdraglib.gui.util.TreeNode;
import com.lowdragmc.lowdraglib.utils.Size;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DialogWidget extends WidgetGroup {
    private static final int HEIGHT = 128;
    private static final int WIDTH = 184;
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

    public static DialogWidget showStringEditorDialog(WidgetGroup parent, String title, String initial, Predicate<String> predicate, Consumer<String> result) {
        Size size = parent.getSize();
        DialogWidget dialog = new DialogWidget(parent, true);
        TextFieldWidget textFieldWidget;
        int x = (size.width - WIDTH) / 2;
        int y = (size.height - HEIGHT) / 2;
        dialog.addWidget(new ImageWidget(x, y, WIDTH, HEIGHT, ResourceBorderTexture.BORDERED_BACKGROUND));

        dialog.addWidget(textFieldWidget = new TextFieldWidget(x + WIDTH / 2 - 70, y + HEIGHT / 2 - 10, 140, 20,  null, null).setCurrentString(initial));
        if (predicate != null) {
            textFieldWidget.setValidator(s -> predicate.test(s) ? s : textFieldWidget.getCurrentString());
        }

        dialog.addWidget(new ButtonWidget(x + WIDTH / 2 - 30 - 20, y + HEIGHT - 32, 40, 20, cd -> {
            dialog.close();
            if (result != null) result.accept(textFieldWidget.getCurrentString());
        }).setButtonTexture(new ResourceTexture("ldlib:textures/gui/darkened_slot.png"), new TextTexture("ldlib.gui.tips.confirm", -1).setDropShadow(true)).setHoverBorderTexture(1, 0xff000000));
        dialog.addWidget(new ButtonWidget(x + WIDTH / 2 + 30 - 20, y + HEIGHT - 32, 40, 20, cd -> {
            dialog.close();
            if (result != null) result.accept(null);
        }).setButtonTexture(new ResourceTexture("ldlib:textures/gui/darkened_slot.png"), new TextTexture("ldlib.gui.tips.cancel", 0xffff0000).setDropShadow(true)).setHoverBorderTexture(1, 0xff000000));


        dialog.addWidget(new ImageWidget(x + 15, y + 20, WIDTH - 30,10, new TextTexture(title, -1).setWidth(WIDTH - 30).setDropShadow(true)));
        return dialog;
    }

    public static DialogWidget showFileDialog(WidgetGroup parent, String title, File dir, boolean isSelector, Predicate<TreeNode<File, File>> valid, Consumer<File> result) {
        Size size = parent.getSize();
        DialogWidget dialog = new DialogWidget(parent, true);
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                return dialog;
            }
        }
        dialog.addWidget(new ImageWidget(0, 0, parent.getSize().width, parent.getSize().height, new ColorRectTexture(0x4f000000)));
        AtomicReference<File> selected = new AtomicReference<>();
        selected.set(dir);
        dialog.addWidget(new TreeListWidget<>(0, 0, 130, size.height, new FileNode(dir).setValid(valid), node -> selected.set(node.getKey()))
                .setNodeTexture(ResourceBorderTexture.BORDERED_BACKGROUND)
                .canSelectNode(true)
                .setLeafTexture(new ResourceTexture("ldlib:textures/gui/darkened_slot.png")));
        int x = 130 + (size.width - 133 - WIDTH) / 2;
        int y = (size.height - HEIGHT) / 2;
        dialog.addWidget(new ImageWidget(x, y, WIDTH, HEIGHT, ResourceBorderTexture.BORDERED_BACKGROUND));
        dialog.addWidget(new ButtonWidget(x + WIDTH / 2 - 30 - 20, y + HEIGHT - 32, 40, 20, cd -> {
            dialog.close();
            if (result != null) result.accept(selected.get());
        }).setButtonTexture(new ResourceTexture("ldlib:textures/gui/darkened_slot.png"), new TextTexture("ldlib.gui.tips.confirm", -1).setDropShadow(true)).setHoverBorderTexture(1, 0xff000000));
        dialog.addWidget(new ButtonWidget(x + WIDTH / 2 + 30 - 20, y + HEIGHT - 32, 40, 20, cd -> {
            dialog.close();
            if (result != null) result.accept(null);
        }).setButtonTexture(new ResourceTexture("ldlib:textures/gui/darkened_slot.png"), new TextTexture("ldlib.gui.tips.cancel", 0xffff0000).setDropShadow(true)).setHoverBorderTexture(1, 0xff000000));
        if (isSelector) {
            dialog.addWidget(new ImageWidget(x + 8, y + HEIGHT / 2 - 5, WIDTH - 16, 20, new GuiTextureGroup(new ColorBorderTexture(1, -1), new ColorRectTexture(0xff000000))));
            dialog.addWidget(new ImageWidget(x + 8, y + HEIGHT / 2 - 5, WIDTH - 16, 20,
                    new TextTexture("", -1).setWidth(WIDTH - 16).setType(TextTexture.TextType.ROLL)
                            .setSupplier(() -> {
                                if (selected.get() != null) {
                                    return selected.get().toString();
                                }
                                return "no file selected";
                            })));
        } else {
            dialog.addWidget(new TextFieldWidget(x + WIDTH / 2 - 70, y + HEIGHT / 2 - 10, 140, 20,  ()->{
                File file = selected.get();
                if (file != null && !file.isDirectory()) {
                    return selected.get().getName();
                }
                return "";
            }, res->{
                File file = selected.get();
                if (file == null) return;
                if (file.isDirectory()) {
                    selected.set(new File(file, res));
                } else {
                    selected.set(new File(file.getParent(), res));
                }
            }));
        }
        dialog.addWidget(new ButtonWidget(x + 15, y + 15, 20, 20, cd -> {
            File file = selected.get();
            if (file != null) {
                Util.getPlatform().openFile(file.isDirectory() ? file : file.getParentFile());
            }
        }).setButtonTexture(new ResourceTexture("ldlib:textures/gui/darkened_slot.png"), new TextTexture("F", -1).setDropShadow(true)).setHoverBorderTexture(1, 0xff000000).setHoverTooltips("ldlib.gui.tips.open_folder"));
        dialog.addWidget(new ImageWidget(x + 15, y + 20, WIDTH - 30,10, new TextTexture(title, -1).setWidth(WIDTH - 30).setDropShadow(true)));
        //        dialog.addWidget(new LabelWidget(x + WIDTH / 2, y + 11, ()->title).setTextColor(-1));
        return dialog;
    }
}
