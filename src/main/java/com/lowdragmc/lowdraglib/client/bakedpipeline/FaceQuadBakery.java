package com.lowdragmc.lowdraglib.client.bakedpipeline;

import com.mojang.math.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import org.joml.*;

import javax.annotation.Nullable;
import java.lang.Math;

/**
 * @author KilaBash
 * @date 2023/2/20
 * @implNote FaceQuadBakery
 */
@OnlyIn(Dist.CLIENT)
public class FaceQuadBakery {
    public static final int VERTEX_INT_SIZE = 8;
    private static final float RESCALE_22_5 = 1.0F / (float)Math.cos((float)Math.PI / 8F) - 1.0F;
    private static final float RESCALE_45 = 1.0F / (float)Math.cos((float)Math.PI / 4F) - 1.0F;
    public static final int VERTEX_COUNT = 4;
    private static final int COLOR_INDEX = 3;
    public static final int UV_INDEX = 4;

    public BakedQuad bakeQuad(Vector3f pPosFrom, Vector3f pPosTo, BlockElementFace pFace, TextureAtlasSprite pSprite, Direction pFacing, ModelState pTransform, @Nullable BlockElementRotation pPartRotation, boolean pShade, int emissivity) {
        BlockFaceUV blockfaceuv = pFace.uv;
        if (pTransform.isUvLocked()) {
            blockfaceuv = recomputeUVs(pFace.uv, pFacing, pTransform.getRotation());
        }

        float[] afloat = new float[blockfaceuv.uvs.length];
        System.arraycopy(blockfaceuv.uvs, 0, afloat, 0, afloat.length);
        float f = pSprite.uvShrinkRatio();
        float f1 = (blockfaceuv.uvs[0] + blockfaceuv.uvs[0] + blockfaceuv.uvs[2] + blockfaceuv.uvs[2]) / 4.0F;
        float f2 = (blockfaceuv.uvs[1] + blockfaceuv.uvs[1] + blockfaceuv.uvs[3] + blockfaceuv.uvs[3]) / 4.0F;
        blockfaceuv.uvs[0] = Mth.lerp(f, blockfaceuv.uvs[0], f1);
        blockfaceuv.uvs[2] = Mth.lerp(f, blockfaceuv.uvs[2], f1);
        blockfaceuv.uvs[1] = Mth.lerp(f, blockfaceuv.uvs[1], f2);
        blockfaceuv.uvs[3] = Mth.lerp(f, blockfaceuv.uvs[3], f2);
        int[] aint = this.makeVertices(blockfaceuv, pSprite, pFacing, this.setupShape(pPosFrom, pPosTo), pTransform.getRotation(), pPartRotation, pShade);
        Direction direction = calculateFacing(aint);
        System.arraycopy(afloat, 0, blockfaceuv.uvs, 0, afloat.length);
        if (pPartRotation == null) {
            this.recalculateWinding(aint, direction);
        }

        fillNormal(aint, direction);
        BakedQuad quad = new BakedQuad(aint, pFace.tintIndex, direction, pSprite, pShade);
        QuadTransformers.settingEmissivity(emissivity).processInPlace(quad);
        return quad;
    }

