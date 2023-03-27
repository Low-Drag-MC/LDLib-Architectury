package com.lowdragmc.lowdraglib.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface IRenderer {
    Set<IRenderer> EVENT_REGISTERS = new HashSet<>();
    IRenderer EMPTY = new IRenderer() {};

    @Environment(EnvType.CLIENT)
    default void renderItem(ItemStack stack,
                    ItemTransforms.TransformType transformType,
                    boolean leftHand, PoseStack matrixStack,
                    MultiBufferSource buffer, int combinedLight,
                    int combinedOverlay, BakedModel model) {

    }

    @Environment(EnvType.CLIENT)
    default List<BakedQuad> renderModel(BlockAndTintGetter level, BlockPos pos, BlockState state, Direction side, RandomSource rand) {
        return Collections.emptyList();
    }

    @Environment(EnvType.CLIENT)
    default void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {

    }

    @Environment(EnvType.CLIENT)
    default void onAdditionalModel(Consumer<ResourceLocation> registry) {

    }

    @Environment(EnvType.CLIENT)
    default void registerEvent() {
        EVENT_REGISTERS.add(this);
    }

    default boolean isRaw() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    default boolean hasTESR(BlockEntity blockEntity) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    default boolean isGlobalRenderer(BlockEntity blockEntity) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    default void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

    }

    @Environment(EnvType.CLIENT)
    @Nonnull
    default TextureAtlasSprite getParticleTexture() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation());
    }

    @Environment(EnvType.CLIENT)
    default boolean useAO() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    default boolean useAO(BlockState state) {
        return useAO();
    }

    @Environment(EnvType.CLIENT)
    default boolean useBlockLight(ItemStack stack) {
        return false;
    }

    /**
     * Should we rebake quads for mcmeta data?
     */
    @Environment(EnvType.CLIENT)
    default boolean reBakeCustomQuads() {
        return false;
    }
}
