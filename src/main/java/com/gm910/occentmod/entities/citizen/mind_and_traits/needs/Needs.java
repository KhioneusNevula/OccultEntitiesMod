package com.gm910.occentmod.entities.citizen.mind_and_traits.needs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.EntityDependentInformationHolder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class Needs extends EntityDependentInformationHolder<CitizenEntity> {

	private Map<NeedType<?>, NeedChecker<?>> needCheckers = new HashMap<>();

	private Map<NeedType<?>, Set<Need<?>>> needs = new NonNullMap<>(Sets::newHashSet);

	public Needs(CitizenEntity entity) {
		super(entity);
	}

	public Set<NeedType<?>> getNeedTypes() {
		return needCheckers.keySet();
	}

	public Needs(CitizenEntity en, Dynamic<?> des) {
		this(en);
		Set<Need<?>> need1s = des.get("needs").asStream().map((d) -> NeedType.deserializeStatic(d))
				.collect(Collectors.toSet());
		for (Need<?> need : need1s) {
			needs.get(need.getType()).add(need);
		}
	}

	public Set<Need<?>> getNeeds(NeedType<?> type) {
		return Sets.newHashSet(needs.get(type));
	}

	public void addNeed(Need<?> need) {
		this.needs.get(need.getType()).add(need);
	}

	public void removeNeed(Need<?> need) {
		this.needs.get(need.getType()).remove(need);
	}

	public <T> NeedChecker<T> getChecker(NeedType<T> type) {
		return (NeedChecker<T>) needCheckers.get(type);
	}

	public Needs registerNeeds(Set<NeedType<?>> needTypes) {
		for (NeedType<?> type : needTypes) {
			this.needCheckers.put(type, type.getNeedsChecker(this.getEntityIn()));

		}

		return this;
	}

	public boolean hasNeed(NeedType<?> type) {
		return needs.values().stream().flatMap((e) -> e.stream()).anyMatch((e) -> e.getType() == type);
	}

	@Override
	public void tick() {
		for (Need<?> need : this.needs.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet())) {
			if (need.isFulfilled()) {
				this.needs.get(need.getType()).remove(need);
			}
		}
		this.needCheckers.values().forEach((e) -> e.tick());
		for (NeedChecker<?> check : needCheckers.values()) {
			if (check.areNeedsFulfilled()) {
				this.needs.get(check.getType()).clear();
			}
			if (check.getNeed() != null && !needs.get(check.getType()).contains(check.getNeed())) {
				needs.get(check.getType()).add(check.getNeed());
			}
		}
		System.out.println(this.needCheckers.values().stream()
				.map((e) -> e + " : " + e.getType().getValue(this.getEntityIn())).collect(Collectors.toSet()));
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {

		T ls = ops.createList(needs.values().stream().flatMap((e) -> e.stream()).map((n) -> n.serialize(ops)));

		return ops.createMap(ImmutableMap.of(ops.createString("needs"), ls));
	}

}
