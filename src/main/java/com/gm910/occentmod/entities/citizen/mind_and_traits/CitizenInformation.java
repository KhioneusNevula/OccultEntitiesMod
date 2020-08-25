package com.gm910.occentmod.entities.citizen.mind_and_traits;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;
import com.gm910.occentmod.capabilities.formshifting.Formshift;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.Memories;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Needs;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity.DynamicCitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.entities.citizen.mind_and_traits.religion.Religion;
import com.gm910.occentmod.entities.citizen.mind_and_traits.skills.Skills;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.Autonomy;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.background.CitizenPickupItemsTask;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.ai.brain.task.InteractWithDoorTask;
import net.minecraft.entity.ai.brain.task.LookTask;
import net.minecraft.entity.ai.brain.task.SwimTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.world.server.ServerWorld;

public class CitizenInformation<E extends CitizenEntity> extends CitizenInfo<E> implements IDynamicSerializable {

	private E citizen;

	private Personality personality;
	private Memories knowledge;
	private Relationships relationships;
	private DynamicCitizenIdentity identity;
	private Genetics<E> genetics;
	private Autonomy autonomy;
	private Needs needs;
	private Emotions emotions;
	private Skills skills;
	private Religion religion;

	public CitizenInformation(E en) {
		this.citizen = en;
	}

	public <T> void deserialize(Dynamic<T> dyn) {

		E en = citizen;
		if (dyn.get("personality").get().isPresent())
			this.relationships = new Relationships(en, dyn.get("relationships").get().get());
		else
			this.relationships = new Relationships(en);
		if (dyn.get("identity").get().isPresent())
			this.identity = new DynamicCitizenIdentity(dyn.get("identity").get().get());
		else
			this.identity = new DynamicCitizenIdentity(Formshift.get(en).getTrueForm(), en.getUniqueID());
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
		if (dyn.get("emotions").get().isPresent())
			this.emotions = new Emotions(dyn.get("emotions").get().get());
		else
			this.emotions = new Emotions();
		if (dyn.get("skills").get().isPresent())
			this.skills = new Skills(dyn.get("skills").get().get());
		else
			this.skills = new Skills();
		if (dyn.get("religion").get().isPresent())
			this.religion = new Religion(en, dyn.get("religion").get().get());
	}

	public <T> CitizenInformation(E en, Dynamic<T> dyn) {
		this(en);
		deserialize(dyn);
	}

	public void initialize(boolean regular) {

		this.personality = new Personality();
		personality.initializeRandomTraits(this.getCitizen().getRNG());
		this.knowledge = new Memories(this.citizen);
		this.relationships = new Relationships(this.citizen);
		this.identity = new DynamicCitizenIdentity(Formshift.get(this.citizen).getTrueForm(),
				this.citizen.getUniqueID());
		if (regular)
			this.genetics = new Genetics<>();
		this.autonomy = new Autonomy(this.citizen);
		if (regular)
			this.needs = new Needs(this.citizen);
		this.emotions = new Emotions();
		if (regular)
			this.skills = new Skills();
		if (regular) {
			this.religion = new Religion(this.getCitizen());
			religion.initialize();
		}
		if (regular)
			this.autonomy.registerBackgroundTasks(getDefaultBackgroundTasks());
	}

	public void initIdentity(ServerWorld world) {

		// TODO
	}

	public void initValues(ServerWorld world) {
		this.initIdentity(world);
		this.needs.registerNeeds(NeedType.getCitizenNeeds());
	}

	public Set<Pair<Integer, Task<? super CitizenEntity>>> getDefaultBackgroundTasks() {
		// TODO
		return ImmutableSet.of(Pair.of(0, new SwimTask(0.4F, 0.8F)), Pair.of(0, new InteractWithDoorTask()),
				Pair.of(0, new LookTask(45, 90)), Pair.of(1, new WalkToTargetTask(200)),
				Pair.of(5, new CitizenPickupItemsTask()));
	}

	public E getCitizen() {
		return citizen;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {

		Map<T, T> mapa = new HashMap<>(ImmutableMap.<T, T>of(ops.createString("personality"),
				personality.serialize(ops), ops.createString("gossip"), knowledge.serialize(ops),
				ops.createString("relationships"), relationships.serialize(ops), ops.createString("identity"),
				identity.serialize(ops), ops.createString("genetics"), genetics.serialize(ops)));
		mapa.put(ops.createString("autonomy"), autonomy.serialize(ops));
		mapa.put(ops.createString("needs"), needs.serialize(ops));
		mapa.put(ops.createString("emotions"), emotions.serialize(ops));
		mapa.put(ops.createString("skills"), skills.serialize(ops));
		mapa.put(ops.createString("religion"), religion.serialize(ops));
		return ops.createMap(ImmutableMap.copyOf(mapa));
	}

	public void update(ServerWorld world) {

		world.getProfiler().startSection("knowledge");
		this.getKnowledge().update();
		world.getProfiler().endSection();
		world.getProfiler().startSection("personality");
		this.getPersonality().update();
		world.getProfiler().endSection();
		world.getProfiler().startSection("relationships");
		this.getRelationships().update();
		world.getProfiler().endSection();
		world.getProfiler().startSection("autonomy");
		this.getAutonomy().update();
		world.getProfiler().endSection();
		world.getProfiler().startSection("needs");
		this.getNeeds().update();
		world.getProfiler().endSection();
		world.getProfiler().startSection("emotions");
		this.getEmotions().update();
		world.getProfiler().endSection();
		world.getProfiler().startSection("religion");
		this.getReligion().update();
		world.getProfiler().endSection();
	}

	public Emotions getEmotions() {
		return emotions;
	}

	public Religion getReligion() {
		return religion;
	}

	public Autonomy getAutonomy() {
		return autonomy;
	}

	public Genetics<E> getGenetics() {
		return genetics;
	}

	public Skills getSkills() {
		return skills;
	}

	public Needs getNeeds() {
		return needs;
	}

	public Memories getKnowledge() {
		return knowledge;
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

	public void setReligion(Religion religion) {
		this.religion = religion;
	}

	public void setKnowledge(Memories gossipKnowledge) {
		this.knowledge = gossipKnowledge;
	}

	public void setEmotions(Emotions emotions) {
		this.emotions = emotions;
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

	public void setSkills(Skills skills) {
		this.skills = skills;
	}

	@Override
	public CompoundNBT serializeNBT() {

		return (CompoundNBT) serialize(NBTDynamicOps.INSTANCE);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		deserialize(GMNBT.makeDynamic(nbt));
	}

}
