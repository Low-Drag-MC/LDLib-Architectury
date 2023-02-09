package com.lowdragmc.lowdraglib.forge;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.async.AsyncThreadData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
