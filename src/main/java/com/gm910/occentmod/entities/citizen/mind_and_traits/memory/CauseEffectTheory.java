package com.gm910.occentmod.entities.citizen.mind_and_traits.memory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceType;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.math.MathHelper;

public class CauseEffectTheory extends CitizenMemory {

	private Occurrence cause;
	private Occurrence effect;
	private OccurrenceEffect connotation;

	private int observationCount;

	public CauseEffectTheory(CitizenEntity owner, Occurrence cause, @Nullable Occurrence effect,
			@Nullable OccurrenceEffect connotation) {
		super(owner, CitizenMemoryType.DEED);
		this.cause = cause;
		this.effect = effect;
		this.connotation = effect.getEffect();
	}

	public CauseEffectTheory(CitizenEntity owner, Dynamic<?> dyn) {
		this(owner, OccurrenceType.deserialize(dyn.get("cause").get().get()),
				dyn.get("effect").get().isPresent() ? OccurrenceType.deserialize(dyn.get("effect").get().get()) : null,
				new OccurrenceEffect(dyn.get("connotation").get().get()));
		this.observationCount = dyn.get("obcount").asInt(0);
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		Map<T, T> mapa = new HashMap<>();
		T deed1 = cause.serialize(ops);
		T id = ops.createString(cause.getType().getName().toString());
		if (effect != null) {
			T eff = this.effect.serialize(ops);
			mapa.put(ops.createString("effect"), eff);
		}
		mapa.put(ops.createString("cause"), deed1);
		mapa.put(ops.createString("connotation"), this.connotation.serialize(ops));
		mapa.put(ops.createString("id"), id);
		mapa.put(ops.createString("obcount"), ops.createInt(observationCount));

		return ops.createMap(mapa);
	}

	public Occurrence getCause() {
		return cause;
	}

	public Occurrence getEffect() {
		return effect;
	}

	public OccurrenceEffect getConnotation() {
		return connotation;
	}

	public int getObservationCount() {
		return observationCount;
	}

	public void setObservationCount(int observationCount) {
		this.observationCount = observationCount;
	}

	public boolean fitsObservation(Occurrence cause, Occurrence effect) {
		return cause.isSimilarTo(this.cause) && effect.isSimilarTo(this.effect);
	}

	public void incrementObservation(int amount) {
		this.setObservationCount(getObservationCount() + amount);
	}

	public Certainty getCertainty() {
		return Certainty.values()[MathHelper.clamp(this.observationCount, 0, Certainty.values().length - 1)];
	}

	@Override
	public void affectCitizen(CitizenEntity en) {

	}

	public static enum Certainty {
		FALSE, TENTATIVE, MAYBE, ALMOST_CERTAIN, TRUE
	}

}
