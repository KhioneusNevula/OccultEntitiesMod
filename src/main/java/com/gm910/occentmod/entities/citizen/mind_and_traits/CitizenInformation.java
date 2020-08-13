package com.gm910.occentmod.entities.citizen.mind_and_traits;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gm910.occentmod.capabilities.formshifting.Formshift;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.entities.citizen.mind_and_traits.gossip.GossipHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Needs;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity.DynamicCitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.Autonomy;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.IDynamicSerializable;
import net.minecraft.world.server.ServerWorld;

public class CitizenInformation<E extends CitizenEntity> implements IDynamicSerializable {

	private E citizen;

	private Personality personality;
	private GossipHolder gossipKnowledge;
	private Relationships relationships;
	private DynamicCitizenIdentity identity;
	private Genetics<E> genetics;
	private Autonomy autonomy;
	private Needs needs;

	public CitizenInformation(E en) {
		this.citizen = en;
	}

	public <T> CitizenInformation(E en, Dynamic<T> dyn) {
		this(en);
		if (dyn.get("gossip").get().isPresent())
			this.gossipKnowledge = new GossipHolder(en, dyn.get("gossip").get().get());
		else
			this.gossipKnowledge = new GossipHolder(en);
		if (dyn.get("personality").get().isPresent())
			this.personality = new Personality(dyn.get("personality").get().get());
		else
			this.personality = new Personality();
		if (dyn.get("personality").get().isPresent())
			this.relationships = new Relationships(en, dyn.get("relationships").get().get());
		else
			this.relationships = new Relationships(en);
		if (dyn.get("identity").get().isPresent())
			this.identity = new DynamicCitizenIdentity(dyn.get("identity").get().get());
		else
			this.identity = new DynamicCitizenIdentity(Formshift.get(en).getTrueForm());
		if (dyn.get("genetics").get().isPresent())
			this.genetics = new Genetics<E>(dyn.get("genetics").get().get());
		else
			this.genetics = new Genetics<E>();
		if (dyn.get("autonomy").get().isPresent())
			this.autonomy = new Autonomy(en, dyn.get("autonomy").get().get());
		else
			this.autonomy = new Autonomy(en);
		if (dyn.get("needs").get().isPresent())
			this.needs = new Needs(en, dyn.get("needs").get().get());
		else
			this.needs = new Needs(en);
	}

	public void initialize() {

		this.personality = new Personality();
		this.gossipKnowledge = new GossipHolder(this.citizen);
		this.relationships = new Relationships(this.citizen);
		this.identity = new DynamicCitizenIdentity(Formshift.get(this.citizen).getTrueForm());
		this.genetics = new Genetics<>();
		this.autonomy = new Autonomy(this.citizen);
		this.needs = new Needs(this.citizen);

		this.autonomy.registerBackgroundTasks(getDefaultBackgroundTasks());
	}

	public void initIdentity(ServerWorld world) {

		// TODO
	}

	public void initValues(ServerWorld world) {
		this.initIdentity(world);
		this.needs.registerNeeds(NeedType.getCitizenNeeds());
	}

	public Set<Pair<Integer, CitizenTask>> getDefaultBackgroundTasks() {
		// TODO
		return ImmutableSet.of();
	}

	public E getCitizen() {
		return citizen;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {

		Map<T, T> mapa = new HashMap<>(ImmutableMap.<T, T>of(ops.createString("personality"),
				personality.serialize(ops), ops.createString("gossip"), gossipKnowledge.serialize(ops),
				ops.createString("relationships"), relationships.serialize(ops), ops.createString("identity"),
				identity.serialize(ops), ops.createString("genetics"), genetics.serialize(ops)));
		mapa.put(ops.createString("autonomy"), autonomy.serialize(ops));
		mapa.put(ops.createString("needs"), needs.serialize(ops));
		return ops.createMap(ImmutableMap.copyOf(mapa));
	}

	public void update(ServerWorld world) {

		world.getProfiler().startSection("gossipknowledge");
		this.getGossipKnowledge().tick();
		world.getProfiler().endSection();
		world.getProfiler().startSection("personality");
		this.getPersonality().tick();
		world.getProfiler().endSection();
		world.getProfiler().startSection("relationships");
		this.getRelationships().tick();
		world.getProfiler().endSection();
		world.getProfiler().startSection("autonomy");
		this.getAutonomy().tick();
		world.getProfiler().endSection();
		world.getProfiler().startSection("needs");
		this.getNeeds().tick();
		world.getProfiler().endSection();
	}

	public Autonomy getAutonomy() {
		return autonomy;
	}

	public Genetics<E> getGenetics() {
		return genetics;
	}

	public Needs getNeeds() {
		return needs;
	}

	public GossipHolder getGossipKnowledge() {
		return gossipKnowledge;
	}

	public DynamicCitizenIdentity getTrueIdentity() {
		return identity;
	}

	public CitizenIdentity getIdentity() {
		return identity.copy();
	}

	public Personality getPersonality() {
		return personality;
	}

	public Relationships getRelationships() {
		return relationships;
	}

	public void setGenetics(Genetics<E> genetics) {
		this.genetics = genetics;
	}

	public void setCitizen(E citizen) {
		this.citizen = citizen;
	}

	public void setGossipKnowledge(GossipHolder gossipKnowledge) {
		this.gossipKnowledge = gossipKnowledge;
	}

	public void setIdentity(CitizenIdentity identity) {
		if (identity instanceof DynamicCitizenIdentity) {
			this.identity = (DynamicCitizenIdentity) identity;
		} else {
			this.identity = new DynamicCitizenIdentity(identity);
		}
	}

	public void setNeeds(Needs needs) {
		this.needs = needs;
	}

	public void setPersonality(Personality personality) {
		this.personality = personality;
	}

	public void setRelationships(Relationships relationships) {
		this.relationships = relationships;
	}

	public void setAutonomy(Autonomy autonomy) {
		this.autonomy = autonomy;
	}

}
