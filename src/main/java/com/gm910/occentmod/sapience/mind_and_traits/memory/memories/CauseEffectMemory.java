package com.gm910.occentmod.sapience.mind_and_traits.memory.memories;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.math.Fraction;

import com.gm910.occentmod.api.language.Translate;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceType;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

public class CauseEffectMemory<E extends LivingEntity> extends Memory<E> {

	private Occurrence cause;
	private Occurrence effect;
	private OccurrenceEffect connotation;

	private int observationCount;

	public CauseEffectMemory(E owner, Occurrence cause, @Nullable Occurrence effect,
			@Nullable OccurrenceEffect connotation) {
		super(owner, MemoryType.DEED);
		this.cause = cause;
		this.effect = effect;
		this.connotation = effect.getEffect();
		this.setMemTolerance(Fraction.getFraction(1, 5));
	}

	public CauseEffectMemory(E owner, Dynamic<?> dyn) {
		this(owner, OccurrenceType.deserialize(((ServerWorld) owner.world), dyn.get("cause").get().get()),
				dyn.get("effect").get().isPresent()
						? OccurrenceType.deserialize(((ServerWorld) owner.world), dyn.get("effect").get().get())
						: null,
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

	@Override
	public Connotation getOpinion() {
		return connotation.getEffect(SapientInfo.get(this.getOwner()).getIdentity());
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
	public void affectCitizen(E en) {

	}

	public static enum Certainty {
		FALSE("false", -2), TENTATIVE("tentative", -1), MAYBE("maybe", 0), ALMOST_CERTAIN("almost_certain", 1),
		TRUE("true", 2);

		private String displayKey;

		private int ord;

		private Certainty(String display, int ord) {
			this.displayKey = display;
		}

		public int getOrd() {
			return ord;
		}

		public int compareCertainty(Certainty other) {
			return this.ord == other.ord ? 0 : (this.ord > other.ord ? 1 : -1);
		}

		public String getDisplayKey() {
			return displayKey;
		}

		public ITextComponent getDisplay() {
			return Translate.make("certainty." + displayKey);
		}
	}

}
