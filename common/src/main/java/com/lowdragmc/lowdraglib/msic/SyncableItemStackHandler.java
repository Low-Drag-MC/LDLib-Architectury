package com.lowdragmc.lowdraglib.msic;

import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * @author KilaBash
 * @date 2022/9/7
 * @implNote SyncableItemStackHandler
 */
@Accessors(chain = true)
public class SyncableItemStackHandler extends ItemStackHandler implements IContentChangeAware<SyncableItemStackHandler> {

    @Getter
    @Setter
    private Runnable onContentsChanged = () -> {};

    @Setter
    private Function<ItemStack, Boolean> filter;

    public SyncableItemStackHandler() {
    }

    public SyncableItemStackHandler(int size) {
        super(size);
    }

    public SyncableItemStackHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return filter == null || filter.apply(stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        onContentsChanged.run();
    }

}
