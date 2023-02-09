package com.lowdragmc.lowdraglib.fabric;

import com.lowdragmc.lowdraglib.CommonProxy;
import com.lowdragmc.lowdraglib.ServerCommands;
import com.lowdragmc.lowdraglib.gui.factory.UIEditorFactory;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import com.lowdragmc.lowdraglib.LDLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class LDLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LDLib.init();
        CommonProxy.init();
        LDLib.LOGGER.info(EnvExecutor.unsafeRunForDist(
                () -> () -> "{} is accessing Porting Lib on a Fabric client!",
                () -> () -> "{} is accessing Porting Lib on a Fabric server!"
                ), LDLib.NAME);
        // on fabric, Registrates must be explicitly finalized and registered.
//        LDLib.REGISTRATE.register();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> PlatformImpl.SERVER = server);
        // register server commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ServerCommands.createServerCommands().forEach(dispatcher::register));
    }

}
