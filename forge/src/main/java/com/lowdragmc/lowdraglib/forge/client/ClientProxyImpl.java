package com.lowdragmc.lowdraglib.forge.client;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.ClientProxy;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.forge.CommonProxyImpl;
import com.lowdragmc.lowdraglib.forge.client.model.LDLRendererModel;
import com.lowdragmc.lowdraglib.forge.jei.JEIClientEventHandler;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.test.TestBlock;
import com.lowdragmc.lowdraglib.utils.CustomResourcePack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Consumer;


@OnlyIn(Dist.CLIENT)
public class ClientProxyImpl extends CommonProxyImpl {

    public ClientProxyImpl() {
        super();
        // init
        ClientProxy.init();
        if (LDLib.isJeiLoaded()) {
            MinecraftForge.EVENT_BUS.register(JEIClientEventHandler.class);
        }
        if (Minecraft.getInstance() != null) {
            Minecraft.getInstance().getResourcePackRepository().addPackFinder(new CustomResourcePack(LDLib.location, PackSource.DEFAULT, LDLib.MOD_ID, "LDLib Extended Resources", 6));
        }
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            Shaders.init();
            DrawerHelper.init();
            if (DEBUG) {
                ItemBlockRenderTypes.setRenderLayer(TestBlock.BLOCK, x -> true);
            }
        });
    }

    @SubscribeEvent
    public void modelRegistry(final ModelEvent.RegisterGeometryLoaders e) {
        e.register("ldlib:renderer", LDLRendererModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public void shaderRegistry(RegisterShadersEvent event) {
        for (Pair<ShaderInstance, Consumer<ShaderInstance>> pair : Shaders.registerShaders(event.getResourceManager())) {
            event.registerShader(pair.getFirst(), pair.getSecond());
        }
    }

    @SubscribeEvent
    public void registerTextures(TextureStitchEvent.Pre event) {
        for (IRenderer renderer : IRenderer.TEXTURE_REGISTERS) {
            renderer.onPrepareTextureAtlas(event.getAtlas().location(), event::addSprite);
        }
    }
}
