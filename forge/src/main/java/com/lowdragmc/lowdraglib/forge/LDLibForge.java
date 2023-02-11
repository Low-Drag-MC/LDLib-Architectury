package com.lowdragmc.lowdraglib.forge;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.forge.ClientProxyImpl;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(LDLib.MOD_ID)
public class LDLibForge {
    public LDLibForge() {
        LDLib.init();
        DistExecutor.unsafeRunForDist(() -> ClientProxyImpl::new, () -> CommonProxyImpl::new);
    }
}