    public static BlockFaceUV recomputeUVs(BlockFaceUV pUv, Direction pFacing, Transformation pModelRotation) {
        Matrix4f matrix4f = BlockMath.getUVLockTransform(pModelRotation, pFacing, () -> "Unable to resolve UVLock for model").getMatrix();
        float f = pUv.getU(pUv.getReverseIndex(0));
        float f1 = pUv.getV(pUv.getReverseIndex(0));
        Vector4f vector4f = new Vector4f(f / 16.0F, f1 / 16.0F, 0.0F, 1.0F);
        vector4f = matrix4f.transform(vector4f);
        float f2 = 16.0F * vector4f.x();
        float f3 = 16.0F * vector4f.y();
        float f4 = pUv.getU(pUv.getReverseIndex(2));
        float f5 = pUv.getV(pUv.getReverseIndex(2));
        Vector4f vector4f1 = new Vector4f(f4 / 16.0F, f5 / 16.0F, 0.0F, 1.0F);
        vector4f1 = matrix4f.transform(vector4f1);
        float f6 = 16.0F * vector4f1.x();
        float f7 = 16.0F * vector4f1.y();
        float f8;
        float f9;
        if (Math.signum(f4 - f) == Math.signum(f6 - f2)) {
            f8 = f2;
            f9 = f6;
        } else {
            f8 = f6;
            f9 = f2;
        }

        float f10;
        float f11;
        if (Math.signum(f5 - f1) == Math.signum(f7 - f3)) {
            f10 = f3;
            f11 = f7;
        } else {
            f10 = f7;
            f11 = f3;
        }

        float f12 = (float)Math.toRadians((double)pUv.rotation);
        Vector3f vector3f = new Vector3f(Mth.cos(f12), Mth.sin(f12), 0.0F);
        Matrix3f matrix3f = new Matrix3f(matrix4f);
        vector3f = matrix3f.transform(vector3f);
        int i = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2((double)vector3f.y(), (double)vector3f.x())) / 90.0D)) * 90, 360);
        return new BlockFaceUV(new float[]{f8, f10, f9, f11}, i);
    }

    private int[] makeVertices(BlockFaceUV pUvs, TextureAtlasSprite pSprite, Direction pOrientation, float[] pPosDiv16, Transformation pRotation, @Nullable BlockElementRotation pPartRotation, boolean pShade) {
        int[] aint = new int[32];

        for(int i = 0; i < 4; ++i) {
            this.bakeVertex(aint, i, pOrientation, pUvs, pPosDiv16, pSprite, pRotation, pPartRotation, pShade);
        }

        return aint;
    }

    private float[] setupShape(Vector3f pPos1, Vector3f pPos2) {
        float[] afloat = new float[Direction.values().length];
        afloat[FaceInfo.Constants.MIN_X] = pPos1.x() / 16.0F;
        afloat[FaceInfo.Constants.MIN_Y] = pPos1.y() / 16.0F;
        afloat[FaceInfo.Constants.MIN_Z] = pPos1.z() / 16.0F;
        afloat[FaceInfo.Constants.MAX_X] = pPos2.x() / 16.0F;
        afloat[FaceInfo.Constants.MAX_Y] = pPos2.y() / 16.0F;
        afloat[FaceInfo.Constants.MAX_Z] = pPos2.z() / 16.0F;
        return afloat;
    }

    private void bakeVertex(int[] pVertexData, int pVertexIndex, Direction pFacing, BlockFaceUV pBlockFaceUV, float[] pPosDiv16, TextureAtlasSprite pSprite, Transformation pRotation, @Nullable BlockElementRotation pPartRotation, boolean pShade) {
        FaceInfo.VertexInfo faceinfo$vertexinfo = FaceInfo.fromFacing(pFacing).getVertexInfo(pVertexIndex);
        Vector3f vector3f = new Vector3f(pPosDiv16[faceinfo$vertexinfo.xFace], pPosDiv16[faceinfo$vertexinfo.yFace], pPosDiv16[faceinfo$vertexinfo.zFace]);
        this.applyElementRotation(vector3f, pPartRotation);
        this.applyModelRotation(vector3f, pRotation);
        this.fillVertex(pVertexData, pVertexIndex, vector3f, pSprite, pBlockFaceUV);
    }

    private void fillVertex(int[] pVertexData, int pVertexIndex, Vector3f pVector, TextureAtlasSprite pSprite, BlockFaceUV pBlockFaceUV) {
        int i = pVertexIndex * 8;
        pVertexData[i] = Float.floatToRawIntBits(pVector.x());
        pVertexData[i + 1] = Float.floatToRawIntBits(pVector.y());
        pVertexData[i + 2] = Float.floatToRawIntBits(pVector.z());
        pVertexData[i + 3] = -1;
        pVertexData[i + 4] = Float.floatToRawIntBits(pSprite.getU(pBlockFaceUV.getU(pVertexIndex) * .999F + pBlockFaceUV.getU((pVertexIndex + 2) % 4) * .001F));
        pVertexData[i + 4 + 1] = Float.floatToRawIntBits(pSprite.getV(pBlockFaceUV.getV(pVertexIndex) * .999F + pBlockFaceUV.getV((pVertexIndex + 2) % 4) * .001F));
    }

    private void applyElementRotation(Vector3f pVec, @Nullable BlockElementRotation pPartRotation) {
        if (pPartRotation != null) {
            Vector3f vector3f;
            Vector3f vector3f1;
            switch (pPartRotation.axis()) {
                case X -> {
                    vector3f = new Vector3f(1, 0 ,0);
                    vector3f1 = new Vector3f(0.0F, 1.0F, 1.0F);
                }
                case Y -> {
                    vector3f = new Vector3f(0, 1, 0);
                    vector3f1 = new Vector3f(1.0F, 0.0F, 1.0F);
                }
                case Z -> {
                    vector3f = new Vector3f(0, 0, 1);
                    vector3f1 = new Vector3f(1.0F, 1.0F, 0.0F);
                }
                default -> throw new IllegalArgumentException("There are only 3 axes");
            }

            var quaternion = new Quaternionf().rotateAxis((float) Math.toRadians(pPartRotation.angle()), vector3f);
            if (pPartRotation.rescale()) {
                if (Math.abs(pPartRotation.angle()) == 22.5F) {
                    vector3f1.mul(RESCALE_22_5);
                } else {
                    vector3f1.mul(RESCALE_45);
                }

                vector3f1.add(1.0F, 1.0F, 1.0F);
            } else {
                vector3f1.set(1.0F, 1.0F, 1.0F);
            }

            this.rotateVertexBy(pVec, new Vector3f(pPartRotation.origin()), new Matrix4f().rotate(quaternion), vector3f1);
        }
    }

    public void applyModelRotation(Vector3f pPos, Transformation pTransform) {
        if (pTransform != Transformation.identity()) {
            this.rotateVertexBy(pPos, new Vector3f(0.5F, 0.5F, 0.5F), pTransform.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
        }
    }

    private void rotateVertexBy(Vector3f pPos, Vector3f pOrigin, Matrix4f pTransform, Vector3f pScale) {
        var vector4f = new Vector4f(pPos.x() - pOrigin.x(), pPos.y() - pOrigin.y(), pPos.z() - pOrigin.z(), 1.0F);
        vector4f = pTransform.transform(vector4f);
        vector4f.mul(pScale.x, pScale.y, pScale.z, 1);
        pPos.set(vector4f.x() + pOrigin.x(), vector4f.y() + pOrigin.y(), vector4f.z() + pOrigin.z());
    }

    public static Direction calculateFacing(int[] pFaceData) {
        Vector3f vector3f = new Vector3f(Float.intBitsToFloat(pFaceData[0]), Float.intBitsToFloat(pFaceData[1]), Float.intBitsToFloat(pFaceData[2]));
        Vector3f vector3f1 = new Vector3f(Float.intBitsToFloat(pFaceData[8]), Float.intBitsToFloat(pFaceData[9]), Float.intBitsToFloat(pFaceData[10]));
        Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(pFaceData[16]), Float.intBitsToFloat(pFaceData[17]), Float.intBitsToFloat(pFaceData[18]));
        Vector3f vector3f3 = new Vector3f(vector3f);
        vector3f3.sub(vector3f1);
        Vector3f vector3f4 = new Vector3f(vector3f2);
        vector3f4.sub(vector3f1);
        Vector3f vector3f5 = new Vector3f(vector3f4);
        vector3f5.cross(vector3f3);
        vector3f5.normalize();
        Direction direction = null;
        float f = 0.0F;

        for(Direction direction1 : Direction.values()) {
            Vec3i vec3i = direction1.getNormal();
            Vector3f vector3f6 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            float f1 = vector3f5.dot(vector3f6);
            if (f1 >= 0.0F && f1 > f) {
                f = f1;
                direction = direction1;
            }
        }

        return direction == null ? Direction.UP : direction;
    }

    private void recalculateWinding(int[] pVertices, Direction pDirection) {
        int[] aint = new int[pVertices.length];
        System.arraycopy(pVertices, 0, aint, 0, pVertices.length);
        float[] afloat = new float[Direction.values().length];
        afloat[FaceInfo.Constants.MIN_X] = 999.0F;
        afloat[FaceInfo.Constants.MIN_Y] = 999.0F;
        afloat[FaceInfo.Constants.MIN_Z] = 999.0F;
        afloat[FaceInfo.Constants.MAX_X] = -999.0F;
        afloat[FaceInfo.Constants.MAX_Y] = -999.0F;
        afloat[FaceInfo.Constants.MAX_Z] = -999.0F;

        for(int i = 0; i < 4; ++i) {
            int j = 8 * i;
            float f = Float.intBitsToFloat(aint[j]);
            float f1 = Float.intBitsToFloat(aint[j + 1]);
            float f2 = Float.intBitsToFloat(aint[j + 2]);
            if (f < afloat[FaceInfo.Constants.MIN_X]) {
                afloat[FaceInfo.Constants.MIN_X] = f;
            }

            if (f1 < afloat[FaceInfo.Constants.MIN_Y]) {
                afloat[FaceInfo.Constants.MIN_Y] = f1;
            }

            if (f2 < afloat[FaceInfo.Constants.MIN_Z]) {
                afloat[FaceInfo.Constants.MIN_Z] = f2;
            }

            if (f > afloat[FaceInfo.Constants.MAX_X]) {
                afloat[FaceInfo.Constants.MAX_X] = f;
            }

            if (f1 > afloat[FaceInfo.Constants.MAX_Y]) {
                afloat[FaceInfo.Constants.MAX_Y] = f1;
            }

            if (f2 > afloat[FaceInfo.Constants.MAX_Z]) {
                afloat[FaceInfo.Constants.MAX_Z] = f2;
            }
        }

        FaceInfo faceinfo = FaceInfo.fromFacing(pDirection);

        for(int i1 = 0; i1 < 4; ++i1) {
            int j1 = 8 * i1;
            FaceInfo.VertexInfo faceinfo$vertexinfo = faceinfo.getVertexInfo(i1);
            float f8 = afloat[faceinfo$vertexinfo.xFace];
            float f3 = afloat[faceinfo$vertexinfo.yFace];
            float f4 = afloat[faceinfo$vertexinfo.zFace];
            pVertices[j1] = Float.floatToRawIntBits(f8);
            pVertices[j1 + 1] = Float.floatToRawIntBits(f3);
            pVertices[j1 + 2] = Float.floatToRawIntBits(f4);

            for(int k = 0; k < 4; ++k) {
                int l = 8 * k;
                float f5 = Float.intBitsToFloat(aint[l]);
                float f6 = Float.intBitsToFloat(aint[l + 1]);
                float f7 = Float.intBitsToFloat(aint[l + 2]);
                if (Mth.equal(f8, f5) && Mth.equal(f3, f6) && Mth.equal(f4, f7)) {
                    pVertices[j1 + 4] = aint[l + 4];
                    pVertices[j1 + 4 + 1] = aint[l + 4 + 1];
                }
            }
        }

    }

    public static void fillNormal(int[] faceData, Direction facing) {
        Vector3f v1 = getVertexPos(faceData, 3);
        Vector3f t1 = getVertexPos(faceData, 1);
        Vector3f v2 = getVertexPos(faceData, 2);
        Vector3f t2 = getVertexPos(faceData, 0);
        v1.sub(t1);
        v2.sub(t2);
        v2.cross(v1);
        v2.normalize();

        int x = ((byte) Math.round(v2.x() * 127)) & 0xFF;
        int y = ((byte) Math.round(v2.y() * 127)) & 0xFF;
        int z = ((byte) Math.round(v2.z() * 127)) & 0xFF;

        int normal = x | (y << 0x08) | (z << 0x10);

        for(int i = 0; i < 4; i++)
        {
            faceData[i * 8 + 7] = normal;
        }
    }

    private static Vector3f getVertexPos(int[] data, int vertex) {
        int idx = vertex * 8;

        float x = Float.intBitsToFloat(data[idx]);
        float y = Float.intBitsToFloat(data[idx + 1]);
        float z = Float.intBitsToFloat(data[idx + 2]);

        return new Vector3f(x, y, z);
    }
}
