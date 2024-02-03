package com.lowdragmc.lowdraglib.client.forge;

import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.client.ClientProxy;
import com.lowdragmc.lowdraglib.client.model.forge.LDLRendererModel;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.renderer.block.RendererBlock;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.client.utils.WidgetClientTooltipComponent;
import com.lowdragmc.lowdraglib.forge.CommonProxyImpl;
import com.lowdragmc.lowdraglib.gui.compass.CompassManager;
import com.lowdragmc.lowdraglib.gui.util.WidgetTooltipComponent;
import com.lowdragmc.lowdraglib.test.TestBlock;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Consumer;


@OnlyIn(Dist.CLIENT)
public class ClientProxyImpl extends CommonProxyImpl {

    public ClientProxyImpl() {
        super();
    }

    @SubscribeEvent
    public void onRegisterClientTooltipComponentFactoriesEvent(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(WidgetTooltipComponent.class, WidgetClientTooltipComponent::new);
    }

    @SubscribeEvent
    public void clientSetup(final FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(CompassManager.INSTANCE);
            CompassManager.INSTANCE.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
            ClientProxy.init();
            ItemBlockRenderTypes.setRenderLayer(RendererBlock.BLOCK, RenderType.translucent());
            if (Platform.isDevEnv()) {
                ItemBlockRenderTypes.setRenderLayer(TestBlock.BLOCK, RenderType.cutoutMipped());
            }
            Minecraft.getInstance().getMainRenderTarget().enableStencil();
        });
    }

    @SubscribeEvent
    public void modelRegistry(final ModelEvent.RegisterGeometryLoaders e) {
        e.register("renderer", LDLRendererModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public void shaderRegistry(RegisterShadersEvent event) {
        for (Pair<ShaderInstance, Consumer<ShaderInstance>> pair : Shaders.registerShaders(event.getResourceProvider())) {
            event.registerShader(pair.getFirst(), pair.getSecond());
        }
    }

//    @SubscribeEvent
//    public void registerTextures(RegisterTextureAtlasSpriteLoadersEvent event) {
//        for (IRenderer renderer : IRenderer.EVENT_REGISTERS) {
//            renderer.onPrepareTextureAtlas(event.getAtlas().location(), location -> event.getAtlas().);
//        }
//    }

    @SubscribeEvent
    public void registerTextures(ModelEvent.RegisterAdditional event) {
        for (IRenderer renderer : IRenderer.EVENT_REGISTERS) {
            renderer.onAdditionalModel(event::register);
        }
    }
}
