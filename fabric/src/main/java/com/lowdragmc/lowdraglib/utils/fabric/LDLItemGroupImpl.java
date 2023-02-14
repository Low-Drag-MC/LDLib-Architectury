package com.lowdragmc.lowdraglib.utils.fabric;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

/**
 * @author KilaBash
 * @date 2023/2/15
 * @implNote LDLItemGroupImpl
 */
public class LDLItemGroupImpl {
    public static int expandArrayAndGetId() {
        ResourceLocation error = new ResourceLocation("if_you_see_this", "something_went_wrong");
        return FabricItemGroupBuilder.build(error, Items.AIR::getDefaultInstance).getId();
    }
}
