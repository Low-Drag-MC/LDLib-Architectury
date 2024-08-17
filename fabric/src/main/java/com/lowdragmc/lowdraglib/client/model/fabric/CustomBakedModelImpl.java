package com.lowdragmc.lowdraglib.client.model.fabric;

import com.lowdragmc.lowdraglib.client.model.custommodel.Connection;
import com.lowdragmc.lowdraglib.client.model.custommodel.CustomBakedModel;
import com.lowdragmc.lowdraglib.client.model.custommodel.ICTMPredicate;
import lombok.Getter;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CustomBakedModelImpl extends CustomBakedModel {

    private final ThreadLocal<CTMQuadTransform> quadTransform = ThreadLocal.withInitial(CTMQuadTransform::new);
    private static final RenderMaterial MATERIAL_SHADED = RendererAccess.INSTANCE.getRenderer()
            .materialFinder()
            .find();
    private static final RenderMaterial MATERIAL_FLAT = RendererAccess.INSTANCE.getRenderer()
            .materialFinder()
            .ambientOcclusion(TriState.FALSE)
            .find();

    public CustomBakedModelImpl(BakedModel parent) {
        super(parent);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter world, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        context.pushTransform(quadTransform.get());
        super.emitBlockQuads(world, state, pos, randomSupplier, context);
        context.popTransform();

        QuadEmitter emitter = context.getEmitter();

        List<BakedQuad> quads = quadTransform.get().getQuads();
        if (world != null && pos != null && state != null) {
            for (Direction side : Direction.values()) {
                boolean isConnected = true;
                for (var connection : Connection.values()) {
                    var offset = connection.transform(pos, side);
                    var adjacent = world.getBlockState(offset);
                    if (!ICTMPredicate.getPredicate(state).isConnected(world, state, pos, adjacent, offset, side)) {
                        isConnected = false;
                        break;
                    }
                }
                if (isConnected) {
                    List<BakedQuad> newQuads = CustomBakedModel.reBakeCustomQuads(quads, world, pos, state, side, 0.0F);
                    for (BakedQuad quad : newQuads) {
                        emitter.fromVanilla(quad, this.useAmbientOcclusion() ? MATERIAL_SHADED : MATERIAL_FLAT, side)
                                .emit();
                    }
                }
            }
        } else {
            for (BakedQuad quad : quads) {
                emitter.fromVanilla(quad, this.useAmbientOcclusion() ? MATERIAL_SHADED : MATERIAL_FLAT, null)
                        .emit();
            }
        }
        quadTransform.get().reset();
    }

    protected static class CTMQuadTransform implements RenderContext.QuadTransform {

        @Getter
        protected List<BakedQuad> quads = new ArrayList<>();

        @Override
        public boolean transform(MutableQuadView quad) {
            SpriteFinder finder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS));
            quads.add(quad.toBakedQuad(finder.find(quad)));
            return true;
        }

        public void reset() {
            quads.clear();
        }
    }
}
