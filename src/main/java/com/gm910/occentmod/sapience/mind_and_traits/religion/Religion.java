package com.gm910.occentmod.sapience.mind_and_traits.religion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.sapience.EntityDependentInformationHolder;
import com.gm910.occentmod.sapience.mind_and_traits.personality.Personality;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait.TraitLevel;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Genealogy;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public class Religion<E extends LivingEntity> extends EntityDependentInformationHolder<E> {

	private Deity personalGod;
	private Set<SapientTask<? super E>> worshipTasks = new HashSet<>();

	public Religion(E entity) {
		super(entity);

	}

	public Religion(E en, Dynamic<?> dyn) {
		super(en);
		if (dyn.get("deity").get().isPresent()) {
			Optional<Deity> de = EmpireData.get((ServerWorld) en.world).getAllDeities().stream()
					.filter((e) -> e.getUuid().equals(UUID.fromString(dyn.get("deity").asString("")))).findAny();
			if (de.isPresent()) {
				personalGod = de.get();
			}
		}
	}

	public void initialize() {
		Personality persona = SapientInfo.get(getEntityIn()).getPersonality();
		TraitLevel level = PersonalityTrait.PIETY.getWeightedRandomReaction(persona.getTrait(PersonalityTrait.PIETY));
		boolean atheist = false;
		if (level == TraitLevel.EXCEPTIONAL_LOW) {
			atheist = getEntityIn().getRNG().nextInt(5) < 2;
		}
		if (!atheist) {
			Genealogy gen = SapientInfo.get(getEntityIn()).getIdentity().getGenealogy();

			if (gen == null)
				return;
			if (gen.getFirstParent() != null) {
				Entity e = gen.getFirstParent().getEntity((ServerWorld) getEntityIn().world);
				Entity e2 = gen.getSecondParent().getEntity((ServerWorld) getEntityIn().world);
				// TODO
			}
		} else {
			personalGod = null;
		}
	}

	public boolean isAtheist() {
		return personalGod == null;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Map<T, T> mapa = new HashMap<>();
		if (this.personalGod != null) {
			mapa.put(ops.createString("deity"), ops.createString(personalGod.getUuid().toString()));
		}
		return ops.createMap(mapa);
	}

}
