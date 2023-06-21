package com.lowdragmc.lowdraglib.fabric;

import com.lowdragmc.lowdraglib.*;
import com.lowdragmc.lowdraglib.plugin.ILDLibPlugin;
import com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries;
import com.lowdragmc.lowdraglib.test.TestBlock;
import com.lowdragmc.lowdraglib.test.TestBlockEntity;
import com.lowdragmc.lowdraglib.test.TestItem;
import com.lowdragmc.lowdraglib.test.fabric.TestBlockEntityImpl;
import com.lowdragmc.lowdraglib.utils.fabric.ReflectionUtilsImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;

public class LDLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LDLib.init();
        if (Platform.isDevEnv()) {
            Registry.register(Registry.BLOCK, LDLib.location("test"), TestBlock.BLOCK);
            Registry.register(Registry.ITEM, LDLib.location("test"), TestItem.ITEM);
            TestBlockEntityImpl.TYPE = Registry.register(
                    Registry.BLOCK_ENTITY_TYPE,
                    LDLib.location("test"),
                    FabricBlockEntityTypeBuilder.create(TestBlockEntity::new, TestBlock.BLOCK).build()
            );
        }
        // hook server
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PlatformImpl.SERVER = server;
        });
        // register server commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ServerCommands.createServerCommands().forEach(dispatcher::register));
        // init common features
        CommonProxy.init();
        // load entry points
        for (ILDLibPlugin ldlibPugin : FabricLoader.getInstance().getEntrypoints("ldlib_pugin", ILDLibPlugin.class)) {
            ldlibPugin.onLoad();
        }
        // execute annotation searching
        ReflectionUtilsImpl.execute();
        // register payload
        TypedPayloadRegistries.postInit();
    }

}
