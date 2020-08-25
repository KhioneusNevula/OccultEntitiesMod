package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.hobbies.DummyHobbyTask;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask.Context;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.needs.EatFoodFromInventory;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;

import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.GlobalPos;
import net.minecraftforge.registries.ForgeRegistries;

public class TaskType<I extends CitizenTask> {

	private static final Map<ResourceLocation, TaskType<?>> TYPES = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static final TaskType<DummyHobbyTask> DUMMY_HOBBY = new TaskType<>(GMFiles.rl("dummy_hobby"),
			(e) -> new DummyHobbyTask((MemoryModuleType<GlobalPos>) ForgeRegistries.MEMORY_MODULE_TYPES
					.getValue(new ResourceLocation(e.get("memory").asString(""))), e.get("maxdist").asInt(0)),
			(aut) -> new DummyHobbyTask(MemoryModuleType.JOB_SITE, 2));

	public static final TaskType<PlanTask> PLAN = new TaskType<>(GMFiles.rl("plan"), PlanTask::new,
			(e) -> new PlanTask(Predicates.alwaysFalse(), Predicates.alwaysTrue(), ImmutableList.of()))
					.setCanBeRandomlyThoughtOf(false);

	public static final TaskType<EatFoodFromInventory> EAT_FOOD_FROM_INVENTORY = new TaskType<>(
			GMFiles.rl("eat_food_from_inventory"), (e) -> new EatFoodFromInventory(e),
			(e) -> new EatFoodFromInventory());

	Function<Dynamic<?>, I> deserializer;
	ResourceLocation rl;
	Function<Autonomy, I> generalCreator;
	boolean canBeRandomlyThoughtOf = true;

	public TaskType(ResourceLocation rl, Function<Dynamic<?>, I> deserializer, Function<Autonomy, I> creator) {
		this.rl = rl;
		this.deserializer = deserializer;
		this.generalCreator = creator;
		TYPES.put(rl, this);
	}

	public boolean canBeRandomlyThoughtOf() {
		return canBeRandomlyThoughtOf;
	}

	public TaskType<I> setCanBeRandomlyThoughtOf(boolean canBeRandomlyThoughtOf) {
		this.canBeRandomlyThoughtOf = canBeRandomlyThoughtOf;
		return this;
	}

	public ResourceLocation getResourceLocation() {
		return rl;
	}

	public I runDeserialize(Dynamic<?> dyn) {
		return deserializer.apply(dyn);
	}

	public I createNew(Autonomy give) {
		return generalCreator.apply(give);
	}

	public static <I extends CitizenTask> TaskType<I> get(ResourceLocation rl) {
		return (TaskType<I>) TYPES.get(rl);
	}

	public static Collection<TaskType<?>> getValues() {
		return TYPES.values();
	}

	@SuppressWarnings("unchecked")
	public static <T extends CitizenTask> T deserialize(Dynamic<?> d) {
		TaskType<?> type = TaskType.get(new ResourceLocation(d.get("type").asString("")));
		T get = (T) type.runDeserialize(d.get("data").get().get());
		get.setContext(d.get("contexts").asStream().map((e) -> Context.valueOf(e.asString("")))
				.collect(Collectors.toList()).toArray(new Context[0]));
		ModReflect.setField(Task.class, Task.Status.class, "status", "field_220384_a", get,
				d.get("running").asBoolean(false) ? Task.Status.RUNNING : Task.Status.STOPPED);
		ModReflect.setField(Task.class, long.class, "stopTime", "field_220385_b", get, d.get("time").asLong(0));
		return get;
	}
}