package com.lowdragmc.lowdraglib.core.mixins;

import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    public void injectRenderItem(ItemStack stack,
                                 ItemTransforms.TransformType transformType,
                                 boolean leftHand, PoseStack matrixStack,
                                 MultiBufferSource buffer, int combinedLight,
                                 int combinedOverlay, BakedModel model,
                                 CallbackInfo ci){
        if (stack.getItem() instanceof IItemRendererProvider && !IItemRendererProvider.disabled.get()) {
            IRenderer renderer =((IItemRendererProvider) stack.getItem()).getRenderer(stack);
            if (renderer != null) {
                renderer.renderItem(stack, transformType, leftHand, matrixStack, buffer, combinedLight, combinedOverlay, model);
                ci.cancel();
            }
        }
    }
}
