package com.lowdragmc.lowdraglib.ItemGroup;

import io.github.fabricators_of_create.porting_lib.util.ItemGroupUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class LDItemGroup extends CreativeModeTab {

    protected String domain, id;
    protected Supplier<ItemStack> iconSupplier;

    public LDItemGroup(String domain, String id, Supplier<ItemStack> iconSupplier) {
        super(ItemGroupUtil.expandArrayAndGetId(), domain + "." + id);
        this.domain = domain;
        this.id = id;
        this.iconSupplier = iconSupplier;
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
