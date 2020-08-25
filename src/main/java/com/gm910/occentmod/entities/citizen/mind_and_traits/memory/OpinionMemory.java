package com.gm910.occentmod.entities.citizen.mind_and_traits.memory;

import java.util.HashMap;
import java.util.Map;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.Memory;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.world.server.ServerWorld;

public class OpinionMemory extends Memory {

	private Occurrence opinionTarget;
	private OccurrenceEffect connotation;
	private OccurrenceEffect.Connotation opinion;

	public OpinionMemory(CitizenEntity owner, Occurrence cause) {
		super(owner, MemoryType.DEED);
		this.opinionTarget = cause;
		this.connotation = cause.getEffect();
		this.opinion = cause.getEffect().getEffect(owner.getIdentity());
	}

	public OpinionMemory(CitizenEntity owner, Dynamic<?> dyn) {
		this(owner, OccurrenceType.deserialize(((ServerWorld) owner.world), dyn.get("cause").get().get()));
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		Map<T, T> mapa = new HashMap<>();
		T deed1 = opinionTarget.serialize(ops);
		T id = ops.createString(opinionTarget.getType().getName().toString());
		mapa.put(ops.createString("cause"), deed1);
		mapa.put(ops.createString("id"), id);

		return ops.createMap(mapa);
	}

	public Occurrence getCause() {
		return opinionTarget;
	}

	public OccurrenceEffect getConnotation() {
		return connotation;
	}

	public OccurrenceEffect.Connotation getOpinion() {
		return opinion;
	}

	public boolean fitsObservation(Occurrence cause) {
		return cause.isSimilarTo(this.opinionTarget);
	}

	@Override
	public void affectCitizen(CitizenEntity en) {

	}

}
