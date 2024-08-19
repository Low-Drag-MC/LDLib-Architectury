package com.lowdragmc.lowdraglib.client.model.fabric;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.ClientProxy;
import com.lowdragmc.lowdraglib.client.model.custommodel.LDLMetadataSection;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class LDLibModelLoadingPlugin implements ModelLoadingPlugin {

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.resolveModel().register(resolverContext -> resolverContext.id().equals(LDLib.location("block/renderer_model")) ? LDLRendererModel.INSTANCE : null);
        pluginContext.modifyModelAfterBake().register((baked, context) -> {
            if (baked == null) {
                return null;
            }
            ResourceLocation rl = context.id();
            UnbakedModel rootModel = context.loader().getModel(rl);
            if (rootModel != context.loader().getModel(ModelBakery.MISSING_MODEL_LOCATION)) {
                if (baked instanceof LDLRendererModel) {
                    return baked;
                }
                if (baked.isCustomRenderer()) { // Nothing we can add to builtin models
                    return baked;
                }
                Deque<ResourceLocation> dependencies = new ArrayDeque<>();
                Set<ResourceLocation> seenModels = new HashSet<>();
                dependencies.push(rl);
                seenModels.add(rl);
                boolean shouldWrap = ClientProxy.WRAPPED_MODELS.getOrDefault(rl, false);
                // Breadth-first loop through dependencies, exiting as soon as a CTM texture is found, and skipping duplicates/cycles
                while (!shouldWrap && !dependencies.isEmpty()) {
                    ResourceLocation dep = dependencies.pop();
                    UnbakedModel model;
                    try {
                        model = dep == rl ? rootModel : context.loader().getModel(dep);
                    } catch (Exception e) {
                        continue;
                    }

                    try {
                        Set<Material> textures = new HashSet<>(ClientProxy.SCRAPED_TEXTURES.get(dep));
                        for (Material tex : textures) {
                            // Cache all dependent texture metadata
                            // At least one texture has CTM metadata, so we should wrap this baked
                            if (!LDLMetadataSection.getMetadata(LDLMetadataSection.spriteToAbsolute(tex.texture())).isMissing()) { // TODO lazy
                                shouldWrap = true;
                                break;
                            }
                        }
                        if (!shouldWrap) {
                            for (ResourceLocation newDep : model.getDependencies()) {
                                if (seenModels.add(newDep)) {
                                    dependencies.push(newDep);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LDLib.LOGGER.error("Error loading baked dependency {} for baked {}. Skipping...", dep, rl, e);
                    }
                }
                ClientProxy.WRAPPED_MODELS.put(rl, shouldWrap);
                if (shouldWrap) {
                    return new CustomBakedModelImpl(baked);
                }
            }
            return baked;
        });
    }

}
