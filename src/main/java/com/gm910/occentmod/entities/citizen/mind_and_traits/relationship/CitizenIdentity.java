package com.gm910.occentmod.entities.citizen.mind_and_traits.relationship;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gm910.occentmod.api.util.Translate;
import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.entities.citizen.mind_and_traits.BodyForm;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;
import net.minecraft.world.server.ServerWorld;

public class CitizenIdentity implements IDynamicSerializable {

	private BodyForm citizen;

	private String name;

	private Genealogy genealogy;

	private Race race;

	private UUID empire;

	public CitizenIdentity(BodyForm en) {
		this.citizen = en;

	}

	public CitizenIdentity(CitizenIdentity other) {
		this(other.citizen, other.name, other.genealogy, other.race, other.empire);
	}

	public CitizenIdentity(BodyForm cit, String name, Genealogy gen, Race race, UUID empire) {
		this.citizen = cit;
		this.name = name;
		this.genealogy = gen;
		this.race = race;
		this.empire = empire;
	}

	private CitizenIdentity setCitizenM(BodyForm citizen) {
		this.citizen = citizen;
		return this;
	}

	private CitizenIdentity setNameM(String name) {
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

	public CitizenIdentity withName(String name) {
		return new CitizenIdentity(this).setNameM(name);
	}

	public CitizenIdentity withCitizen(BodyForm citizen) {
		return new CitizenIdentity(this).setCitizenM(citizen);
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

	public String getName() {
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
		this.name = dyn.get("name").asString("");
		if (name.isEmpty()) {
			name = null;
		}
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
		T name = op.createString(this.name == null ? "" : this.name);
		Map<T, T> mapi = new HashMap<>(ImmutableMap.of(op.createString("id"), id, op.createString("name"), name));
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
		return op.createMap(mapi);

	}

	@Override
	public String toString() {
		return name;
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
		public DynamicCitizenIdentity(BodyForm en) {
			super(en);

		}

		public DynamicCitizenIdentity(CitizenIdentity other) {
			super(other.citizen, other.name, other.genealogy, other.race, other.empire);
		}

		public DynamicCitizenIdentity(BodyForm cit, String name, Genealogy gen, Race race, UUID empire) {
			super(cit, name, gen, race, empire);
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

		public void setName(String name) {
			super.setNameM(name);
		}

		public void setRace(Race race) {
			super.setRaceM(race);
		}

		public void setGenealogy(Genealogy gen) {
			super.setGenealogyM(gen);
		}

		public CitizenIdentity copy() {
			CitizenIdentity id = new CitizenIdentity(this);
			id.genealogy.of = id;
			return id;
		}

	}

}
