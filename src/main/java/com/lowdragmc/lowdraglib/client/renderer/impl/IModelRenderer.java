package com.lowdragmc.lowdraglib.client.renderer.impl;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;
import com.lowdragmc.lowdraglib.client.renderer.IItemRendererProvider;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public class IModelRenderer implements IRenderer {

    public final ResourceLocation modelLocation;
    @OnlyIn(Dist.CLIENT)
    protected BakedModel itemModel;
    @OnlyIn(Dist.CLIENT)
    protected Map<Direction, BakedModel> blockModels;

    protected IModelRenderer() {
        modelLocation = null;
    }

    public IModelRenderer(ResourceLocation modelLocation) {
        this.modelLocation = modelLocation;
        if (LDLib.isClient()) {
            blockModels = new ConcurrentHashMap<>();
            registerEvent();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        BakedModel model = getItemBakedModel();
        if (model == null) {
            return IRenderer.super.getParticleTexture();
        }
        return model.getParticleIcon();
    }

    @OnlyIn(Dist.CLIENT)
    protected UnbakedModel getModel() {
        return ModelFactory.getUnBakedModel(modelLocation);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderItem(ItemStack stack,
                           ItemDisplayContext transformType,
                           boolean leftHand, PoseStack poseStack,
                           MultiBufferSource buffer, int combinedLight,
                           int combinedOverlay, BakedModel model) {
        IItemRendererProvider.disabled.set(true);
        model = getItemBakedModel(stack);
        if (model != null) {
            Minecraft.getInstance().getItemRenderer().render(stack, transformType, leftHand, poseStack, buffer, combinedLight, combinedOverlay, model);
        }
        IItemRendererProvider.disabled.set(false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean useBlockLight(ItemStack stack) {
        var model = getItemBakedModel(stack);
        if (model != null) {
            return model.usesBlockLight();
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean useAO() {
        var model = getItemBakedModel();
        if (model != null) {
            return model.useAmbientOcclusion();
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> renderModel(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, @Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        var ibakedmodel = getBlockBakedModel(pos, level);
        if (ibakedmodel == null) return Collections.emptyList();
        return ibakedmodel.getQuads(state, side, rand);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    protected BakedModel getItemBakedModel() {
        if (itemModel == null) {
            var model = getModel();
            if (model instanceof BlockModel blockModel && blockModel.getRootModel() == ModelBakery.GENERATION_MARKER) {
                // fabric doesn't help us to fix vanilla bakery, so we have to do it ourselves
                model = ModelFactory.ITEM_MODEL_GENERATOR.generateBlockModel(this::materialMapping, blockModel);
            }
            itemModel = model.bake(
                    ModelFactory.getModelBaker(),
                    this::materialMapping,
                    BlockModelRotation.X0_Y0,
                    modelLocation);
        }
        return itemModel;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    protected BakedModel getItemBakedModel(ItemStack itemStack) {
        return getItemBakedModel();
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    protected BakedModel getBlockBakedModel(BlockPos pos, BlockAndTintGetter blockAccess) {
        return getRotatedModel(Direction.NORTH);
    }

    @OnlyIn(Dist.CLIENT)
    public BakedModel getRotatedModel(Direction frontFacing) {
        return blockModels.computeIfAbsent(frontFacing, facing -> getModel().bake(
                ModelFactory.getModelBaker(),
                this::materialMapping,
                ModelFactory.getRotation(facing),
                modelLocation));
    }

    @OnlyIn(Dist.CLIENT)
    protected TextureAtlasSprite materialMapping(Material material) {
        return material.sprite();
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {
        if (atlasName.equals(TextureAtlas.LOCATION_BLOCKS)) {
            itemModel = null;
            blockModels.clear();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onAdditionalModel(Consumer<ResourceLocation> registry) {
        registry.accept(modelLocation);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isGui3d() {
        var model = getItemBakedModel();
        if (model == null) {
            return IRenderer.super.isGui3d();
        }
        return model.isGui3d();
    }
}
