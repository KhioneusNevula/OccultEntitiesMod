package com.gm910.occentmod.api.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class GMHelper {
	private GMHelper() {
	}

	public static <E> E create(E object, Function<E, E> runwith) {
		return runwith.apply(object);
	}

	public static <E> E create(E object, Consumer<E> runwith) {
		return create(object, (e) -> {
			runwith.accept(e);
			return e;
		});
	}

	public static <K, V> Map<K, V> createHashMap(Consumer<Map<K, V>> initializer) {
		Map<K, V> object = new HashMap<>();
		initializer.accept(object);
		return object;
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T[]> getArrayClass(Class<T> clazz) {
		return ((Class<T[]>) Array.newInstance(clazz, 0).getClass());
	}

}
