package com.lowdragmc.creategreg.content.contraptions.relays.elementary;

import com.lowdragmc.creategreg.AllBlockEntities;
import com.lowdragmc.creategreg.api.Tier;
import com.lowdragmc.creategreg.api.block.ITierBlock;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.relays.elementary.CogWheelBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/1/30
 * @implNote TierCogWheelBlock
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TierCogWheelBlock extends CogWheelBlock implements ITierBlock {

    public final Tier tier = Tier.peekCurrentTier();

    protected TierCogWheelBlock(boolean large, Properties properties) {
        super(large, properties);
    }

    public static TierCogWheelBlock small(Properties properties) {
        return new TierCogWheelBlock(false, properties);
    }

    public static TierCogWheelBlock large(Properties properties) {
        return new TierCogWheelBlock(true, properties);
    }

    @Override
    public BlockEntityType<? extends KineticTileEntity> getTileEntityType() {
        return AllBlockEntities.BRACKETED_KINETIC.get();
    }

    @Override
    public Tier getTier() {
        return tier;
    }
}
