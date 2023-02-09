package com.lowdragmc.lowdraglib.forge;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.forge.client.ClientProxyImpl;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(LDLib.MOD_ID)
public class LDLibForge {
    public LDLibForge() {
        LDLib.init();
        // registrate must be given the mod event bus on forge before registration
//        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
//        LDLib.REGISTRATE.registerEventListeners(eventBus);
        DistExecutor.unsafeRunForDist(() -> ClientProxyImpl::new, () -> CommonProxyImpl::new);
    }
}
