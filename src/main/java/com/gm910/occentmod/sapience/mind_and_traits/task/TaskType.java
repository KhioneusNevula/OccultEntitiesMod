package com.gm910.occentmod.sapience.mind_and_traits.task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.sapience.EntityDependentInformationHolder;
import com.gm910.occentmod.sapience.mind_and_traits.personality.hobbies.DummyHobbyTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask.Context;
import com.gm910.occentmod.sapience.mind_and_traits.task.motion.ExploreAndObserveTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.needs.ChooseHobbyTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.needs.EatFoodFromInventory;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.GlobalPos;
import net.minecraftforge.registries.ForgeRegistries;

public class TaskType<M extends LivingEntity, I extends SapientTask<M>> {

	private static final Map<ResourceLocation, TaskType<?, ?>> TYPES = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static final TaskType<CitizenEntity, DummyHobbyTask> DUMMY_HOBBY = new TaskType<CitizenEntity, DummyHobbyTask>(
			CitizenEntity.class, DummyHobbyTask.class, GMFiles.rl("dummy_hobby"),
			(aut, e) -> new DummyHobbyTask((MemoryModuleType<GlobalPos>) ForgeRegistries.MEMORY_MODULE_TYPES
					.getValue(new ResourceLocation(e.get("memory").asString(""))), e.get("maxdist").asInt(0)),
			(aut) -> new DummyHobbyTask(MemoryModuleType.JOB_SITE, 2));

	public static final TaskType<LivingEntity, PlanTask> PLAN = new TaskType<>(LivingEntity.class, PlanTask.class,
			GMFiles.rl("plan"), PlanTask::new,
			(e) -> new PlanTask(Predicates.alwaysFalse(), Predicates.alwaysTrue(), ImmutableList.of()))
					.setCanBeRandomlyThoughtOf(false);

	public static final TaskType<CitizenEntity, EatFoodFromInventory> EAT_FOOD_FROM_INVENTORY = new TaskType<>(
			CitizenEntity.class, EatFoodFromInventory.class, GMFiles.rl("eat_food_from_inventory"),
			(aut, e) -> new EatFoodFromInventory(e), (e) -> new EatFoodFromInventory());

	public static final TaskType<MobEntity, ExploreAndObserveTask> EXPLORE_AND_OBSERVE = new TaskType<>(MobEntity.class,
			ExploreAndObserveTask.class, GMFiles.rl("explore_and_observe"),
			(aut, e) -> new ExploreAndObserveTask(e.asFloat(1)),
			(e) -> new ExploreAndObserveTask(e.getEntityIn().getAIMoveSpeed()));

	public static final TaskType<LivingEntity, ChooseHobbyTask<LivingEntity>> CHOOSE_HOBBY = new TaskType(
			LivingEntity.class, ChooseHobbyTask.class, GMFiles.rl("choose_hobby"),
			(aut, e) -> new ChooseHobbyTask(((EntityDependentInformationHolder<?>) aut).getEntityIn()),
			(e) -> new ChooseHobbyTask(((EntityDependentInformationHolder<?>) e).getEntityIn()));

	BiFunction<Autonomy<?>, Dynamic<?>, I> deserializer;
	ResourceLocation rl;
	Function<Autonomy<? extends LivingEntity>, I> generalCreator;
	boolean canBeRandomlyThoughtOf = true;

	private Class<M> doerClass;

	private Class<I> taskClass;

	private boolean isHobby;

	public TaskType(Class<M> doerClass, Class<I> taskClass, ResourceLocation rl,
			BiFunction<Autonomy<?>, Dynamic<?>, I> deserializer,
			Function<Autonomy<? extends LivingEntity>, I> creator) {
		this.rl = rl;
		this.deserializer = deserializer;
		this.generalCreator = creator;
		this.doerClass = doerClass;
		this.taskClass = taskClass;
		TYPES.put(rl, this);
	}

	public Class<M> getDoerClass() {
		return doerClass;
	}

	public Class<I> getTaskClass() {
		return taskClass;
	}

	public boolean canBeRandomlyThoughtOf() {
		return canBeRandomlyThoughtOf;
	}

	public TaskType<M, I> setCanBeRandomlyThoughtOf(boolean canBeRandomlyThoughtOf) {
		this.canBeRandomlyThoughtOf = canBeRandomlyThoughtOf;
		return this;
	}

	public ResourceLocation getResourceLocation() {
		return rl;
	}

	/**
	 * Set whether it is a hobby
	 * 
	 * @return
	 */
	public TaskType<M, I> setIsHobby() {
		this.isHobby = true;
		return this;
	}

	public boolean isHobby() {
		return isHobby;
	}

	public I runDeserialize(Autonomy<?> aut, Dynamic<?> dyn) {
		return deserializer.apply(aut, dyn);
	}

	public <R extends LivingEntity> I createNew(Autonomy<R> give) {
		return generalCreator.apply(give);
	}

	public static <M extends LivingEntity, I extends SapientTask<M>> TaskType<M, I> get(ResourceLocation rl) {
		return (TaskType<M, I>) TYPES.get(rl);
	}

	public static Collection<TaskType<?, ?>> getValues() {
		return TYPES.values();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " " + this.rl;
	}

	@SuppressWarnings("unchecked")
	public static <T extends SapientTask<? extends LivingEntity>> T deserialize(Autonomy<?> aut, Dynamic<?> d) {
		TaskType<?, ?> type = TaskType.get(new ResourceLocation(d.get("type").asString("")));
		T get = (T) type.runDeserialize(aut, d.get("data").get().get());
		get.setContext(d.get("contexts").asStream().map((e) -> Context.valueOf(e.asString("")))
				.collect(Collectors.toList()).toArray(new Context[0]));
		ModReflect.setField(Task.class, Task.Status.class, "status", "field_220384_a", get,
				d.get("running").asBoolean(false) ? Task.Status.RUNNING : Task.Status.STOPPED);
		ModReflect.setField(Task.class, long.class, "stopTime", "field_220385_b", get, d.get("time").asLong(0));
		return get;
	}
}