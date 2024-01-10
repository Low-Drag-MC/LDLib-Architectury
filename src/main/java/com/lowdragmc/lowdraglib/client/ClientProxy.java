package com.lowdragmc.lowdraglib.client;

import com.lowdragmc.lowdraglib.CommonProxy;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.client.model.forge.LDLRendererModel;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.client.shader.Shaders;
import com.lowdragmc.lowdraglib.client.utils.WidgetClientTooltipComponent;
import com.lowdragmc.lowdraglib.core.mixins.ParticleEngineAccessor;
import com.lowdragmc.lowdraglib.gui.compass.CompassManager;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.util.WidgetTooltipComponent;
import com.lowdragmc.lowdraglib.test.TestBlock;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public ClientProxy(IEventBus eventBus) {
        super(eventBus);
    }


    /**
     * should be called when Minecraft is prepared.
     */
    public static void init() {
        Shaders.init();
        DrawerHelper.init();
        CompassManager.INSTANCE.init();
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
            if (Platform.isDevEnv()) {
                ItemBlockRenderTypes.setRenderLayer(TestBlock.BLOCK, RenderType.cutoutMipped());
            }
            Minecraft.getInstance().getMainRenderTarget().enableStencil();
        });
    }

    @SubscribeEvent
    public void modelRegistry(final ModelEvent.RegisterGeometryLoaders e) {
        e.register(LDLib.location("renderer"), LDLRendererModel.Loader.INSTANCE);
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

    public static ParticleProvider getProvider(ParticleType<?> type) {
        if (Minecraft.getInstance().particleEngine instanceof ParticleEngineAccessor accessor) {
            return accessor.getProviders().get(BuiltInRegistries.PARTICLE_TYPE.getKey(type));
        }
        return null;
    }

}
