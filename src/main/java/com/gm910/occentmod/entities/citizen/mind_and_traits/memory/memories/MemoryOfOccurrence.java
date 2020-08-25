package com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.world.server.ServerWorld;

public class MemoryOfOccurrence extends Memory {

	private Occurrence event;

	public MemoryOfOccurrence(CitizenEntity owner, Occurrence deed) {
		super(owner, MemoryType.DEED);
		this.event = deed;
	}

	public MemoryOfOccurrence(CitizenEntity owner, Dynamic<?> dyn) {
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
	public void affectCitizen(CitizenEntity en) {
		event.affectCitizen(en.getInfo());
	}

	public boolean couldEventBeCauseOf(MemoryOfOccurrence other) {
		return this.event.couldBeCauseOf(other.event, this.getMemoryCreationTime(), other.getMemoryCreationTime());
	}

}
