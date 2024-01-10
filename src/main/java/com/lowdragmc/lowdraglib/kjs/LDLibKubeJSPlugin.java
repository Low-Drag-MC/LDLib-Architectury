package com.lowdragmc.lowdraglib.kjs;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import org.joml.Vector3f;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;

/**
 * @author KilaBash
 * @date 2023/3/26
 * @implNote GregTechKubeJSPlugin
 */
public class LDLibKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void registerClasses(ScriptType type, ClassFilter filter) {
        super.registerClasses(type, filter);
        filter.allow("com.lowdragmc.lowdraglib");
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        super.registerBindings(event);
        // texture
        event.add("ResourceTexture", ResourceTexture.class);
        event.add("FillDirection", ProgressTexture.FillDirection.class);
        event.add("ProgressTexture", ProgressTexture.class);
        event.add("AnimationTexture", AnimationTexture.class);
        event.add("ColorRectTexture", ColorRectTexture.class);
        event.add("ColorRectTexture", ColorRectTexture.class);
        event.add("ItemStackTexture", ItemStackTexture.class);
        event.add("ResourceBorderTexture", ResourceBorderTexture.class);
        event.add("ShaderTexture", ShaderTexture.class);
        event.add("TextTexture", TextTexture.class);
        event.add("GuiTextureGroup", GuiTextureGroup.class);
        // LDLib Widget
        event.add("ModularUI", ModularUI.class);
        event.add("BlockSelectorWidget", BlockSelectorWidget.class);
        event.add("ButtonWidget", ButtonWidget.class);
        event.add("DialogWidget", DialogWidget.class);
        event.add("DraggableScrollableWidgetGroup", DraggableScrollableWidgetGroup.class);
        event.add("DraggableWidgetGroup", DraggableWidgetGroup.class);
        event.add("ImageWidget", ImageWidget.class);
        event.add("LabelWidget", LabelWidget.class);
        event.add("PhantomFluidWidget", PhantomFluidWidget.class);
        event.add("PhantomSlotWidget", PhantomSlotWidget.class);
        event.add("SceneWidget", SceneWidget.class);
        event.add("SelectableWidgetGroup", SelectableWidgetGroup.class);
        event.add("SlotWidget", SlotWidget.class);
        event.add("SwitchWidget", SwitchWidget.class);
        event.add("TabButton", TabButton.class);
        event.add("TabContainer", TabContainer.class);
        event.add("TankWidget", TankWidget.class);
        event.add("TextBoxWidget", TextBoxWidget.class);
        event.add("TextFieldWidget", TextFieldWidget.class);
        event.add("TreeListWidget", TreeListWidget.class);
        event.add("WidgetGroup", WidgetGroup.class);
        event.add("ProgressWidget", ProgressWidget.class);
        // math
        event.add("Vector3f", Vector3f.class);
        event.add("GuiSize", Size.class);
        event.add("GuiPos", Position.class);
    }

    @Override
    public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
        super.registerTypeWrappers(type, typeWrappers);
    }

}
