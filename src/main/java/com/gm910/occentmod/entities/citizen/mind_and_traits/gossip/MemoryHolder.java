package com.gm910.occentmod.entities.citizen.mind_and_traits.gossip;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.EntityDependentInformationHolder;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.nbt.NBTDynamicOps;

public class MemoryHolder extends EntityDependentInformationHolder<CitizenEntity> {

	/**
	 * 
	 */
	private Set<CitizenMemory> knowledge = new HashSet<>();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T gmemo = ops.createList(knowledge.stream().map((trait) -> {
			return trait.serialize(ops);
		}));
		return ops.createMap(ImmutableMap.of(ops.createString("knowledge"), gmemo));
	}

	public MemoryHolder(CitizenEntity en, Dynamic<?> dyn) {
		super(en);
		Set<CitizenMemory> map2 = dyn.get("knowledge").asStream()
				.map((d) -> CitizenMemory.deserialize(this.getEntityIn(), d)).collect(Collectors.toSet());
		knowledge.addAll(map2);
	}

	public MemoryHolder(CitizenEntity en) {
		super(en);
	}

	public void tick() {

	}

	public Set<CitizenMemory> getKnowledge() {
		return new HashSet<>(this.knowledge);
	}

	public void addKnowledge(CitizenMemory mem) {
		this.knowledge.add(mem);
	}

	public void shareKnowledge(CitizenMemory mem, CitizenEntity other) {
		other.getKnowledge().receiveKnowledge(mem);

	}

	public void receiveKnowledge(CitizenMemory mem) {
		CitizenMemory copy = CitizenMemory.deserialize(this.getEntityIn(),
				GMNBT.makeDynamic(mem.serialize(NBTDynamicOps.INSTANCE)));

		copy.affectCitizen(this.getEntityIn());
		this.addKnowledge(copy);
	}

	public void forget(CitizenMemory mem) {
		this.knowledge.remove(mem);
	}

}
