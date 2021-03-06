package com.gm910.occentmod.sapience.mind_and_traits.needs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Emotions.EmotionType;
import com.gm910.occentmod.sapience.mind_and_traits.needs.checkers.EmotionChecker;
import com.gm910.occentmod.sapience.mind_and_traits.needs.checkers.HungerChecker;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.TaskType;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class NeedType<E extends LivingEntity, T> {

	private static final Map<ResourceLocation, NeedType<?, ?>> TYPES = new HashMap<>();

	public static final NeedType<CitizenEntity, Float> HUNGER = new NeedType<CitizenEntity, Float>(CitizenEntity.class,
			Float.class, GMFiles.rl("hunger"), (en) -> en.getFoodLevel(), (a, d) -> d.asFloat(0),
			(d, n) -> d.createFloat(n.getDesiredValue()), (m, e) -> new HungerChecker(m, e),
			(m, e) -> Sets.newHashSet(TaskType.EAT_FOOD_FROM_INVENTORY.createNew(e.getAutonomy())), true); // TODO

	public static final NeedType<LivingEntity, Float> HAPPINESS = new NeedType<LivingEntity, Float>(LivingEntity.class,
			Float.class, GMFiles.rl("happiness"),
			(en) -> SapientInfo.get(en).getEmotions().getLevel(EmotionType.HAPPINESS), (a, d) -> d.asFloat(0),
			(d, n) -> d.createFloat(n.getDesiredValue()), (m, e) -> new EmotionChecker(m, EmotionType.HAPPINESS, e),
			(m, e) -> Sets.newHashSet(), true); // TODO

	public static final NeedType<LivingEntity, Float> SOCIAL = new NeedType<LivingEntity, Float>(LivingEntity.class,
			Float.class, GMFiles.rl("social"), (en) -> SapientInfo.get(en).getEmotions().getLevel(EmotionType.SOCIAL),
			(a, d) -> d.asFloat(0), (d, n) -> d.createFloat(n.getDesiredValue()),
			(m, e) -> new EmotionChecker(m, EmotionType.SOCIAL, e), (m, e) -> Sets.newHashSet(), true); // TODO

	public static final NeedType<LivingEntity, Float> FUN = new NeedType<LivingEntity, Float>(LivingEntity.class,
			Float.class, GMFiles.rl("fun"), (en) -> SapientInfo.get(en).getEmotions().getLevel(EmotionType.FUN),
			(a, d) -> d.asFloat(0), (d, n) -> d.createFloat(n.getDesiredValue()),
			(m, e) -> new EmotionChecker(m, EmotionType.FUN, e),
			(m, e) -> Sets.newHashSet(TaskType.CHOOSE_HOBBY.createNew(SapientInfo.get(e).getAutonomy())), true); // TODO

	public static final NeedType<LivingEntity, Float> COMFORT = new NeedType<LivingEntity, Float>(LivingEntity.class,
			Float.class, GMFiles.rl("comfort"), (en) -> SapientInfo.get(en).getEmotions().getLevel(EmotionType.COMFORT),
			(a, d) -> d.asFloat(0), (d, n) -> d.createFloat(n.getDesiredValue()),
			(m, e) -> new EmotionChecker(m, EmotionType.COMFORT, e), (m, e) -> Sets.newHashSet(), true); // TODO

	ResourceLocation resource;

	Function<E, T> getVal;

	BiFunction<NeedType<E, T>, Dynamic<?>, T> deserialize;

	BiFunction<DynamicOps<?>, Need<E, T>, ?> serializer;

	BiFunction<NeedType<E, T>, E, NeedChecker<E, T>> checker;

	BiFunction<Need<E, T>, E, Set<SapientTask<E>>> needFulfillmentTask;

	public final boolean citizenNeed;

	private Class<E> ownerType;
	private Class<T> valueType;

	public NeedType(Class<E> ownerType, Class<T> valueType, ResourceLocation rl, Function<E, T> getValue,
			BiFunction<NeedType<E, T>, Dynamic<?>, T> deserializer, BiFunction<DynamicOps<?>, Need<E, T>, ?> serializer,
			BiFunction<NeedType<E, T>, E, NeedChecker<E, T>> checker,
			BiFunction<Need<E, T>, E, Set<SapientTask<E>>> needFulfillmentTask, boolean citizenNeed) {
		this.resource = rl;
		this.deserialize = deserializer;
		this.getVal = getValue;
		this.serializer = serializer;
		this.needFulfillmentTask = needFulfillmentTask;
		this.checker = checker;
		this.citizenNeed = citizenNeed;
		this.ownerType = ownerType;
		this.valueType = valueType;
		TYPES.put(rl, this);
	}

	public Class<E> getOwnerType() {
		return ownerType;
	}

	public Class<T> getValueType() {
		return valueType;
	}

	public NeedChecker<E, T> makeNeedsChecker(E en) {
		return checker.apply(this, en);
	}

	public int getCheckInterval(E en) {
		return en.getRNG().nextInt(100);
	}

	public Set<SapientTask<E>> getNeedFulfillmentTask(Need<E, T> need, E owner) {
		return needFulfillmentTask.apply(need, owner);
	}

	public ResourceLocation getResource() {
		return resource;
	}

	public <M> M serialize(DynamicOps<M> ops, Need<E, T> need) {
		return (M) serializer.apply(ops, need);
	}

	public T getValue(E t) {
		return getVal.apply(t);
	}

	public Need<E, T> deserialize(Dynamic<?> dyn) {
		Need<E, T> n = new Need<>(this, deserialize.apply(this, dyn.get("data").get().get()));
		n.setFulfilled(dyn.get("fulf").asBoolean(false));
		return n;
	}

	public static <E extends LivingEntity, T> Need<E, T> deserializeStatic(Dynamic<?> dyn) {
		NeedType<E, T> type = (NeedType<E, T>) get(new ResourceLocation(dyn.get("type").asString("")));
		return type.deserialize(dyn.get("data").get().get());
	}

	public static <E extends LivingEntity, T> NeedType<E, T> get(ResourceLocation rl) {
		return (NeedType<E, T>) TYPES.get(rl);
	}

	public static Collection<NeedType<?, ?>> getAll() {
		return TYPES.values();
	}

	public static <E extends CitizenEntity> Set<NeedType<E, ?>> getCitizenNeeds() {
		return TYPES.values().stream()
				.filter((e) -> e.citizenNeed && ModReflect.<NeedType<E, ?>>instanceOf(e, NeedType.class))
				.map((e) -> (NeedType<E, ?>) e).collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " " + this.resource;
	}

}
