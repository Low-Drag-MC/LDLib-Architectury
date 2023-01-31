package com.lowdragmc.creategreg;

import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileEntity;
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileInstance;
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticTileRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.lowdragmc.creategreg.CreateGreg.REGISTRATE;

/**
 * @author KilaBash
 * @date 2023/1/30
 * @implNote AllTileEntities
 */
public class AllBlockEntities {

    // Kinetics
    public static final BlockEntityEntry<BracketedKineticTileEntity> BRACKETED_KINETIC = REGISTRATE
            .tileEntity("simple_kinetic", BracketedKineticTileEntity::new)
            .instance(() -> BracketedKineticTileInstance::new, false)
            .validBlocks(AllBlocks.COGWHEEL)
            .renderer(() -> BracketedKineticTileRenderer::new)
            .register();

    public static void init() {
        // load the class and register everything
        CreateGreg.LOGGER.info("Registering block entities for " + CreateGreg.NAME);
    }
}
