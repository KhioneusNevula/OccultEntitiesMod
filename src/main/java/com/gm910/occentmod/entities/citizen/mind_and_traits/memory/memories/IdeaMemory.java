package com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.SapientTask;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.TaskType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;

public class IdeaMemory<E extends LivingEntity> extends Memory<E> {

	private SapientTask<? super E> doTask;

	private Class<E> clazz;

	public IdeaMemory(E owner) {
		super(owner, MemoryType.IDEA);
		clazz = (Class<E>) owner.getClass();
		initialize();
	}

	@Override
	public E getOwner() {
		// TODO Auto-generated method stub
		return super.getOwner();
	}

	@Override
	public void setOwner(E owner) {
		// TODO Auto-generated method stub
		super.setOwner(owner);
	}

	public void initialize() {
		Set<SapientTask<? super E>> tasques = Sets.newHashSet(TaskType.getValues()).stream()
				.filter(TaskType::canBeRandomlyThoughtOf)
				.filter((m) -> ModReflect.<TaskType<? super E, ?>>instanceOf(m, TaskType.class)
						&& m.getDoerClass().isAssignableFrom(this.getOwner().getClass()))
				.map((m) -> (SapientTask<? super E>) m.createNew(SapientInfo.get(this.getOwner()).getAutonomy()))
				.filter((e) -> e.canExecute((E) this.getOwner())).collect(Collectors.toSet());
		Optional<SapientTask<? super E>> tasca = tasques.stream().findAny();
		if (tasca.isPresent()) {
			doTask = tasca.get();
		}
	}

	public IdeaMemory(E owner, Dynamic<?> dyn) {
		super(owner, MemoryType.IDEA);
		if (dyn.get("task").get().isPresent()) {
			this.doTask = TaskType.deserialize(dyn.get("task").get().get());
		}
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {

		if (doTask != null) {
			return ops.createMap(ImmutableMap.of(ops.createString("task"), doTask.serialize(ops)));
		} else {
			return ops.emptyMap();
		}
	}

	public SapientTask<? super E> getDoTask() {
		return doTask;
	}

	public void setDoTask(SapientTask<? super E> doTask) {
		this.doTask = doTask;
	}

	public boolean isEmpty() {
		return doTask == null;
	}

	@Override
	public boolean isUseless() {
		return isEmpty() || getAccessedTimes() > 0;
	}

	@Override
	public void affectCitizen(E en) {
		if (doTask != null) {
			SapientInfo.get(en).getAutonomy().considerTask(2, doTask, false);
		}
	}

}
