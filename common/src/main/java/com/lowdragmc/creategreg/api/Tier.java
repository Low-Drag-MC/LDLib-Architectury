package com.lowdragmc.creategreg.api;

import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.StringRepresentable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author KilaBash
 * @date 2023/1/30
 * @implNote Tier
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum Tier implements StringRepresentable {
    ULS(0, "uls"),
    LS(1, "ls"),
    MS(2, "ms"),
    HS(3, "hs"),
    ES(4, "es"),
    IS(5, "is"),
    US(6, "us"),
    LUS(7, "lus");

    private final static ThreadLocal<Tier> CURRENT_TIER = new ThreadLocal<>();
    @Getter
    public final int tier;
    @Getter
    public final int stress;
    @Getter
    public final String name;

    Tier (int tier, String name) {
        this.tier = tier;
        this.name = name;
        this.stress = 0x1 << this.tier;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static Tier popCurrentTier() {
        var tier = CURRENT_TIER.get();
        if (tier == null) {
            throw new IllegalStateException("Current tier is NULL");
        }
        CURRENT_TIER.set(null);
        return tier;
    }

    public static Tier peekCurrentTier() {
        var tier = CURRENT_TIER.get();
        if (tier == null) {
            throw new IllegalStateException("Current tier is NULL");
        }
        return tier;
    }

    public static void pushCurrentTier(Tier tier) {
        CURRENT_TIER.set(tier);
    }
}
