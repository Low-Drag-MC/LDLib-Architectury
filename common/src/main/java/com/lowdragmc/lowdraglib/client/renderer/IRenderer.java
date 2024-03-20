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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface IRenderer {
    Set<IRenderer> EVENT_REGISTERS = new HashSet<>();
    IRenderer EMPTY = new IRenderer() {};

    /**
     * Render itemstack.
     */
    @Environment(EnvType.CLIENT)
    default void renderItem(ItemStack stack,
                    ItemTransforms.TransformType transformType,
                    boolean leftHand, PoseStack matrixStack,
                    MultiBufferSource buffer, int combinedLight,
                    int combinedOverlay, BakedModel model) {

    }

    /**
     * Render static block model.
     */
    @Environment(EnvType.CLIENT)
    default List<BakedQuad> renderModel(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, @Nullable BlockState state, @Nullable  Direction side, RandomSource rand) {
        return Collections.emptyList();
    }

    /**
     * Register TextureSprite here.
     */
    @Environment(EnvType.CLIENT)
    default void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {

    }

    /**
     * Register additional model here.
     */
    @Environment(EnvType.CLIENT)
    default void onAdditionalModel(Consumer<ResourceLocation> registry) {

    }

    /**
     * If the renderer requires event registration either {@link #onPrepareTextureAtlas} or {@link #onAdditionalModel}, call this method in the constructor.
     */
    @Environment(EnvType.CLIENT)
    default void registerEvent() {
        synchronized (EVENT_REGISTERS) {
            EVENT_REGISTERS.add(this);
        }
    }

    /**
     * If the renderer is ready to be rendered.
     */
    default boolean isRaw() {
        return false;
    }

    /**
     * Does the block entity have the TESR {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer}.
     */
    @Environment(EnvType.CLIENT)
    default boolean hasTESR(BlockEntity blockEntity) {
        return false;
    }

    /**
     * Is the TESR {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer} global.
     */
    @Environment(EnvType.CLIENT)
    default boolean isGlobalRenderer(BlockEntity blockEntity) {
        return false;
    }

    /**
     * Get the view distance for TESR.
     */
    @Environment(EnvType.CLIENT)
    default int getViewDistance() {
        return 64;
    }

    /**
     * Should the TESR {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer} render.
     */
    @Environment(EnvType.CLIENT)
    default boolean shouldRender(BlockEntity blockEntity, Vec3 cameraPos) {
        return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(cameraPos, this.getViewDistance());
    }

    /**
     * Render the TESR {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer}.
     */
    @Environment(EnvType.CLIENT)
    default void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

    }

    /**
     * Get the particle texture.
     */
    @Environment(EnvType.CLIENT)
    @Nonnull
    default TextureAtlasSprite getParticleTexture() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation());
    }

    /**
     * Whether to apply AO for the model.
     */
    @Environment(EnvType.CLIENT)
    default boolean useAO() {
        return false;
    }

    /**
     * Whether to apply AO for the model.
     */
    @Environment(EnvType.CLIENT)
    default boolean useAO(BlockState state) {
        return useAO();
    }

    /**
     * Whether to apply block light during the itemstack rendering.
     */
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

    /**
     * Offset for rebake's quads sides while {@link #reBakeCustomQuads()} return true.
     */
    @Environment(EnvType.CLIENT)
    default float reBakeCustomQuadsOffset() {
        return 0.002f;
    }

    /**
     * Whether to apply gui 3d transform during itemstack rendering.
     */
    @Environment(EnvType.CLIENT)
    default boolean isGui3d() {
        return true;
    }
}
