package com.gregtechceu.gtlib.core.mixins;

import com.gregtechceu.gtlib.client.renderer.IItemRendererProvider;
import com.gregtechceu.gtlib.client.renderer.IRenderer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemModelShaper.class)
public class ItemModelShaperMixin {

    @Shadow @Final private Int2ObjectMap<BakedModel> shapesCache;

    @Shadow
    private static int getIndex(Item item) {
        throw new RuntimeException("Mixin apply failed!");
    }

    @Inject(method = "getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), cancellable = true)
    public void injectGetModel(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        if (stack.getItem() instanceof IItemRendererProvider provider) {
            IRenderer renderer = provider.getRenderer(stack);
            if (renderer != null) {
                int itemIndex = getIndex(stack.getItem());

                shapesCache.putIfAbsent(itemIndex, new BakedModel() {
                    @Override
                    public List<BakedQuad> getQuads(@org.jetbrains.annotations.Nullable BlockState state, @org.jetbrains.annotations.Nullable Direction direction, RandomSource random) {
                        return renderer.renderModel(null, null, state, direction, random);
                    }

                    @Override
                    public boolean useAmbientOcclusion() {
                        return renderer.useAO();
                    }

                    @Override
                    public boolean isGui3d() {
                        return true;
                    }

                    @Override
                    public boolean usesBlockLight() {
                        return renderer.useBlockLight(stack);
                    }

                    @Override
                    public boolean isCustomRenderer() {
                        return false;
                    }

                    @Override
                    public TextureAtlasSprite getParticleIcon() {
                        return renderer.getParticleTexture();
                    }

                    @Override
                    public ItemTransforms getTransforms() {
                        return ItemTransforms.NO_TRANSFORMS;
                    }

                    @Override
                    public ItemOverrides getOverrides() {
                        return ItemOverrides.EMPTY;
                    }
                });
                cir.setReturnValue(shapesCache.get(itemIndex));
            }

        }
    }
}
