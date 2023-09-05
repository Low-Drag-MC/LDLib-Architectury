package com.lowdragmc.lowdraglib.gui.factory;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.ui.UIEditor;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import com.lowdragmc.lowdraglib.gui.widget.custom.PlayerInventoryWidget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class UIEditorFactory extends UIFactory<UIEditorFactory> implements IUIHolder {

	public static final UIEditorFactory INSTANCE = new UIEditorFactory();

	private UIEditorFactory(){
		super(LDLib.location("ui_editor"));
	}

	@Override
	protected ModularUI createUITemplate(UIEditorFactory holder, Player entityPlayer) {
		return createUI(entityPlayer);
	}

	@Override
	protected UIEditorFactory readHolderFromSyncData(FriendlyByteBuf syncData) {
		return this;
	}

	@Override
	protected void writeHolderToSyncData(FriendlyByteBuf syncData, UIEditorFactory holder) {

	}

	@Override
	public ModularUI createUI(Player entityPlayer) {
		var playerInventory = new PlayerInventoryWidget();
		playerInventory.setPlayer(entityPlayer);
		var pos = entityPlayer.getOnPos();
		var sceneWidget = new SceneWidget(50, 50, 200, 200, entityPlayer.level());
		sceneWidget.setRenderedCore(List.of(pos, pos.below(), pos.above(), pos.north(), pos.south(), pos.east(), pos.west()), null);
		return new ModularUI(this, entityPlayer)
				.widget(new UIEditor(LDLib.getLDLibDir()))
				.widget(playerInventory)
				.widget(sceneWidget);
	}

	@Override
	public boolean isInvalid() {
		return false;
	}

	@Override
	public boolean isRemote() {
		return LDLib.isRemote();
	}

	@Override
	public void markAsDirty() {

	}
}
