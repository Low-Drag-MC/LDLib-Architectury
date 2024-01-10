package com.lowdragmc.lowdraglib.gui.modular;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface WidgetUIAccess {

    boolean attemptMergeStack(ItemStack itemStack, boolean fromContainer, boolean simulate);

    void writeClientAction(Widget widget, int id, Consumer<FriendlyByteBuf> payloadWriter);

    void writeUpdateInfo(Widget widget, int id, Consumer<FriendlyByteBuf> payloadWriter);

}
