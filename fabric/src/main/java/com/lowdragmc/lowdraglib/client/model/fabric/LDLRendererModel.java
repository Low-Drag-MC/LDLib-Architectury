package com.lowdragmc.lowdraglib.client.model.fabric;

import com.lowdragmc.lowdraglib.client.renderer.IBlockRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2022/05/28
 * @implNote LDLModel, use vanilla way to improve model rendering
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LDLRendererModel implements UnbakedModel {
    public static final LDLRendererModel INSTANCE = new LDLRendererModel();

    private LDLRendererModel() {}
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBakery modelBakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation location) {
        return new RendererBakedModel();
    }

    public static final class RendererBakedModel implements BakedModel, FabricBakedModel {
        private IRenderer renderer = IRenderer.EMPTY;

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
            return Collections.emptyList();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean usesBlockLight() {
            return false;
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


        @Override
        public boolean isVanillaAdapter() {
            return false;
        }

        @Override
        public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
            if (state.getBlock() instanceof IBlockRendererProvider rendererProvider) {
                IRenderer renderer = rendererProvider.getRenderer(state);
                if (renderer != null) {
                    context.bakedModelConsumer().accept(new BakedModel() {
                        @Override
                        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
                            return renderer.renderModel(blockView, pos, state, direction, random);
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
                            return renderer.useBlockLight(ItemStack.EMPTY);
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
                }
            }
        }

        @Override
        public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
            /** use mixin {@link com.lowdragmc.lowdraglib.core.mixins.ItemRendererMixin}*/
        }

    }

    public static final class Loader implements ModelResourceProvider {

        public static final Loader INSTANCE = new Loader();
        private Loader() {}

        @Override
        public UnbakedModel loadModelResource(ResourceLocation resourceId, ModelProviderContext context) {
            return resourceId.equals(new ResourceLocation("ldlib:block/renderer_model")) ? LDLRendererModel.INSTANCE : null;
        }
    }
}
