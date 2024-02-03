package com.lowdragmc.lowdraglib.gui.editor.data.resource;

import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.renderer.ISerializableRenderer;
import com.lowdragmc.lowdraglib.gui.editor.ui.ResourcePanel;
import com.lowdragmc.lowdraglib.gui.editor.ui.resource.IRendererResourceContainer;
import com.lowdragmc.lowdraglib.gui.editor.ui.resource.ResourceContainer;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class IRendererResource extends Resource<IRenderer> {
    @Override
    public String name() {
        return "renderer";
    }

    @Override
    public ResourceContainer<IRenderer, ? extends Widget> createContainer(ResourcePanel resourcePanel) {
        return new IRendererResourceContainer(this, resourcePanel);
    }

    @Nullable
    @Override
    public Tag serialize(IRenderer renderer) {
        if (renderer instanceof ISerializableRenderer serializableRenderer) {
            return ISerializableRenderer.serializeWrapper(serializableRenderer);
        }
        return null;
    }

    @Override
    public IRenderer deserialize(Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            return ISerializableRenderer.deserializeWrapper(compoundTag);
        }
        return IRenderer.EMPTY;
    }
}
