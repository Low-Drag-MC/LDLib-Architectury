package com.lowdragmc.lowdraglib.gui.util;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import net.minecraft.util.Tuple;

import java.util.Stack;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2022/12/5
 * @implNote TreeBuilder
 */
public class TreeBuilder<K, V> {
    protected final Stack<TreeNode<K, V>> stack = new Stack<>();

    public TreeBuilder(K key) {
        stack.push(new TreeNode<>(0, key));
    }

    public static <K, V> TreeBuilder<K, V> start(K key){
        return new TreeBuilder<>(key);
    }

    public TreeBuilder<K, V> branch(K key, Consumer<TreeBuilder<K, V>> builderConsumer) {
        var children = stack.peek().getChildren();
        if (children != null && !children.isEmpty()) {
            for (var child : children) {
                if (!child.isLeaf() && child.key.equals(key)) {
                    stack.push(child);
                    builderConsumer.accept(this);
                    endBranch();
                    return this;
                }
            }
        }

        stack.push(stack.peek().getOrCreateChild(key));
        builderConsumer.accept(this);
        endBranch();
        return this;
    }

    public TreeBuilder<K, V> startBranch(K key) {
        stack.push(stack.peek().getOrCreateChild(key));
        return this;
    }

    public TreeBuilder<K, V> endBranch() {
        stack.pop();
        return this;
    }

    public TreeBuilder<K, V> leaf(K key, V content) {
        stack.peek().addContent(key, content);
        return this;
    }

    public TreeBuilder<K, V> remove(K key) {
        stack.peek().removeChild(key);
        return this;
    }

    public TreeNode<K, V> build() {
        while (stack.size() > 1) {
            stack.pop();
        }
        return stack.peek();
    }

    public static class Menu extends TreeBuilder<Tuple<IGuiTexture, String>, Runnable> {
        public static Tuple<IGuiTexture, String> CROSS_LINE = new Tuple<>(IGuiTexture.EMPTY, "");

        private Menu(Tuple<IGuiTexture, String> key) {
            super(key);
        }

        public static Menu start(){
            return new Menu(new Tuple<>(IGuiTexture.EMPTY, ""));
        }

        public Menu crossLine() {
            stack.peek().createChild(CROSS_LINE);
            return this;
        }

        public Menu branch(IGuiTexture icon, String name, Consumer<Menu> menuConsumer) {
            branch(new Tuple<>(icon, name), builder -> menuConsumer.accept(this));
            return this;
        }

        public Menu branch(String name, Consumer<Menu> menuConsumer) {
            var children = stack.peek().getChildren();
            if (children != null && !children.isEmpty()) {
                for (TreeNode<Tuple<IGuiTexture, String>, Runnable> child : children) {
                    if (!child.isLeaf() && child.getKey().getB().equals(name)) {
                        stack.push(child);
                        menuConsumer.accept(this);
                        endBranch();
                        return this;
                    }
                }
            }
            return branch(IGuiTexture.EMPTY, name, menuConsumer);
        }

        public Menu endBranch() {
            super.endBranch();
            return this;
        }

        public Menu leaf(IGuiTexture icon, String name, Runnable runnable) {
            super.leaf(new Tuple<>(icon, name), runnable);
            return this;
        }

        public Menu leaf(String name, Runnable runnable) {
            super.leaf(new Tuple<>(IGuiTexture.EMPTY, name), runnable);
            return this;
        }

        public Menu remove(String name) {
            var children = stack.peek().getChildren();
            if (children != null && !children.isEmpty()) {
                for (TreeNode<Tuple<IGuiTexture, String>, Runnable> child : children) {
                    if (child.getKey().getB().equals(name)) {
                        stack.peek().removeChild(child.getKey());
                        return this;
                    }
                }
            }
            return this;
        }

        public static IGuiTexture getIcon(Tuple<IGuiTexture, String> key) {
            return key.getA();
        }

        public static String getName(Tuple<IGuiTexture, String> key) {
            return key.getB();
        }

        public static void handle(TreeNode<Tuple<IGuiTexture, String>, Runnable> node) {
            if (node.isLeaf() && node.getContent() != null) {
                node.getContent().run();
            }
        }

        public static boolean isCrossLine(Tuple<IGuiTexture, String> key) {
            return key == CROSS_LINE;
        }
    }

}
