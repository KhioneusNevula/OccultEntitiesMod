package com.gm910.occentmod.sapience.mind_and_traits.memory.memories;

import com.gm910.occentmod.sapience.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.CauseEffectMemory.Certainty;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;

public class ExternallyGivenMemory<E extends LivingEntity> extends Memory<E> {

	private Memory<E> delegate;

	private Certainty trust;

	private SapientIdentity giver;

	public ExternallyGivenMemory(E owner, SapientIdentity giver, Memory<E> delegate, Certainty trust) {
		super(owner, MemoryType.EXTERNALLY_GIVEN_MEMORY);
		this.delegate = delegate;
		this.trust = trust;
		this.giver = giver;
	}

	public Certainty getTrust() {
		return trust;
	}

	public Memory<E> getDelegate() {
		return delegate;
	}

	public void setDelegate(Memory<E> delegate) {
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

	public SapientIdentity getGiver() {
		return giver;
	}

	public void setGiver(SapientIdentity giver) {
		this.giver = giver;
	}

	public ExternallyGivenMemory(E owner, Dynamic<?> dyn) {
		this(owner, new SapientIdentity(dyn.get("giver").get().get()),
				Memory.deserialize(owner, dyn.get("delegate").get().get()),
				Certainty.valueOf(dyn.get("trust").asString("")));
	}

	@Override
	public void affectCitizen(E en) {
		this.delegate.affectCitizen(en);
	}

	@Override
	public ITextComponent getDisplayText() {
		// TODO Auto-generated method stub
		return delegate.getDisplayText();
	}

	@Override
	public Connotation getOpinion() {
		return delegate.getOpinion();
	}

}
