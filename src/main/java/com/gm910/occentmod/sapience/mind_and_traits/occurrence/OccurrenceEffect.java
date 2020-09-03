package com.gm910.occentmod.sapience.mind_and_traits.occurrence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.IDynamicSerializable;

public class OccurrenceEffect implements IDynamicSerializable {

	public static enum Connotation {
		SAVIOR(2), HELPFUL(1), HARMFUL(-1), FATAL(-2), INDIFFERENT(0);

		private int val;

		public static final int MAX = 2;
		public static final int MIN = -2;

		private Connotation(int val) {
			this.val = val;
		}

		public int getValue() {
			return val;
		}

		public boolean harmful() {
			return this.val < 0;
		}

		public boolean helpful() {
			return this.val > 0;
		}
	}

	Map<SapientIdentity, OccurrenceEffect.Connotation> effects;

	public OccurrenceEffect(Map<SapientIdentity, OccurrenceEffect.Connotation> affected) {
		effects = new HashMap<>(affected);
	}

	public OccurrenceEffect(Dynamic<?> dyn) {
		this(dyn.asMap((k) -> new SapientIdentity(k), (m) -> Connotation.valueOf(m.asString(""))));
	}

	public OccurrenceEffect.Connotation getEffect(SapientIdentity id) {
		return effects.getOrDefault(id, OccurrenceEffect.Connotation.INDIFFERENT);
	}

	public boolean wasAffected(SapientIdentity id) {
		return getEffect(id) != OccurrenceEffect.Connotation.INDIFFERENT;
	}

	public Set<SapientIdentity> getAffected() {
		return this.effects.keySet().stream().filter((m) -> wasAffected(m)).collect(Collectors.toSet());
	}

	public Map<SapientIdentity, OccurrenceEffect.Connotation> getEffects() {
		return effects;
	}

	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createMap(this.effects.entrySet().stream()
				.map((e) -> Pair.of(e.getKey().serialize(ops), ops.createString(e.getValue().name())))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
	}

	@Override
	public boolean equals(Object obj) {
		return this.effects.equals(((OccurrenceEffect) obj).effects);
	}
}