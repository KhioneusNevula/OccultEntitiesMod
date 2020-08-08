package com.gm910.occentmod.entities.citizen.mind_and_traits.needs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.checkers.HungerChecker;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.needs.EatFoodFromInventory;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.ResourceLocation;

public class NeedType<T> {

	private static final Map<ResourceLocation, NeedType<?>> TYPES = new HashMap<>();

	public static final NeedType<Float> HUNGER = new NeedType<Float>(GMFiles.rl("hunger"), (en) -> en.getFoodLevel(),
			(a, d) -> d.asFloat(0), (d, n) -> d.createFloat(n.getDesiredValue()), (m, e) -> new HungerChecker(m, e),
			(m, e) -> Sets.newHashSet(new EatFoodFromInventory()), true); // TODO
	ResourceLocation resource;

	Function<CitizenEntity, T> getVal;

	BiFunction<NeedType<T>, Dynamic<?>, T> deserialize;

	BiFunction<DynamicOps<?>, Need<T>, ?> serializer;

	BiFunction<NeedType<T>, CitizenEntity, NeedChecker<T>> checker;

	BiFunction<Need<T>, CitizenEntity, Set<CitizenTask>> needFulfillmentTask;

	public final boolean citizenNeed;

	public NeedType(ResourceLocation rl, Function<CitizenEntity, T> getValue,
			BiFunction<NeedType<T>, Dynamic<?>, T> deserializer, BiFunction<DynamicOps<?>, Need<T>, ?> serializer,
			BiFunction<NeedType<T>, CitizenEntity, NeedChecker<T>> checker,
			BiFunction<Need<T>, CitizenEntity, Set<CitizenTask>> needFulfillmentTask, boolean citizenNeed) {
		this.resource = rl;
		this.deserialize = deserializer;
		this.getVal = getValue;
		this.serializer = serializer;
		this.needFulfillmentTask = needFulfillmentTask;
		this.checker = checker;
		this.citizenNeed = citizenNeed;
		TYPES.put(rl, this);
	}

	public NeedChecker<T> getNeedsChecker(CitizenEntity en) {
		return checker.apply(this, en);
	}

	public Set<CitizenTask> getNeedFulfillmentTask(Need<T> need, CitizenEntity owner) {
		return needFulfillmentTask.apply(need, owner);
	}

	public ResourceLocation getResource() {
		return resource;
	}

	public <M> M serialize(DynamicOps<M> ops, Need<T> need) {
		return (M) serializer.apply(ops, need);
	}

	public T getValue(CitizenEntity t) {
		return getVal.apply(t);
	}

	public Need<T> deserialize(Dynamic<?> dyn) {
		Need<T> n = new Need<>(this, deserialize.apply(this, dyn.get("data").get().get()));
		n.setFulfilled(dyn.get("fulf").asBoolean(false));
		return n;
	}

	public static <T> Need<T> deserializeStatic(Dynamic<?> dyn) {
		NeedType<T> type = (NeedType<T>) get(new ResourceLocation(dyn.get("type").asString("")));
		return type.deserialize(dyn.get("data").get().get());
	}

	public static <T> NeedType<T> get(ResourceLocation rl) {
		return (NeedType<T>) TYPES.get(rl);
	}

	public static Collection<NeedType<?>> getAll() {
		return TYPES.values();
	}

	public static Set<NeedType<?>> getCitizenNeeds() {
		return TYPES.values().stream().filter((e) -> e.citizenNeed).collect(Collectors.toSet());
	}

}
