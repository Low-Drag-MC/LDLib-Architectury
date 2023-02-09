package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.syncdata.blockentity.IAsyncAutoSyncBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAutoPersistBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAutoSyncBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author KilaBash
 * @date 2022/11/27
 * @implNote BlockEntityMixin
 */
@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Shadow public abstract void load(CompoundTag pTag);

    @Inject(method = "saveAdditional", at = @At(value = "RETURN"))
    private void injectSaveAdditional(CompoundTag pTag, CallbackInfo ci) {
        if (this instanceof IAutoPersistBlockEntity autoPersistBlockEntity) {
            autoPersistBlockEntity.saveManagedPersistentData(pTag, false);
        }
    }

    @Inject(method = "load", at = @At(value = "RETURN"))
    private void injectLoad(CompoundTag pTag, CallbackInfo ci) {
        if (this instanceof IAutoPersistBlockEntity autoPersistBlockEntity) {
            autoPersistBlockEntity.loadManagedPersistentData(pTag);
        }
    }

    @Inject(method = "setRemoved", at = @At(value = "RETURN"))
    private void injectSetRemoved(CallbackInfo ci) {
        if (this instanceof IAsyncAutoSyncBlockEntity autoSyncBlockEntity) {
            autoSyncBlockEntity.onInValid();
        }
        if (this instanceof IAutoSyncBlockEntity autoSyncBlockEntity) {
            // update fields before removed
            for (IRef field : autoSyncBlockEntity.getNonLazyFields()) {
                field.update();
            }
        }
    }

    @Inject(method = "clearRemoved", at = @At(value = "RETURN"))
    private void injectClearRemoved(CallbackInfo ci) {
        if (this instanceof IAsyncAutoSyncBlockEntity autoSyncBlockEntity) {
            autoSyncBlockEntity.onValid();
        }
    }

}
