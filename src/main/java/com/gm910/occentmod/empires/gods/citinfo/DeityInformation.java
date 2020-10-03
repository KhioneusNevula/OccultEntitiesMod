package com.gm910.occentmod.empires.gods.citinfo;

import java.util.HashMap;
import java.util.Map;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.capabilities.formshifting.Formshift;
import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Race.SpiritRace;
import com.gm910.occentmod.sapience.mind_and_traits.memory.Memories;
import com.gm910.occentmod.sapience.mind_and_traits.needs.Needs;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity.DynamicSapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.skills.Skills;
import com.gm910.occentmod.sapience.mind_and_traits.task.Autonomy;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.server.ServerWorld;

public class DeityInformation extends SapientInfo<Deity> {

	@Override
	public <T> T serialize(DynamicOps<T> ops) {

		Map<T, T> mapa = new HashMap<>(ImmutableMap.<T, T>of(ops.createString("personality"),
				personality.serialize(ops), ops.createString("gossip"), knowledge.serialize(ops),
				ops.createString("relationships"), relationships.serialize(ops), ops.createString("identity"),
				identity.serialize(ops), ops.createString("genetics"), genetics.serialize(ops)));
		mapa.put(ops.createString("autonomy"), autonomy.serialize(ops));
		mapa.put(ops.createString("emotions"), emotions.serialize(ops));
		return ops.createMap(ImmutableMap.copyOf(mapa));
	}

	public <T> void deserialize(Dynamic<T> dyn) {

		Deity en = this.$getOwner();
		if (dyn.get("personality").get().isPresent())
			this.relationships = new Relationships(en, dyn.get("relationships").get().get());
		if (dyn.get("identity").get().isPresent())
			this.identity = new DynamicSapientIdentity(dyn.get("identity").get().get(),
					(ServerWorld) this.$getOwner().world);
		if (dyn.get("autonomy").get().isPresent())
			this.autonomy = new Autonomy<Deity>(en, dyn.get("autonomy").get().get());
		if (dyn.get("emotions").get().isPresent())
			this.emotions = new Emotions(dyn.get("emotions").get().get());
		if (dyn.get("knowledge").get().isPresent()) {
			this.knowledge = new Memories<>(en, dyn.get("knowledge").get().get());
		}
		if (dyn.get("genetics").get().isPresent()) {
			this.genetics = new Genetics<>(dyn.get("genetics").get().get());
		}
	}

	@Override
	public IInventory getInventory() {
		return this.$getOwner().inventory;
	}

	@Override
	public Skills getSkills() {
		return new DeityEmptySkills(this.$getOwner());
	}

	@Override
	public Needs<Deity> getNeeds() {
		return new EmptyNeeds<Deity>(this.$getOwner());
	}

	@Override
	public void onCreation() {
		System.out.println("Begin deity info init");
		Deity en = this.$getOwner();
		this.relationships = new Relationships(en);
		this.identity = new DynamicSapientIdentity(Formshift.get(en).getForm(), en.getUniqueID(),
				(ServerWorld) this.$getOwner().world);
		this.autonomy = new Autonomy<Deity>(en);
		this.emotions = new Emotions();
		this.knowledge = new Memories<>(en);
		this.genetics = new Genetics<>(Deity.class);
		genetics.initGenes(SpiritRace.DEITY, this.$getOwner());
		this.profile = new GameProfile(this.$getOwner().getUniqueID(), this.getIdentity().getName().toString());
		System.out.println("End deity info init with " + this.toString());
	}

}
