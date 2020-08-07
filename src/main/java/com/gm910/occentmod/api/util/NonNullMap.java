package com.gm910.occentmod.api.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This map's <code>{@link java.util.Map#get get}</code> method is equivalent to
 * the normal map's <code>{@link Map#computeIfAbsent computeIfAbsent}</code>
 * method using a supplier given in the constructor. It's good for storing lists
 * The
 * <code>{@link Map#getOrDefault getOrDefault} method with a <code>null</code>
 * argument can be used to simulate regular map behavior
 * 
 * @author borah
 *
 * @param <K>
 * @param <V>
 */
public class NonNullMap<K, V> extends HashMap<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1019130149273473657L;

	private BiFunction<K, NonNullMap<K, V>, V> supplier;

	public static final float DEFAULT_LOAD_FACTOR = ModReflect.getField(HashMap.class, float.class,
			"DEFAULT_LOAD_FACTOR", null, null);
	public static final int MAXIMUM_CAPACITY = ModReflect.getField(HashMap.class, int.class, "MAXIMUM_CAPACITY", null,
			null);

	public NonNullMap(BiFunction<K, NonNullMap<K, V>, V> supplier) {
		this.supplier = supplier;
	}

	public NonNullMap(Function<K, V> supplier) {
		this.supplier = (a, b) -> {
			return supplier.apply(a);
		};
	}

	public static <K, V> NonNullMap<K, V> create(Function<NonNullMap<K, V>, V> supplier) {
		BiFunction<K, NonNullMap<K, V>, V> supplier2 = (a, b) -> {
			return supplier.apply(b);
		};
		return new NonNullMap<K, V>(supplier2);
	}

	public NonNullMap(Supplier<V> supplier) {
		this.supplier = (a, b) -> {
			return supplier.get();
		};
	}

	public BiFunction<K, NonNullMap<K, V>, V> getSupplier() {
		return supplier;
	}

	@Override
	public V put(K key, V value) {
		if (isEmptyCollection(value)) {
			return super.get(key);
		}
		return super.put(key, value);
	}

	public boolean isEmptyCollection(Object value) {
		return (value instanceof Collection) && ((Collection<?>) value).isEmpty();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {

		super.putAll(m.entrySet().stream().filter((e) -> !isEmptyCollection(e))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
	}

	@Override
	public V putIfAbsent(K key, V value) {
		if (isEmptyCollection(value)) {
			return super.get(key);
		}
		return super.putIfAbsent(key, value);
	}

	@Override
	public V get(Object key) {

		if (super.get(key) == null && ModReflect.<K>instanceOf(key, null)) {
			this.put((K) key, supplier.apply((K) key, this));
		}
		return super.get(key);
	}

	/**
	 * Replaces the entire contents of this map with the contents of the other map
	 * Returns self for easy chaining (since the constructor only accepts suppliers)
	 * 
	 * @param newmap
	 */
	public NonNullMap<K, V> setAs(Map<? extends K, ? extends V> newmap) {
		this.clear();
		this.putAll(newmap);
		return this;
	}

	/**
	 * Constructs map the same way as the given Returns self for easy chaining
	 * (since the constructor only accepts suppliers)
	 * 
	 * @param newmap
	 */
	public NonNullMap<K, V> setAs(int capacity) {

		return setAs(capacity, DEFAULT_LOAD_FACTOR);
	}

	public NonNullMap<K, V> setAs(int initialCapacity, float loadFactor) {

		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal load factor: " + loadFactor);

		System.out.println("Nonnullmapmaking "
				+ ModReflect.setField(HashMap.class, float.class, "loadFactor", null, this, loadFactor));
		System.out.println("Nonnullmapmaking " + ModReflect.setField(HashMap.class, int.class, "threshold", null, this,
				ModReflect.run(HashMap.class, int.class, "tableSizeFor", null, initialCapacity)));

		return this;
	}

	public NonNullMap<K, V> initialize(Consumer<NonNullMap<K, V>> initializer) {
		initializer.accept(this);
		return this;
	}

}
