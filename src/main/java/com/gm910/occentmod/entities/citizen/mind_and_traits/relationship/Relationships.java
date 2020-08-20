package com.gm910.occentmod.entities.citizen.mind_and_traits.relationship;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.BodyForm;
import com.gm910.occentmod.entities.citizen.mind_and_traits.EntityDependentInformationHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryOfDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.CitizenDeed;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.math.MathHelper;

public class Relationships extends EntityDependentInformationHolder<CitizenEntity> {

	public static final float MIN_LIKE_VALUE = -3;
	public static final float MAX_LIKE_VALUE = 3;

	/**
	 * this is a map of how much a citizen likes certain people AS WELL AS their
	 * identities. float values start at 0. If they go to -1, that's moderate
	 * dislike. -2 is hate, and -3 is archnemesis-level. 1 is moderate
	 * acquaintanceship. 2 is friendship. 3 is strong familial love or romantic love
	 * if the two are unrelated.
	 */
	private Object2FloatMap<CitizenIdentity> identities = new Object2FloatOpenHashMap<>();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T gmemo = ops.createMap(identities.entrySet().stream().map((trait) -> {
			return Pair.of(trait.getKey().serialize(ops), ops.createFloat(trait.getValue()));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		/*T deed = ops.createMap(deeds.entrySet().stream().map((trait) -> {
			return Pair.of(trait.getKey().serialize(ops),
					ops.createList(trait.getValue().stream().map((e) -> e.serialize(ops))));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));*/
		return ops
				.createMap(ImmutableMap.of(ops.createString("identities"), gmemo/*, ops.createString("deeds"), deed*/));

	}

	public Relationships(CitizenEntity en, Dynamic<?> dyn) {
		super(en);
		Map<CitizenIdentity, Float> map = dyn.get("identities").asMap(CitizenIdentity::new, (d) -> d.asFloat(0));
		/*Map<CitizenIdentity, Set<CitizenDeed>> map2 = new NonNullMap<CitizenIdentity, Set<CitizenDeed>>(
				() -> new HashSet<>())
						.setAs(dyn.get("deeds").asMap(CitizenIdentity::new, (e) -> new HashSet<>(
								e.asList((xcxc) -> (CitizenDeed) OccurrenceType.deserialize(xcxc)))));*/
		identities.putAll(map);
		// deeds.putAll(map2);
	}

	public Relationships(CitizenEntity en) {
		super(en);
	}

	public void tick() {

	}

	public Set<CitizenIdentity> getIdentities() {
		return this.identities.keySet();
	}

	public Set<CitizenDeed> getDeeds(CitizenIdentity identity) {
		return this.getEntityIn().getKnowledge()
				.<MemoryOfDeed>getByPredicate(
						(m) -> m instanceof MemoryOfDeed && ((MemoryOfDeed) m).getDeed().getCitizen().equals(identity))
				.stream().map((e) -> e.getDeed()).collect(Collectors.toSet());
	}

	public void moveDeeds(CitizenIdentity from, CitizenIdentity to) {
		Set<CitizenDeed> deeds = this.getDeeds(from);
		for (CitizenDeed deed : deeds) {
			deed.setCitizen(to);
		}
	}

	public float getLikeValue(CitizenIdentity citizen) {
		return this.identities.getFloat(citizen);
	}

	public void setLikeValue(CitizenIdentity citizen, float value) {
		this.identities.put(citizen, MathHelper.clamp(value, MIN_LIKE_VALUE, MAX_LIKE_VALUE));
	}

	public void changeLikeValue(CitizenIdentity cit, float value) {
		this.setLikeValue(cit, this.getLikeValue(cit) + value);
	}

	public float getLikeValue(BodyForm citizen) {
		return this.identities.getFloat(getIdentityFor(citizen));
	}

	public CitizenIdentity getIdentityFor(BodyForm cit) {
		for (CitizenIdentity e : identities.keySet()) {
			if (e.getCitizen() != null && e.getCitizen().equals(cit)) {
				return e;
			}
		}
		return null;
	}

}
