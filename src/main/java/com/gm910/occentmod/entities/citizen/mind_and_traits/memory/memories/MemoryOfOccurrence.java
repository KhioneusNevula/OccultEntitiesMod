package com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories;

import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public class MemoryOfOccurrence extends Memory {

	private Occurrence event;

	public MemoryOfOccurrence(LivingEntity owner, Occurrence deed) {
		super(owner, MemoryType.DEED);
		this.event = deed;
	}

	public MemoryOfOccurrence(LivingEntity owner, Dynamic<?> dyn) {
		this(owner, OccurrenceType.deserialize(((ServerWorld) owner.world), dyn.get("event").get().get()));
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		T deed1 = event.serialize(ops);
		T id = ops.createString(event.getType().getName().toString());
		return ops.createMap(ImmutableMap.of(ops.createString("event"), deed1, ops.createString("id"), id));
	}

	public Occurrence getEvent() {
		return event;
	}

	@Override
	public void affectCitizen(LivingEntity en) {
		event.affectCitizen(CitizenInfo.get(en).orElse(null));
	}

	public boolean couldEventBeCauseOf(MemoryOfOccurrence other) {
		return this.event.couldBeCauseOf(other.event, this.getMemoryCreationTime(), other.getMemoryCreationTime());
	}

}
