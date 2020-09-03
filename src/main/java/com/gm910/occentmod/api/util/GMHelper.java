package com.gm910.occentmod.api.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;

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

	public static double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(max, val));
	}

	public static <T> T weightedRandom(Map<T, Double> weights) {

		// Credit: https://stackoverflow.com/a/6737362
		double totalWeight = 0.0d;
		for (T i : weights.keySet()) {
			totalWeight += weights.getOrDefault(i, 0.0);
		}
		List<T> races = new ArrayList<>(weights.keySet());
		// Now choose a random item
		int randomIndex = 0;
		double random = Math.random() * totalWeight;
		for (int i = 0; i < races.size(); ++i) {
			random -= weights.getOrDefault(races.get(i), 0.0);
			if (random <= 0.0d) {
				randomIndex = i;
				break;
			}
		}
		return races.get(randomIndex);
	}

	public static <T> T weightedRandomFloats(Map<T, Float> weights) {
		return weightedRandom(
				weights.entrySet().stream().map((entry) -> Pair.of(entry.getKey(), (double) entry.getValue()))
						.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
	}

	public static <T> int count(List<T> list, T obj) {
		int count = 0;
		for (int i = list.indexOf(obj); i != -1
				|| i < list.size() - 1; i = list.subList(i + 1, list.size()).indexOf(obj)) {
			count++;
		}
		return count;
	}

}
