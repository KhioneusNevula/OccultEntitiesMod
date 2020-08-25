package com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.TaskType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;

public class IdeaMemory extends Memory {

	private CitizenTask doTask;

	public IdeaMemory(LivingEntity owner) {
		super(owner, MemoryType.IDEA);
		initialize();
	}

	public void initialize() {
		Set<CitizenTask> tasques = Sets.newHashSet(TaskType.getValues()).stream()
				.filter(TaskType::canBeRandomlyThoughtOf)
				.map((m) -> m.createNew(CitizenInfo.get(this.getOwner()).orElse(null).getAutonomy()))
				.filter((e) -> e.canExecute(this.getOwner())).collect(Collectors.toSet());
		Optional<CitizenTask> tasca = tasques.stream().findAny();
		if (tasca.isPresent()) {
			doTask = tasca.get();
		}
	}

	public IdeaMemory(LivingEntity owner, Dynamic<?> dyn) {
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

	public CitizenTask getDoTask() {
		return doTask;
	}

	public void setDoTask(CitizenTask doTask) {
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
	public void affectCitizen(LivingEntity en) {
		if (doTask != null) {
			CitizenInfo.get(en).orElse(null).getAutonomy().considerTask(2, doTask, false);
		}
	}

}
