package com.lowdragmc.lowdraglib.client.fabric;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.Platform;
import com.lowdragmc.lowdraglib.client.ClientCommands;
import com.lowdragmc.lowdraglib.client.ClientProxy;
import com.lowdragmc.lowdraglib.client.model.fabric.LDLRendererModel;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.rei.ModularDisplay;
import com.lowdragmc.lowdraglib.test.TestBlock;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.impl.client.screen.ScreenEventFactory;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.List;

/**
 * @author KilaBash
 * @date 2023/2/8
 * @implNote ClientProxyImpl
 */
@Environment(EnvType.CLIENT)
public class ClientProxyImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Minecraft start
        ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> ClientProxy.init());

        // register custom model loader
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> LDLRendererModel.Loader.INSTANCE);

        // register client commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            List<LiteralArgumentBuilder<FabricClientCommandSource>> commands = ClientCommands.createClientCommands();
            commands.forEach(dispatcher::register);
        });

        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS).register((atlas, registry) -> IRenderer.EVENT_REGISTERS.forEach(renderer -> renderer.onPrepareTextureAtlas(InventoryMenu.BLOCK_ATLAS, registry::register)));

        if (LDLib.isReiLoaded()) {
            ScreenEventFactory.createRemoveEvent().register(screen -> {
                if (screen instanceof DisplayScreen && !ModularDisplay.CACHE_OPENED.isEmpty()) {
                    synchronized (ModularDisplay.CACHE_OPENED) {
                        ModularDisplay.CACHE_OPENED.forEach(modular -> modular.modularUI.triggerCloseListeners());
                        ModularDisplay.CACHE_OPENED.clear();
                    }
                }
            });
        }

        // render types
        if (Platform.isDevEnv()) {
            BlockRenderLayerMap.INSTANCE.putBlock(TestBlock.BLOCK, RenderType.cutoutMipped());
        }
    }

}
