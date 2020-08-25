package com.gm910.occentmod.entities.citizen.mind_and_traits.memory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.EntityDependentInformationHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.emotions.Mood;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.CauseEffectMemory.Certainty;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.ExternallyGivenMemory;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.IdeaMemory;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories.Memory;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;

public class Memories extends EntityDependentInformationHolder<LivingEntity> {

	/**
	 * 
	 */
	private Set<Memory> knowledge = new HashSet<>();

	private Set<Memory> forgotten = new HashSet<>();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T gmemo = ops.createList(knowledge.stream().map((trait) -> {
			return trait.serialize(ops);
		}));
		T g = ops.createList(forgotten.stream().map((trait) -> {
			return trait.serialize(ops);
		}));
		return ops.createMap(ImmutableMap.of(ops.createString("knowledge"), gmemo, ops.createString("forgotten"), g));
	}

	public Memories(CitizenEntity en, Dynamic<?> dyn) {
		super(en);
		Set<Memory> map2 = dyn.get("knowledge").asStream().map((d) -> Memory.deserialize(this.getEntityIn(), d))
				.collect(Collectors.toSet());
		knowledge.addAll(map2);
		map2 = dyn.get("forgotten").asStream().map((d) -> Memory.deserialize(this.getEntityIn(), d))
				.collect(Collectors.toSet());
		forgotten.addAll(map2);
	}

	public Memories(CitizenEntity en) {
		super(en);
	}

	public void tick() {
		processMemories();
		generateIdeas();
	}

	public void generateIdeas() {

		CitizenInfo<LivingEntity> info = CitizenInfo.get(getEntityIn()).orElse(null);

		float chancia = this.getEntityIn().getRNG().nextFloat();
		float inqui = info.getPersonality().getTrait(PersonalityTrait.INQUISITIVITY);
		if (chancia < inqui && !info.getEmotions().getMoods().contains(Mood.CREATIVE)) {
			info.getEmotions().addMood(Mood.CREATIVE, (int) (inqui * 40), this.getEntityIn());
		}

		if (info.getEmotions().getMoods().contains(Mood.CREATIVE) && chancia < inqui) {
			this.addKnowledge(new IdeaMemory(this.getEntityIn()));
		}

		for (IdeaMemory mem : this.<IdeaMemory>getByPredicate((e) -> e instanceof IdeaMemory)) {
			if (!mem.isUseless()) {
				chancia = this.getEntityIn().getRNG().nextFloat();
				if (chancia < inqui) {
					mem.affectCitizen(this.getEntityIn());
					mem.access();
				}
			}
		}
	}

	public void processMemories() {
		for (Memory mem : new HashSet<>(this.knowledge)) {
			long existed = mem.getTicksExisted();
			int accesses = mem.getAccessedTimes();
			if (mem.memTolerance() <= 0 ? false
					: (existed > 24000 && accesses < existed / mem.memTolerance() || mem.isUseless())) {
				mem.setAccessedTimes(0);
				this.forget(mem);
			}
		}
	}

	public Set<Memory> getKnowledge() {
		return new HashSet<>(this.knowledge);
	}

	public Set<Memory> getForgottenKnowledge() {
		return new HashSet<>(this.forgotten);
	}

	public boolean knows(Memory mem) {
		return this.knowledge.contains(mem);
	}

	public boolean knows(Predicate<? super Memory> pred) {
		return this.knowledge.stream().anyMatch(pred);
	}

	public <T extends Memory> Set<T> getByPredicate(Predicate<T> pred) {
		Set<T> t = this.knowledge.stream().filter((m) -> ModReflect.<T>instanceOf(m, null)).map((d) -> (T) d)
				.filter(pred).collect(Collectors.toSet());
		return t;
	}

	public Set<Memory> fromTime(long minGrace, long maxGrace) {
		return this
				.getByPredicate((e) -> e.getMemoryCreationTime() >= minGrace && e.getMemoryCreationTime() <= maxGrace);
	}

	public void addKnowledge(Memory mem) {
		mem.setOwner(this.getEntityIn());
		this.knowledge.add(mem);
	}

	public void shareKnowledge(Memory mem, CitizenEntity other) {
		other.getKnowledge().receiveKnowledge(mem);

	}

	public void receiveKnowledge(Memory mem) {

		float trustProba = CitizenInfo.get(this.getEntityIn()).orElse(null).getRelationships().getTrustValue(
				CitizenInfo.get(mem.getOwner()).orElse(null).getIdentity()) / Relationships.MAX_TRUST_VALUE;
		Certainty trust = Certainty.values()[(int) (trustProba * Certainty.values().length)];
		if (mem.getOwner() != this.getEntityIn()) {
			mem = new ExternallyGivenMemory(this.getEntityIn(),
					CitizenInfo.get(mem.getOwner()).orElse(null).getIdentity(), mem, trust);
		}

		Memory copy = Memory.copy(this.getEntityIn(), mem);

		copy.affectCitizen(this.getEntityIn());

		this.addKnowledge(copy);
	}

	public void forget(Memory mem) {
		this.knowledge.remove(mem);
		this.forgotten.add(mem);
	}

}
