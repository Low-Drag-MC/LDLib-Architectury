package com.lowdragmc.lowdraglib.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * @author KilaBash
 * @date 2023/5/30
 * @implNote Range
 */
public class Range {

    @Getter @Setter
    protected Number a, b;

    public Range(Number a, Number b) {
        this.a = a;
        this.b = b;
    }

    public Number getMin() {
        return Math.min(a.doubleValue(), b.doubleValue());
    }

    public Number getMax() {
        return Math.min(a.doubleValue(), b.doubleValue());
    }
}
