package com.lowdragmc.lowdraglib.gui.factory;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.editor.ui.Editor;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class UIEditorFactory extends UIFactory<UIEditorFactory> implements IUIHolder {

	public static final UIEditorFactory INSTANCE = new UIEditorFactory();

	private UIEditorFactory(){

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
//		BlockPos pos = entityPlayer.getOnPos();
//		SceneWidget sceneWidget = new SceneWidget(30, 10, 100, 100, entityPlayer.level).useCacheBuffer();
//		sceneWidget.setRenderedCore(List.of(pos, pos.above(), pos.below(),
//				pos.relative(Direction.NORTH), pos.relative(Direction.SOUTH), pos.relative(Direction.EAST), pos.relative(Direction.WEST)), null);
//		return new ModularUI(150,200, this, entityPlayer)
//				.widget(new ImageWidget(0, 0, 150, 200, new ColorBorderTexture(1, -1)))
//				.widget(sceneWidget)
//				.widget(new ButtonWidget(10, 10, 40, 20, ResourceBorderTexture.BUTTON_COMMON, null));
		return new ModularUI(this, entityPlayer)
				.widget(new Editor(LDLib.location));
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
