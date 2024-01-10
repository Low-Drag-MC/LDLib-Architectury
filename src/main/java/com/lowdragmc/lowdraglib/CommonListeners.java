package com.lowdragmc.lowdraglib;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.async.AsyncThreadData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * @author KilaBash
 * @date 2022/11/27
 * @implNote CommonListeners
 */
@Mod.EventBusSubscriber(modid = LDLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonListeners {

    @SubscribeEvent
    public static void onWorldUnLoad(LevelEvent.Unload event) {
        LevelAccessor world = event.getLevel();
        if (!world.isClientSide() && world instanceof ServerLevel serverLevel) {
            AsyncThreadData.getOrCreate(serverLevel).releaseExecutorService();
        }
    }

}
