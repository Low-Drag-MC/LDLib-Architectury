package com.lowdragmc.lowdraglib.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.util.TreeNode;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class TreeListWidget<K, T> extends Widget {
    private static final int ITEM_HEIGHT = 11;
    protected int scrollOffset;
    protected List<TreeNode<K, T>> list;
    protected TreeNode<K, T> selected;
    protected IGuiTexture nodeTexture;
    protected IGuiTexture leafTexture;
    protected Consumer<TreeNode<K, T>> onSelected;
    protected Function<K, IGuiTexture> keyIconSupplier;
    protected Function<K, String> keyNameSupplier;
    protected Function<T, IGuiTexture> contentIconSupplier;
    protected Function<T, String> contentNameSupplier;
    protected boolean canSelectNode;
    private int tick;

    public TreeListWidget(int xPosition, int yPosition, int width, int height, TreeNode<K, T> root, Consumer<TreeNode<K, T>> onSelected) {
        super(xPosition, yPosition, width, height);
        list = new ArrayList<>();
        if (root.getChildren() != null) {
            list.addAll(root.getChildren());
        }
        this.onSelected = onSelected;
    }

    public TreeListWidget<K, T> canSelectNode(boolean canSelectNode) {
        this.canSelectNode = canSelectNode;
        return this;
    }

    public TreeListWidget<K, T> setBackground(IGuiTexture background) {
        super.setBackground(background);
        return this;
    }

    public TreeListWidget<K, T> setNodeTexture(IGuiTexture nodeTexture) {
        this.nodeTexture = nodeTexture;
        return this;
    }

    public TreeListWidget<K, T> setLeafTexture(IGuiTexture leafTexture) {
        this.leafTexture = leafTexture;
        return this;
    }

    public TreeListWidget<K, T> setContentIconSupplier(Function<T, IGuiTexture> iconSupplier) {
        contentIconSupplier = iconSupplier;
        return this;
    }

    public TreeListWidget<K, T> setKeyIconSupplier(Function<K, IGuiTexture> iconSupplier) {
        keyIconSupplier = iconSupplier;
        return this;
    }

    public TreeListWidget<K, T> setContentNameSupplier(Function<T, String> nameSupplier) {
        contentNameSupplier = nameSupplier;
        return this;
    }

    public TreeListWidget<K, T> setKeyNameSupplier(Function<K, String> nameSupplier) {
        keyNameSupplier = nameSupplier;
        return this;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void updateScreen() {
        tick++;
        if (nodeTexture != null) nodeTexture.updateTick();
        if (leafTexture != null) leafTexture.updateTick();
    }


    @Override
    @Environment(EnvType.CLIENT)
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        if (this.isMouseOverElement(mouseX, mouseY)) {
            int moveDelta = (int) (-Mth.clamp(wheelDelta, -1, 1) * 5);
            this.scrollOffset = Mth.clamp(scrollOffset + moveDelta, 0, Math.max(list.size() * ITEM_HEIGHT - getSize().height, 0));
            return true;
        }
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void drawInBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawInBackground(graphics, mouseX, mouseY, partialTicks);
        int x = getPosition().x;
        int y = getPosition().y;
        int width = getSize().width;
        int height = getSize().height;
        if (backgroundTexture == null) {
            DrawerHelper.drawGradientRect(graphics, x, y, width, height, 0x8f000000, 0x8f000000);
        }

        graphics.enableScissor(x, y, x + width, y + height);
        Font fr = Minecraft.getInstance().font;
        int minToRender = scrollOffset / ITEM_HEIGHT;
        int maxToRender = Math.min(list.size(), height / ITEM_HEIGHT + 2 + minToRender);
        for (int i = minToRender; i < maxToRender; i++) {
            RenderSystem.setShaderColor(1,1,1,1);
            TreeNode<K, T> node = list.get(i);
            int sX = x + 10 * node.dimension;
            int sY = y - scrollOffset + i * ITEM_HEIGHT;
            String name = node.toString();
            if (node.isLeaf()) {
                if (leafTexture != null) {
                    leafTexture.draw(graphics, mouseX, mouseY, x, sY, width, ITEM_HEIGHT);
                } else {
                    DrawerHelper.drawSolidRect(graphics, x, sY, width, ITEM_HEIGHT, 0xffff0000);
                }
                if (node.getContent() != null) {
                    String nameS = contentNameSupplier == null ? null : contentNameSupplier.apply(node.getContent());
                    name = nameS == null ? name : nameS;
                    IGuiTexture icon = contentIconSupplier == null ? null : contentIconSupplier.apply(node.getContent());
                    if (icon != null) {
                        icon.draw(graphics, mouseX, mouseY, sX - 9, sY + 1, 8, 8);
                    }
                }
            } else {
                if (nodeTexture != null) {
                    nodeTexture.draw(graphics, mouseX, mouseY, x, sY, width, ITEM_HEIGHT);
                } else {
                    DrawerHelper.drawSolidRect(graphics, x, sY, width, ITEM_HEIGHT, 0xffffff00);
                }
                String nameS = keyNameSupplier == null ? null : keyNameSupplier.apply(node.getKey());
                name = nameS == null ? name : nameS;
                IGuiTexture icon = keyIconSupplier == null ? null : keyIconSupplier.apply(node.getKey());
                if (icon != null) {
                    icon.draw(graphics, mouseX, mouseY, sX - 9, sY + 1, 8, 8);
                }
            }
            if (node == selected) {
                DrawerHelper.drawSolidRect(graphics, x, sY, width, ITEM_HEIGHT, 0x7f000000);
            }
            int textW = Math.max(width - 10 * node.dimension, 10);
            List<FormattedText> list = fr.getSplitter().splitLines(LocalizationUtils.format(name), textW, Style.EMPTY);
            graphics.drawString(fr, list.get(Math.abs((tick / 20) % list.size())).getString(), sX, sY + 2, 0xff000000, false);
        }
        graphics.disableScissor();

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1,1,1,1);
    }

    public TreeNode<K, T> jumpTo(List<K> path) {
        list.removeIf(node->node.dimension != 1);
        this.selected = null;
        int dim = 1;
        int index = 0;
        boolean flag = false;
        TreeNode<K, T> node = null;
        for (K key : path) {
            flag = false;
            for (int i = index; i < list.size(); i++) {
                node = list.get(i);
                if (node.dimension != dim) {
                    return null;
                } else if (node.getKey().equals(key)) { //expand
                    if(!node.isLeaf() && path.size() > dim) {
                        for (int j = 0; j < node.getChildren().size(); j++) {
                            list.add(index + 1 + j, node.getChildren().get(j));
                        }
                    }
                    index++;
                    dim++;
                    flag = true;
                    break;
                } else {
                    index++;
                }
            }
            if (!flag) return null;
        }
        if (flag) {
            this.selected = node;
            this.scrollOffset = Mth.clamp(ITEM_HEIGHT * (index - 1), 0, Math.max(list.size() * ITEM_HEIGHT - getSize().height, 0));
            return this.selected;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseOverElement(mouseX, mouseY)) {
            int index = (int) (((mouseY - getPosition().y) + scrollOffset) / ITEM_HEIGHT);
            if (index < list.size()) {
                TreeNode<K, T> node = list.get(index);
                if (node.isLeaf()) {
                    if (node != this.selected) {
                        this.selected = node;
                        if (onSelected != null){
                            onSelected.accept(node);
                        }
                    }
                } else {
                    if (canSelectNode && this.selected != node) {
                        this.selected = node;
                        if (onSelected != null){
                            onSelected.accept(node);
                        }
                    } else if (node.getChildren().size() > 0 && list.contains(node.getChildren().get(0))){
                        removeNode(node);
                    } else {
                        for (int i = 0; i < node.getChildren().size(); i++) {
                            list.add(index + 1 + i, node.getChildren().get(i));
                        }
                    }
                }
                playButtonClickSound();
            }
            return true;
        }
        return false;
    }

    private void removeNode(TreeNode<?, T> node) {
        if(node.isLeaf()) return;
        for (TreeNode<?, T> child : node.getChildren()) {
            list.remove(child);
            removeNode(child);
        }
    }
}
