package com.lowdragmc.lowdraglib.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
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
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

public interface IRenderer {
    Set<IRenderer> EVENT_REGISTERS = new HashSet<>();
    IRenderer EMPTY = new IRenderer() {};

    @OnlyIn(Dist.CLIENT)
    default void renderItem(ItemStack stack,
                    ItemDisplayContext transformType,
                    boolean leftHand, PoseStack poseStack,
                    MultiBufferSource buffer, int combinedLight,
                    int combinedOverlay, BakedModel model) {

    }

    @OnlyIn(Dist.CLIENT)
    default List<BakedQuad> renderModel(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, @Nullable BlockState state, @Nullable  Direction side, RandomSource rand) {
        return Collections.emptyList();
    }

    @OnlyIn(Dist.CLIENT)
    default void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {

    }

    @OnlyIn(Dist.CLIENT)
    default void onAdditionalModel(Consumer<ResourceLocation> registry) {

    }

    @OnlyIn(Dist.CLIENT)
    default void registerEvent() {
        synchronized (EVENT_REGISTERS) {
            EVENT_REGISTERS.add(this);
        }
    }

    default boolean isRaw() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default boolean hasTESR(BlockEntity blockEntity) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default boolean isGlobalRenderer(BlockEntity blockEntity) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default int getViewDistance() {
        return 64;
    }

    @OnlyIn(Dist.CLIENT)
    default boolean shouldRender(BlockEntity blockEntity, Vec3 cameraPos) {
        return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(cameraPos, this.getViewDistance());
    }

    @OnlyIn(Dist.CLIENT)
    default void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

    }

    @OnlyIn(Dist.CLIENT)
    @Nonnull
    default TextureAtlasSprite getParticleTexture() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation());
    }

    @OnlyIn(Dist.CLIENT)
    default boolean useAO() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default boolean useAO(BlockState state) {
        return useAO();
    }

    @OnlyIn(Dist.CLIENT)
    default boolean useBlockLight(ItemStack stack) {
        return false;
    }

    /**
     * Should we rebake quads for mcmeta data?
     */
    @OnlyIn(Dist.CLIENT)
    default boolean reBakeCustomQuads() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default float reBakeCustomQuadsOffset() {
        return 0.002f;
    }

    @OnlyIn(Dist.CLIENT)
    default boolean isGui3d() {
        return true;
    }
}
