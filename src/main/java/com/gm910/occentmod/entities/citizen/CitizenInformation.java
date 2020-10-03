package com.gm910.occentmod.entities.citizen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.capabilities.formshifting.Formshift;
import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Race;
import com.gm910.occentmod.sapience.mind_and_traits.memory.Memories;
import com.gm910.occentmod.sapience.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.sapience.mind_and_traits.needs.Needs;
import com.gm910.occentmod.sapience.mind_and_traits.personality.Personality;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Genealogy;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity.DynamicSapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.religion.Religion;
import com.gm910.occentmod.sapience.mind_and_traits.skills.Skills;
import com.gm910.occentmod.sapience.mind_and_traits.task.Autonomy;
import com.gm910.occentmod.sapience.mind_and_traits.task.background.SapientRightClickFromMemory;
import com.gm910.occentmod.sapience.mind_and_traits.task.background.SapientWalkToTargetTask;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.ai.brain.task.InteractWithDoorTask;
import net.minecraft.entity.ai.brain.task.LookTask;
import net.minecraft.entity.ai.brain.task.SwimTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public class CitizenInformation<E extends CitizenEntity> extends SapientInfo<E> {

	public CitizenInformation(E en) {
		$setOwner(en);
	}

	public <T> void deserialize(Dynamic<T> dyn) {

		E en = $getOwner();
		if (dyn.get("personality").get().isPresent())
			this.personality = new Personality<>(en, dyn.get("personality").get().get());
		if (dyn.get("relationships").get().isPresent())
			this.relationships = new Relationships(en, dyn.get("relationships").get().get());
		if (dyn.get("identity").get().isPresent())
			this.identity = new DynamicSapientIdentity(dyn.get("identity").get().get(),
					(ServerWorld) this.$getOwner().world);
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
		initialize();
		deserialize(dyn);
	}

	public void initialize() {
		System.out.println("Initializing citizen entity info");
		boolean initPersona = personality == null;
		boolean initReligion = religion == null;

		if (identity == null) {
			this.identity = new DynamicSapientIdentity(Formshift.get(this.$getOwner()).getTrueForm(),
					this.$getOwner().getUniqueID(), (ServerWorld) this.$getOwner().world);
			System.out.println("Initializing citizen entity identity");
		}
		if (initPersona) {
			this.personality = new Personality<>(this.$getOwner());
			System.out.println("Initializing citizen entity personality");
		}

		if (knowledge == null) {
			this.knowledge = new Memories<>(this.$getOwner());
			System.out.println("Initializing citizen entity memories");
		}
		if (relationships == null) {
			this.relationships = new Relationships(this.$getOwner());
			System.out.println("Initializing citizen entity relationships");
		}
		if (genetics == null) {
			this.genetics = new Genetics<E>((Class<E>) this.$getOwner().getClass());
			System.out.println("Initializing citizen entity genes");
		}
		if (autonomy == null) {
			this.autonomy = new Autonomy<E>(this.$getOwner());
			System.out.println("Initializing citizen entity autonomy");
		}
		if (needs == null) {
			this.needs = new Needs<E>(this.$getOwner());

			System.out.println("Initializing citizen entity needs");
		}
		if (emotions == null) {
			this.emotions = new Emotions();
			System.out.println("Initializing citizen entity emotions");
		}
		if (skills == null) {
			this.skills = new Skills();
			System.out.println("Initializing citizen entity skills");
		}
		if (initReligion) {
			this.religion = new Religion<E>(this.getCitizen());
			System.out.println("Initializing citizen religion");
		}

		initIdentity((ServerWorld) this.$getOwner().world);
		if (this.profile == null) {

			this.profile = new GameProfile(this.$getOwner().getUniqueID(), this.getIdentity().getName().toString());
			System.out.println("Initializing citizen entity profile");
		}
		this.autonomy.registerBackgroundTasks(getDefaultBackgroundTasks());
		System.out.println("Initializing citizen entity default background tasks");
		if (initPersona) {
			personality.initializeRandomTraits();
			System.out.println("Initializing citizen entity personality traits");
		}
		if (initReligion) {
			religion.initialize();
			System.out.println("Initializing citizen entity religion info");
		}
	}

	public void initIdentity(ServerWorld world) {
		// TODO

		this.identity.setName(EmpireData.get(world).giveRandomCitizenName());
		this.getCitizen().setCustomName(new StringTextComponent(identity.getName().toString()));
		this.getCitizen().setCustomNameVisible(true);
		System.out.println("citizeninfo name " + this.identity.getName());
		this.identity.setGenealogy(new Genealogy(identity));
		System.out.println("citizeninfo owner " + $getOwner());
		if ($getOwner() != null) {
			System.out.println("citizeninfo empdata " + $getOwner().getEmpdata());
		}
		System.out.println("citizeninfo world " + world);

		Set<Empire> emps = $getOwner().getEmpdata().getInRadius(world.dimension.getType(), $getOwner().getPosition(),
				20);
		Optional<Empire> empo = emps.stream().findAny();
		Race tryRace = Race.getRaces().stream().findAny().get();
		if (empo.isPresent()) {
			tryRace = empo.get().chooseRandomRace(world.rand);
			this.getTrueIdentity().setEmpire(empo.get());
		}
		this.getGenetics().initGenes(tryRace, this.$getOwner());
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
				Pair.of(0, new LookTask(45, 90)), Pair.of(1, new SapientWalkToTargetTask(200)),
				Pair.of(1, new SapientRightClickFromMemory()));
	}

	public E getCitizen() {
		return $getOwner();
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

	@Override
	public IInventory getInventory() {
		return this.$getOwner().getInventory();
	}

	@Override
	public void onCreation() {
		initialize();
	}

	@Override
	public PlayerEntity getPlayerDelegate() {
		return super.getPlayerDelegate();
	}

}
