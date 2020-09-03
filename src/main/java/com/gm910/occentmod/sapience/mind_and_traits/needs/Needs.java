package com.gm910.occentmod.sapience.mind_and_traits.needs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.sapience.EntityDependentInformationHolder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.LivingEntity;

public class Needs<E extends LivingEntity> extends EntityDependentInformationHolder<E> {

	private Map<NeedType<E, ?>, NeedChecker<E, ?>> needCheckers = new HashMap<>();

	private Map<NeedType<E, ?>, Set<Need<E, ?>>> needs = new NonNullMap<>(Sets::newHashSet);

	private Object2IntMap<NeedType<E, ?>> randomCheckInterval = new Object2IntOpenHashMap<>();

	public Needs(E entity) {
		super(entity);
	}

	public int getRandomCheckInterval(NeedType<E, ?> t) {
		return randomCheckInterval.getOrDefault(t, 1);
	}

	public void newRandomCheckInterval(NeedType<E, ?> t) {
		randomCheckInterval.put(t, t.getCheckInterval(this.getEntityIn()));
	}

	public Set<NeedType<E, ?>> getNeedTypes() {
		return needCheckers.keySet();
	}

	public Needs(E en, Dynamic<?> des) {
		this(en);
		Set<Need<E, ?>> need1s = des.get("needs").asStream().map((d) -> (Need<E, ?>) NeedType.deserializeStatic(d))
				.collect(Collectors.toSet());
		for (Need<E, ?> need : need1s) {
			needs.get(need.getType()).add(need);
			newRandomCheckInterval(need.getType());
		}
	}

	public Set<Need<E, ?>> getNeeds(NeedType<E, ?> type) {
		return Sets.newHashSet(needs.get(type));
	}

	public void addNeed(Need<E, ?> need) {
		this.needs.get(need.getType()).add(need);
	}

	public void removeNeed(Need<E, ?> need) {
		this.needs.get(need.getType()).remove(need);
	}

	public <T> NeedChecker<E, T> getChecker(NeedType<E, T> type) {
		return (NeedChecker<E, T>) needCheckers.get(type);
	}

	public Needs<E> registerNeeds(Set<NeedType<E, ?>> needTypes) {
		for (NeedType<E, ?> type : needTypes) {
			this.needCheckers.put(type, type.makeNeedsChecker(this.getEntityIn()));
			this.newRandomCheckInterval(type);
		}

		return this;
	}

	public boolean hasNeed(NeedType<E, ?> type) {
		return needs.values().stream().flatMap((e) -> e.stream()).anyMatch((e) -> e.getType() == type);
	}

	@Override
	public void tick() {
		for (Need<E, ?> need : this.needs.values().stream().flatMap((e) -> e.stream()).collect(Collectors.toSet())) {
			if (need.isFulfilled()) {
				this.needs.get(need.getType()).remove(need);
			}
		}
		this.needCheckers.values().forEach((e) -> e.tick());
		for (NeedChecker<E, ?> check : needCheckers.values()) {
			if (check.areNeedsFulfilled()) {
				this.needs.get(check.getType()).clear();
			}
			if (this.getTicksExisted() % this.getRandomCheckInterval(check.getType()) == 0) {
				if (check.getNeed() != null && !needs.get(check.getType()).contains(check.getNeed())) {
					needs.get(check.getType()).add(check.getNeed());
					newRandomCheckInterval(check.getType());

				}
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " with " + this.needs;
	}

}
