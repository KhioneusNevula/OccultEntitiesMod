package com.gm910.occentmod.entities.citizen.mind_and_traits.relationship;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gm910.occentmod.api.language.Translate;
import com.gm910.occentmod.api.language.NamePhonemicHelper.PhonemeWord;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.entities.citizen.mind_and_traits.BodyForm;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.world.server.ServerWorld;

public class SapientIdentity implements IDynamicSerializable {

	private BodyForm citizen;

	private PhonemeWord name;

	private Genealogy genealogy;

	private Race race;

	private UUID empire;

	private UUID trueId;

	public SapientIdentity(BodyForm en, UUID trueId) {
		this.citizen = en;
		this.trueId = trueId;

	}

	public UUID getTrueId() {
		return trueId;
	}

	public SapientIdentity(SapientIdentity other) {
		this(other.citizen, other.trueId, other.name, other.genealogy, other.race, other.empire);
	}

	public SapientIdentity(BodyForm cit, UUID trueId, PhonemeWord name, Genealogy gen, Race race, UUID empire) {
		this.citizen = cit;
		this.name = name;
		this.genealogy = gen;
		this.race = race;
		this.empire = empire;
		this.trueId = trueId;
	}

	private SapientIdentity setCitizenM(BodyForm citizen) {
		this.citizen = citizen;
		return this;
	}

	private SapientIdentity setTrueIdM(UUID trueId) {
		this.trueId = trueId;
		return this;
	}

	private SapientIdentity setNameM(PhonemeWord name) {
		this.name = name;
		return this;
	}

	private SapientIdentity setGenealogyM(Genealogy genealogy) {
		this.genealogy = genealogy;
		return this;
	}

	private SapientIdentity setRaceM(Race race) {
		this.race = race;
		return this;
	}

	public SapientIdentity setEmpireM(UUID empire) {
		this.empire = empire;
		return this;
	}

	public Entity getEntity(ServerWorld world2) {
		return (LivingEntity) ServerPos.getEntityFromUUID(this.trueId, world2.getServer());
	}

	public SapientIdentity withName(PhonemeWord name) {
		return new SapientIdentity(this).setNameM(name);
	}

	public SapientIdentity withCitizen(BodyForm citizen) {
		return new SapientIdentity(this).setCitizenM(citizen);
	}

	public SapientIdentity withTrueId(UUID tru1) {
		return new SapientIdentity(this).setTrueIdM(tru1);
	}

	public SapientIdentity withGenealogy(Genealogy gen) {
		return new SapientIdentity(this).setGenealogyM(gen);
	}

	public SapientIdentity withRace(Race race) {
		return new SapientIdentity(this).setRaceM(race);
	}

	public SapientIdentity withEmpire(UUID empire) {
		return new SapientIdentity(this).setEmpireM(empire);
	}

	public SapientIdentity withEmpire(Empire empire) {
		return new SapientIdentity(this).setEmpireM(empire.getEmpireId());
	}

	public Genealogy getGenealogy() {
		return genealogy;
	}

	public PhonemeWord getName() {
		return name;
	}

	public UUID getEmpireId() {
		return empire;
	}

	public Empire getEmpire(EmpireData dat) {
		return dat.getEmpire(empire);
	}

	public Race getRace() {
		return race;
	}

	public <T> SapientIdentity(Dynamic<T> dyn) {

		this.citizen = new BodyForm(UUID.fromString(dyn.get("id").asString("")));
		if (dyn.get("name").get().isPresent()) {
			this.name = new PhonemeWord(dyn.get("name").get().get());
		}
		this.trueId = UUID.fromString(dyn.get("truid").asString(""));

		OptionalDynamic<?> dyn1 = dyn.get("genealogy");
		if (dyn1.get().isPresent()) {
			genealogy = new Genealogy(dyn1.get().get());
		}

		race = dyn1.get("race").get().isPresent() ? Race.fromId(dyn.get("race").asInt(0)) : null;
		empire = dyn1.get("empire").get().isPresent() ? UUID.fromString(dyn.get("empire").asString("")) : null;
	}

	public BodyForm getCitizen() {
		return citizen;
	}

	public boolean equals(Object obj) {
		return this.citizen.equals(((SapientIdentity) obj).citizen);
	}

	public boolean trueEquals(Object obj) {

		return this.serialize(NBTDynamicOps.INSTANCE).equals(((SapientIdentity) obj).serialize(NBTDynamicOps.INSTANCE));
	}

	@Override
	public <T> T serialize(DynamicOps<T> op) {
		T id = op.createString(this.citizen.getFormId().toString());
		T tru = op.createString(this.trueId.toString());

		Map<T, T> mapi = new HashMap<>(ImmutableMap.of(op.createString("id"), id, op.createString("truid"), tru));
		if (genealogy != null) {
			T gen = genealogy.serialize(op);
			mapi.put(op.createString("genealogy"), gen);
		}
		if (empire != null) {
			T em = op.createString(this.empire.toString());
			mapi.put(op.createString("empire"), em);
		}
		if (race != null) {
			T rac = op.createInt(this.race.id);
			mapi.put(op.createString("race"), rac);
		}
		if (name != null) {
			T nam = name.serialize(op);
			mapi.put(op.createString("name"), nam);
		}
		return op.createMap(mapi);

	}

	@Override
	public String toString() {
		return "Citizen of name " + (name == null ? "Nameless Citizen" : name);
	}

	public String getString(ServerWorld world) {
		EmpireData dat = EmpireData.get(world);
		Empire em = null;
		if (this.empire != null) {
			em = dat.getEmpire(this.empire);
		}
		if (em != null) {
			return Translate.translate("speech.of", name, em.getSingleName());
		}
		return toString();
	}

	public static class DynamicCitizenIdentity extends SapientIdentity {
		public DynamicCitizenIdentity(BodyForm en, UUID tru) {
			super(en, tru);

		}

		public DynamicCitizenIdentity(SapientIdentity other) {
			super(other.citizen, other.trueId, other.name, other.genealogy, other.race, other.empire);
		}

		public DynamicCitizenIdentity(BodyForm cit, UUID tru, PhonemeWord name, Genealogy gen, Race race, UUID empire) {
			super(cit, tru, name, gen, race, empire);
		}

		public <T> DynamicCitizenIdentity(Dynamic<T> dyn) {
			<T>super(dyn);
		}

		public void setEmpire(Empire emp) {
			super.setEmpireM(emp.getEmpireId());
		}

		public void setEmpire(UUID emp) {
			super.setEmpireM(emp);
		}

		public void setCitizen(BodyForm cit) {
			super.setCitizenM(cit);
		}

		public void setName(PhonemeWord name) {
			super.setNameM(name);
		}

		public void setRace(Race race) {
			super.setRaceM(race);
		}

		public void setGenealogy(Genealogy gen) {
			super.setGenealogyM(gen);
		}

		public void setTrueId(UUID tru) {
			super.setTrueIdM(tru);
		}

		public SapientIdentity copy() {
			SapientIdentity id = new SapientIdentity(this);
			if (id.genealogy != null)
				id.genealogy.of = id;
			return id;
		}

	}

}
