package com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories;

import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.CauseEffectMemory.Certainty;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;

public class ExternallyGivenMemory extends Memory {

	private Memory delegate;

	private Certainty trust;

	private CitizenIdentity giver;

	public ExternallyGivenMemory(LivingEntity owner, CitizenIdentity giver, Memory delegate, Certainty trust) {
		super(owner, MemoryType.EXTERNALLY_GIVEN_MEMORY);
		this.delegate = delegate;
		this.trust = trust;
		this.giver = giver;
	}

	public Certainty getTrust() {
		return trust;
	}

	public Memory getDelegate() {
		return delegate;
	}

	public void setDelegate(Memory delegate) {
		this.delegate = delegate;
	}

	public void setTrust(Certainty trust) {
		this.trust = trust;
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		T del = delegate.serialize(ops);
		T cer = ops.createString(trust.name());
		T id = this.giver.serialize(ops);
		return ops.createMap(ImmutableMap.of(ops.createString("delegate"), del, ops.createString("trust"), cer,
				ops.createString("giver"), id));
	}

	public CitizenIdentity getGiver() {
		return giver;
	}

	public void setGiver(CitizenIdentity giver) {
		this.giver = giver;
	}

	public ExternallyGivenMemory(LivingEntity owner, Dynamic<?> dyn) {
		this(owner, new CitizenIdentity(dyn.get("giver").get().get()),
				Memory.deserialize(owner, dyn.get("delegate").get().get()),
				Certainty.valueOf(dyn.get("trust").asString("")));
	}

	@Override
	public void affectCitizen(LivingEntity en) {

	}

}
