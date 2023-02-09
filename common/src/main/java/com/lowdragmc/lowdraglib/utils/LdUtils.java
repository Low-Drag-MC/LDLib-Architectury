package com.lowdragmc.lowdraglib.utils;

import java.util.function.Consumer;

public class LdUtils {
	private LdUtils() {
		throw new UnsupportedOperationException("can't instantiate LdUtils");
	}

	public static <T> T make(T value, Consumer<T> operate) {
		operate.accept(value);
		return value;
	}
}
