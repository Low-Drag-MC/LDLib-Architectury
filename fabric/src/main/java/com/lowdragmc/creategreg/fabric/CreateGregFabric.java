package com.lowdragmc.creategreg.fabric;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import com.lowdragmc.creategreg.AllBlocks;
import com.lowdragmc.creategreg.CreateGreg;
import net.fabricmc.api.ModInitializer;

public class CreateGregFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreateGreg.init();
        CreateGreg.LOGGER.info(EnvExecutor.unsafeRunForDist(
                () -> () -> "{} is accessing Porting Lib on a Fabric client!",
                () -> () -> "{} is accessing Porting Lib on a Fabric server!"
                ), CreateGreg.NAME);
        // on fabric, Registrates must be explicitly finalized and registered.
        CreateGreg.REGISTRATE.register();
    }
}
