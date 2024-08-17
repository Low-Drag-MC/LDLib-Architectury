package com.lowdragmc.lowdraglib.client.model.forge;

import com.lowdragmc.lowdraglib.client.model.custommodel.CustomBakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.lowdragmc.lowdraglib.client.model.forge.LDLRendererModel.RendererBakedModel.*;

public class CustomBakedModelImpl extends CustomBakedModel {

    public CustomBakedModelImpl(BakedModel parent) {
        super(parent);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
        BlockAndTintGetter world = data.get(WORLD);
        BlockPos pos = data.get(POS);
        List<BakedQuad> quads = super.getQuads(state, side, rand, data, renderType);
        if (world != null && pos != null && state != null) {
            quads = CustomBakedModel.reBakeCustomQuads(quads, world, pos, state, side, 0.0F);
        }
        return quads;
    }


    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
        return ModelData.builder()
                .with(WORLD, level)
                .with(POS, pos)
                .with(MODEL_DATA, modelData)
                .build();
    }
}
