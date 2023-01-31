package com.lowdragmc.creategreg;

import com.lowdragmc.creategreg.api.Tier;
import com.lowdragmc.creategreg.api.block.ITierBlock;
import com.lowdragmc.creategreg.content.contraptions.relays.elementary.TierCogWheelBlock;
import com.simibubi.create.content.contraptions.relays.elementary.BracketedKineticBlockModel;
import com.simibubi.create.content.contraptions.relays.elementary.CogwheelBlockItem;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Arrays;
import java.util.function.BiConsumer;

import static com.lowdragmc.creategreg.CreateGreg.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class AllBlocks {

	public static final BlockEntry<TierCogWheelBlock>[] COGWHEEL = createSimpleTierBlock("cogwheel", TierCogWheelBlock::small, (tier, builder) ->
			builder.initialProperties(SharedProperties::stone)
				.properties(p -> p.sound(SoundType.WOOD))
				.properties(p -> p.color(MaterialColor.DIRT))
				.transform(BlockStressDefaults.setNoImpact())
				.transform(axeOrPickaxe())
				.blockstate(BlockStateGen.axisBlockProvider(false))
				.onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
				.item(CogwheelBlockItem::new)
				.build(),
			Tier.values());

	public static <T extends Block & ITierBlock> BlockEntry<T>[] createSimpleTierBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, BiConsumer<Tier, BlockBuilder<T, CreateRegistrate>> builder, Tier... tiers) {
		var entries =  Arrays.stream(tiers).map(tier -> {
			var blockBuilder =  REGISTRATE.block(name + "_" + tier.getName(), properties -> {
				Tier.pushCurrentTier(tier);
				var block = factory.apply(properties);
				Tier.popCurrentTier();
				return block;
			});
			builder.accept(tier, blockBuilder);
			return blockBuilder.register();
		}).toArray(BlockEntry[]::new);
		return entries;
	}

	public static void init() {
		// load the class and register everything
		CreateGreg.LOGGER.info("Registering blocks for " + CreateGreg.NAME);
	}
}
