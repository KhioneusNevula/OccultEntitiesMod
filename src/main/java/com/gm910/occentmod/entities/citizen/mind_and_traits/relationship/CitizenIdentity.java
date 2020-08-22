package com.gm910.occentmod.entities.citizen.mind_and_traits.relationship;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.api.util.Translate;
import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.entities.citizen.NamePhonemicHelper.PhonemeWord;
import com.gm910.occentmod.entities.citizen.mind_and_traits.BodyForm;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.Entity;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.world.server.ServerWorld;

public class CitizenIdentity implements IDynamicSerializable {

	private BodyForm citizen;

	private PhonemeWord name;

	private Genealogy genealogy;

	private Race race;

	private UUID empire;

	private UUID trueId;

	public CitizenIdentity(BodyForm en, UUID trueId) {
		this.citizen = en;
		this.trueId = trueId;

	}

	public UUID getTrueId() {
		return trueId;
	}

	public CitizenIdentity(CitizenIdentity other) {
		this(other.citizen, other.trueId, other.name, other.genealogy, other.race, other.empire);
	}

	public CitizenIdentity(BodyForm cit, UUID trueId, PhonemeWord name, Genealogy gen, Race race, UUID empire) {
		this.citizen = cit;
		this.name = name;
		this.genealogy = gen;
		this.race = race;
		this.empire = empire;
		this.trueId = trueId;
	}

	private CitizenIdentity setCitizenM(BodyForm citizen) {
		this.citizen = citizen;
		return this;
	}

	private CitizenIdentity setTrueIdM(UUID trueId) {
		this.trueId = trueId;
		return this;
	}

	private CitizenIdentity setNameM(PhonemeWord name) {
		this.name = name;
		return this;
	}

	private CitizenIdentity setGenealogyM(Genealogy genealogy) {
		this.genealogy = genealogy;
		return this;
	}

	private CitizenIdentity setRaceM(Race race) {
		this.race = race;
		return this;
	}

	public CitizenIdentity setEmpireM(UUID empire) {
		this.empire = empire;
		return this;
	}

	public Entity getEntity(ServerWorld world2) {
		return ServerPos.getEntityFromUUID(this.trueId, world2.getServer());
	}

	public CitizenIdentity withName(PhonemeWord name) {
		return new CitizenIdentity(this).setNameM(name);
	}

	public CitizenIdentity withCitizen(BodyForm citizen) {
		return new CitizenIdentity(this).setCitizenM(citizen);
	}

	public CitizenIdentity withTrueId(UUID tru1) {
		return new CitizenIdentity(this).setTrueIdM(tru1);
	}

	public CitizenIdentity withGenealogy(Genealogy gen) {
		return new CitizenIdentity(this).setGenealogyM(gen);
	}

	public CitizenIdentity withRace(Race race) {
		return new CitizenIdentity(this).setRaceM(race);
	}

	public CitizenIdentity withEmpire(UUID empire) {
		return new CitizenIdentity(this).setEmpireM(empire);
	}

	public CitizenIdentity withEmpire(Empire empire) {
		return new CitizenIdentity(this).setEmpireM(empire.getEmpireId());
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

	public <T> CitizenIdentity(Dynamic<T> dyn) {

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

	@Override
	public boolean equals(Object obj) {

		return this.citizen.equals(((CitizenIdentity) obj).citizen);
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

	public static class DynamicCitizenIdentity extends CitizenIdentity {
		public DynamicCitizenIdentity(BodyForm en, UUID tru) {
			super(en, tru);

		}

		public DynamicCitizenIdentity(CitizenIdentity other) {
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

		public CitizenIdentity copy() {
			CitizenIdentity id = new CitizenIdentity(this);
			id.genealogy.of = id;
			return id;
		}

	}

}
