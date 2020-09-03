package com.gm910.occentmod.ritualism;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.ResourceLocation;

public class RitualNodeType<T extends RitualNode> {

	private static final Map<ResourceLocation, RitualNodeType<?>> TYPES = new HashMap<>();

	public final ResourceLocation resource;

	private Function<Ritual, T> supplier;

	public RitualNodeType(ResourceLocation rl, Function<Ritual, T> supplier) {
		this.resource = rl;
		this.supplier = supplier;
		TYPES.put(rl, this);
	}

	public T create(Ritual ritual) {
		return supplier.apply(ritual);
	}

	public T deserialize(Ritual ritual, Dynamic<?> dyn) {
		T creat = create(ritual);
		dyn = dyn.get("data").get().isPresent() ? dyn.get("data").get().get() : dyn;
		creat.read(dyn);

		return creat;
	}

	public static <T extends RitualNode> T getFromDynamic(Ritual ritual, Dynamic<?> dyn) {
		RitualNodeType<T> type = (RitualNodeType<T>) get(new ResourceLocation(dyn.get("id").asString("")));
		return type.deserialize(ritual, dyn.get("data").get().get());
	}

	public static <M extends RitualNode> RitualNodeType<M> get(ResourceLocation rl) {
		return (RitualNodeType<M>) TYPES.get(rl);
	}

	public static Set<RitualNodeType<?>> getAllTypes() {
		return Sets.newHashSet(TYPES.values());
	}

}
