package com.lowdragmc.lowdraglib.client.model.fabric;

import com.lowdragmc.lowdraglib.client.model.custommodel.CustomBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.renderer.VanillaModelEncoder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CustomBakedModelImpl extends CustomBakedModel {

    public CustomBakedModelImpl(BakedModel parent) {
        super(parent);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter level, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        VanillaModelEncoder.emitBlockQuads(new BakedModel() {
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
                return CustomBakedModelImpl.this.getCustomQuads(level, pos, state, direction, random);
            }

            @Override
            public boolean useAmbientOcclusion() {
                return CustomBakedModelImpl.this.useAmbientOcclusion();
            }

            @Override
            public boolean isGui3d() {
                return CustomBakedModelImpl.this.isGui3d();
            }

            @Override
            public boolean usesBlockLight() {
                return CustomBakedModelImpl.this.usesBlockLight();
            }

            @Override
            public boolean isCustomRenderer() {
                return CustomBakedModelImpl.this.isCustomRenderer();
            }

            @Override
            public TextureAtlasSprite getParticleIcon() {
                return CustomBakedModelImpl.this.getParticleIcon();
            }

            @Override
            public ItemTransforms getTransforms() {
                return CustomBakedModelImpl.this.getTransforms();
            }

            @Override
            public ItemOverrides getOverrides() {
                return CustomBakedModelImpl.this.getOverrides();
            }
        }, state, randomSupplier, context, context.getEmitter());
    }
}
