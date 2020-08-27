package com.gm910.occentmod.entities.citizen.mind_and_traits;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.capabilities.formshifting.Formshift;
import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.Memories;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Needs;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Genealogy;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.SapientIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.SapientIdentity.DynamicCitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.religion.Religion;
import com.gm910.occentmod.entities.citizen.mind_and_traits.skills.Skills;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.Autonomy;
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
import net.minecraft.inventory.IInventory;
import net.minecraft.world.server.ServerWorld;

public class CitizenInformation<E extends CitizenEntity> extends SapientInfo<E> {

	private E citizen;

	public CitizenInformation(E en) {
		this.citizen = en;
		$setOwner(en);
	}

	public <T> void deserialize(Dynamic<T> dyn) {

		E en = citizen;
		if (dyn.get("personality").get().isPresent())
			this.relationships = new Relationships(en, dyn.get("relationships").get().get());
		if (dyn.get("identity").get().isPresent())
			this.identity = new DynamicCitizenIdentity(dyn.get("identity").get().get());
		if (dyn.get("genetics").get().isPresent())
			this.genetics = new Genetics<E>(dyn.get("genetics").get().get());
		if (dyn.get("autonomy").get().isPresent())
			this.autonomy = new Autonomy<E>(en, dyn.get("autonomy").get().get());
		if (dyn.get("needs").get().isPresent())
			this.needs = new Needs<E>(en, dyn.get("needs").get().get());
		if (dyn.get("emotions").get().isPresent())
			this.emotions = new Emotions(dyn.get("emotions").get().get());
		if (dyn.get("skills").get().isPresent())
			this.skills = new Skills(dyn.get("skills").get().get());
		if (dyn.get("religion").get().isPresent())
			this.religion = new Religion<E>(en, dyn.get("religion").get().get());
		if (dyn.get("knowledge").get().isPresent()) {
			this.knowledge = new Memories<>(en, dyn.get("knowledge").get().get());
		}
	}

	public <T> CitizenInformation(E en, Dynamic<T> dyn) {
		this(en);
		deserialize(dyn);
	}

	public void initialize(boolean regular) {

		this.personality = new Personality();
		personality.initializeRandomTraits(this.getCitizen().getRNG());
		this.knowledge = new Memories<>(this.citizen);
		this.relationships = new Relationships(this.citizen);
		this.identity = new DynamicCitizenIdentity(Formshift.get(this.citizen).getTrueForm(),
				this.citizen.getUniqueID());
		this.genetics = new Genetics<E>((Class<E>) this.citizen.getClass());
		this.autonomy = new Autonomy<E>(this.citizen);
		this.autonomy.registerBackgroundTasks(getDefaultBackgroundTasks());
		this.needs = new Needs<E>(this.citizen);
		this.emotions = new Emotions();
		this.skills = new Skills();
		this.religion = new Religion<E>(this.getCitizen());
		religion.initialize();
		initIdentity((ServerWorld) this.citizen.world);
	}

	public void initIdentity(ServerWorld world) {
		// TODO

		this.identity = new DynamicCitizenIdentity(this.citizen.getForm(), this.citizen.getUniqueID());
		this.identity.setName(EmpireData.get(world).giveRandomCitizenName());
		System.out.println(this.identity.getName());
		this.identity.setGenealogy(new Genealogy(identity));
		Set<Empire> emps = citizen.getEmpdata().getInRadius(world.dimension.getType(), citizen.getPosition(), 20);
		Optional<Empire> empo = emps.stream().findAny();
		Race tryRace = Race.getRaces().stream().findAny().get();
		if (empo.isPresent()) {
			tryRace = empo.get().chooseRandomRace(world.rand);
			this.getTrueIdentity().setEmpire(empo.get());
		}
		this.getGenetics().initGenes(tryRace, this.citizen);
		this.initValues(world);
		this.getTrueIdentity().setName(EmpireData.get(world).giveRandomCitizenName());
		this.getTrueIdentity().setRace(this.getGenetics().getRace());
	}

	public void initValues(ServerWorld world) {
		this.needs.registerNeeds(NeedType.getCitizenNeeds());
	}

	public <F extends CitizenEntity> Set<Pair<Integer, Task<? super F>>> getDefaultBackgroundTasks() {
		// TODO
		return ImmutableSet.of(Pair.of(0, new SwimTask(0.4F, 0.8F)), Pair.of(0, new InteractWithDoorTask()),
				Pair.of(0, new LookTask(45, 90)), Pair.of(1, new WalkToTargetTask(200)));
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

	public Religion<E> getReligion() {
		return religion;
	}

	public Autonomy<E> getAutonomy() {
		return autonomy;
	}

	public Genetics<E> getGenetics() {
		return genetics;
	}

	public Skills getSkills() {
		return skills;
	}

	public Needs<E> getNeeds() {
		return needs;
	}

	public Memories<E> getKnowledge() {
		return knowledge;
	}

	public DynamicCitizenIdentity getTrueIdentity() {
		return identity;
	}

	public SapientIdentity getIdentity() {
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
		$setOwner(citizen);
	}

	public void setReligion(Religion<E> religion) {
		this.religion = religion;
	}

	public void setKnowledge(Memories<E> gossipKnowledge) {
		this.knowledge = gossipKnowledge;
	}

	public void setEmotions(Emotions emotions) {
		this.emotions = emotions;
	}

	public void setIdentity(SapientIdentity identity) {
		if (identity instanceof DynamicCitizenIdentity) {
			this.identity = (DynamicCitizenIdentity) identity;
		} else {
			this.identity = new DynamicCitizenIdentity(identity);
		}
	}

	public void setNeeds(Needs<E> needs) {
		this.needs = needs;
	}

	public void setPersonality(Personality personality) {
		this.personality = personality;
	}

	public void setRelationships(Relationships relationships) {
		this.relationships = relationships;
	}

	public void setAutonomy(Autonomy<E> autonomy) {
		this.autonomy = autonomy;
	}

	public void setSkills(Skills skills) {
		this.skills = skills;
	}

	@Override
	public IInventory getInventory() {
		return this.$getOwner().getInventory();
	}

	@Override
	public void onCreation() {
		initialize(true);
	}

}
