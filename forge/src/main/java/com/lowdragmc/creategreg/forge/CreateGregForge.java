package com.lowdragmc.creategreg.forge;

import com.lowdragmc.creategreg.CreateGreg;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateGreg.MOD_ID)
public class CreateGregForge {
    public CreateGregForge() {
        // registrate must be given the mod event bus on forge before registration
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CreateGreg.REGISTRATE.registerEventListeners(eventBus);
        CreateGreg.init();
    }
}
