package com.gm910.occentmod.sapience.mind_and_traits.relationship;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.capabilities.GMCaps;
import com.gm910.occentmod.sapience.BodyForm;
import com.gm910.occentmod.sapience.EntityDependentInformationHolder;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfDeed;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class Relationships extends EntityDependentInformationHolder<LivingEntity> {

	public static final float MIN_LIKE_VALUE = -3;
	public static final float MAX_LIKE_VALUE = 3;

	public static final float MAX_TRUST_VALUE = 3;

	/**
	 * this is a map of how much a citizen likes certain people AS WELL AS their
	 * identities. float values start at 0. If they go to -1, that's moderate
	 * dislike. -2 is hate, and -3 is archnemesis-level. 1 is moderate
	 * acquaintanceship. 2 is friendship. 3 is strong familial love or romantic love
	 * if the two are unrelated.
	 */
	private Object2FloatMap<SapientIdentity> identities = new Object2FloatOpenHashMap<>();
	/**
	 * this is a map of how much a citizen TRUSTS certain people AS WELL AS their
	 * identities. float values start at 0. up to 3.
	 */
	private Object2FloatMap<SapientIdentity> trust = new Object2FloatOpenHashMap<>();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T gmemo = ops.createMap(identities.object2FloatEntrySet().stream().map((trait) -> {
			return Pair.of(trait.getKey().serialize(ops), ops.createFloat(trait.getFloatValue()));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		T tr = ops.createMap(trust.object2FloatEntrySet().stream().map((trait) -> {
			return Pair.of(trait.getKey().serialize(ops), ops.createFloat(trait.getFloatValue()));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		/*T deed = ops.createMap(deeds.entrySet().stream().map((trait) -> {
			return Pair.of(trait.getKey().serialize(ops),
					ops.createList(trait.getValue().stream().map((e) -> e.serialize(ops))));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));*/
		return ops.createMap(ImmutableMap.of(ops.createString("like"), gmemo, ops.createString("trust"), tr));

	}

	public Relationships(LivingEntity en, Dynamic<?> dyn) {
		super(en);
		Map<SapientIdentity, Float> map = dyn.get("like").asMap(SapientIdentity::new, (d) -> d.asFloat(0));
		/*Map<CitizenIdentity, Set<CitizenDeed>> map2 = new NonNullMap<CitizenIdentity, Set<CitizenDeed>>(
				() -> new HashSet<>())
						.setAs(dyn.get("deeds").asMap(CitizenIdentity::new, (e) -> new HashSet<>(
								e.asList((xcxc) -> (CitizenDeed) OccurrenceType.deserialize(xcxc)))));*/
		identities.putAll(map);
		map = dyn.get("trust").asMap(SapientIdentity::new, (d) -> d.asFloat(0));
		trust.putAll(map);
		// deeds.putAll(map2);
	}

	public Relationships(LivingEntity en) {
		super(en);
	}

	public void tick() {

	}

	public Set<SapientDeed> getDeeds(SapientIdentity identity) {
		return this.getEntityIn().getCapability(GMCaps.SAPIENT_INFO).orElse(null).getKnowledge()
				.<MemoryOfDeed>getByPredicate(
						(m) -> m instanceof MemoryOfDeed && ((MemoryOfDeed) m).getDeed().getDoer().equals(identity))
				.stream().map((e) -> e.getDeed()).collect(Collectors.toSet());
	}

	public void moveDeeds(SapientIdentity from, SapientIdentity to) {
		Set<SapientDeed> deeds = this.getDeeds(from);
		for (SapientDeed deed : deeds) {
			deed.setCitizen(to);
		}
	}

	public boolean knows(SapientIdentity id) {
		return this.identities.containsKey(id) || this.identities.containsKey(id);
	}

	public float getLikeValue(SapientIdentity citizen) {
		return this.identities.getFloat(citizen);
	}

	public void setLikeValue(SapientIdentity cit, float value) {
		SapientIdentity citizen = this.identities.keySet().stream()
				.filter((e) -> e.getCitizen().equals(cit.getCitizen())).findFirst().orElse(cit);
		this.identities.put(citizen, MathHelper.clamp(value, MIN_LIKE_VALUE, MAX_LIKE_VALUE));
	}

	public void changeLikeValue(SapientIdentity cit, float value) {
		this.setLikeValue(cit, this.getLikeValue(cit) + value);
	}

	public float getLikeValue(BodyForm citizen) {
		return this.identities.getFloat(getIdentityFor(citizen));
	}

	public float getTrustValue(SapientIdentity citizen) {
		return this.trust.getFloat(citizen);
	}

	public void setTrustValue(SapientIdentity cit, float value) {
		SapientIdentity citizen = this.trust.keySet().stream().filter((e) -> e.getCitizen().equals(cit.getCitizen()))
				.findFirst().orElse(cit);
		this.trust.put(citizen, MathHelper.clamp(value, 0, MAX_TRUST_VALUE));
	}

	public void changeTrustValue(SapientIdentity cit, float value) {
		this.setTrustValue(cit, this.getLikeValue(cit) + value);
	}

	public float getTrustValue(BodyForm citizen) {
		return this.trust.getFloat(getIdentityFor(citizen));
	}

	public SapientIdentity changeIdentity(BodyForm citizen, SapientIdentity newId) {
		newId = newId.withCitizen(citizen);
		SapientIdentity old = getIdentityFor(citizen);
		float like = getLikeValue(old);
		identities.put(newId, like);
		float trust = getTrustValue(old);
		this.trust.put(newId, trust);
		return old;
	}

	public SapientIdentity getIdentityFor(BodyForm cit) {
		Set<SapientIdentity> setta = Sets.newHashSet(identities.keySet());
		setta.addAll(this.trust.keySet());
		for (SapientIdentity e : setta) {
			if (e.getCitizen() != null && e.getCitizen().equals(cit)) {
				return e;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " with known " + this.identities.keySet();
	}

}
