package com.lowdragmc.lowdraglib.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class LDLItemGroup extends CreativeModeTab {

    protected String domain, id;
    protected Supplier<ItemStack> iconSupplier;

    public LDLItemGroup(String domain, String id, Supplier<ItemStack> iconSupplier) {
        super(expandArrayAndGetId(), domain + "." + id);
        this.domain = domain;
        this.id = id;
        this.iconSupplier = iconSupplier;
    }

    @ExpectPlatform
    public static int expandArrayAndGetId() {
        throw new AssertionError();
    }

    public String getDomain() {
        return domain;
    }

    public String getGroupId() {
        return id;
    }

    @Nonnull
    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack makeIcon() {
        return iconSupplier.get();
    }
}
