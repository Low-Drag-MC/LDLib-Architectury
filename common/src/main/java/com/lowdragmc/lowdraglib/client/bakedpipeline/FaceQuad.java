package com.lowdragmc.lowdraglib.client.bakedpipeline;

import com.mojang.math.Vector3f;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

/**
 * @author KilaBash
 * @date 2023/2/20
 * @implNote FaceQuadBuilder
 */
@Environment(EnvType.CLIENT)
public class FaceQuad {
    public static final AABB BLOCK = new AABB(0, 0, 0, 1, 1, 1);

    public static BakedQuad bakeFace(AABB cube, Direction face, TextureAtlasSprite sprite, ModelState rotation, int tintIndex, int emissivity, boolean cull, boolean shade) {
        return new FaceQuadBakery().bakeQuad(
                new Vector3f((float) cube.minX * 16f, (float) cube.minY * 16f, (float) cube.minZ * 16f),
                new Vector3f((float) cube.maxX * 16f, (float) cube.maxY * 16f, (float) cube.maxZ * 16f),
                new BlockElementFace(cull ? face : null, tintIndex, "", new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)),
                sprite,
                face,
                rotation,
                null,
                shade,
                emissivity
        );
    }

    public static BakedQuad bakeFace(Direction face, TextureAtlasSprite sprite, ModelState rotation, int tintIndex, int emissivity, boolean cull, boolean shade) {
        return bakeFace(BLOCK, face, sprite, rotation, tintIndex, emissivity, cull, shade);
    }

    public static BakedQuad bakeFace(Direction face, TextureAtlasSprite sprite, ModelState rotation, int tintIndex, int emissivity) {
        return bakeFace(face, sprite, rotation, tintIndex, emissivity, true, true);
    }

    public static BakedQuad bakeFace(Direction face, TextureAtlasSprite sprite, ModelState rotation, int tintIndex) {
        return bakeFace(face, sprite, rotation, tintIndex, 0);
    }

    public static BakedQuad bakeFace(Direction face, TextureAtlasSprite sprite, ModelState rotation) {
        return bakeFace(face, sprite, rotation, 0);
    }

    public static BakedQuad bakeFace(Direction face, TextureAtlasSprite sprite) {
        return bakeFace(face, sprite, BlockModelRotation.X0_Y0);
    }

    public static Builder builder(Direction face, TextureAtlasSprite sprite) {
        return new Builder(face, sprite);
    }

    @Accessors(chain = true, fluent = true)
    public static class Builder {
        @Setter
        Vector3f from = Vector3f.ZERO;
        @Setter
        Vector3f to = new Vector3f(16, 16, 16);
        @Setter
        Direction face;
        @Setter
        TextureAtlasSprite sprite;
        @Setter
        ModelState rotation = BlockModelRotation.X0_Y0;
        @Setter
        int tintIndex = 0, emissivity = 0;
        @Setter
        boolean cull = true, shade = true;

        protected Builder(Direction face, TextureAtlasSprite sprite) {
            this.face = face;
            this.sprite = sprite;
        }

        public Builder cube(AABB cube) {
            from = new Vector3f((float) cube.minX * 16f, (float) cube.minY * 16f, (float) cube.minZ * 16f);
            to = new Vector3f((float) cube.maxX * 16f, (float) cube.maxY * 16f, (float) cube.maxZ * 16f);
            return this;
        }

        public BakedQuad bake() {
            return new FaceQuadBakery().bakeQuad(
                    from,
                    to,
                    new BlockElementFace(cull ? face : null, tintIndex, "", new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)),
                    sprite,
                    face,
                    rotation,
                    null,
                    shade,
                    emissivity
            );
        }
    }

}
