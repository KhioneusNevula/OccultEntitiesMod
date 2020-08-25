package com.gm910.occentmod.capabilities.citizeninfo;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.capabilities.GMCapabilityUser;
import com.gm910.occentmod.capabilities.IModCapability;
import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.Memories;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Needs;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity.DynamicCitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.entities.citizen.mind_and_traits.religion.Religion;
import com.gm910.occentmod.entities.citizen.mind_and_traits.skills.Skills;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.Autonomy;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class CitizenInfo<E extends LivingEntity>
		implements IModCapability<E>, INBTSerializable<CompoundNBT> {

	public static final ResourceLocation LOC = new ResourceLocation(OccultEntities.MODID, "citizeninfo");

	private E owner;

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

	@Override
	public void $setOwner(E wiz) {
		this.owner = wiz;

		MinecraftForge.EVENT_BUS.register(this);
	}

	public boolean isDeity() {
		return this.owner instanceof Deity;
	}

	@Override
	public E $getOwner() {
		return owner;
	}

	public Autonomy getAutonomy() {
		return autonomy;
	}

	public Emotions getEmotions() {
		return emotions;
	}

	public Genetics<E> getGenetics() {
		return genetics;
	}

	public CitizenIdentity getIdentity() {
		return identity.copy();
	}

	public DynamicCitizenIdentity getTrueIdentity() {
		return this.identity;
	}

	public Memories getKnowledge() {
		return knowledge;
	}

	public Needs getNeeds() {
		return needs;
	}

	public Personality getPersonality() {
		return personality;
	}

	public Relationships getRelationships() {
		return relationships;
	}

	public Religion getReligion() {
		return religion;
	}

	public Skills getSkills() {
		return skills;
	}

	public void setAutonomy(Autonomy autonomy) {
		this.autonomy = autonomy;
	}

	public void setEmotions(Emotions emotions) {
		this.emotions = emotions;
	}

	public void setGenetics(Genetics<E> genetics) {
		this.genetics = genetics;
	}

	public void setIdentity(CitizenIdentity identity) {
		if (identity instanceof DynamicCitizenIdentity) {
			this.identity = (DynamicCitizenIdentity) identity;
		} else {
			this.identity = new DynamicCitizenIdentity(identity);
		}
	}

	public void setKnowledge(Memories knowledge) {
		this.knowledge = knowledge;
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

	public void setReligion(Religion religion) {
		this.religion = religion;
	}

	public void setSkills(Skills skills) {
		this.skills = skills;
	}

	public static <T extends LivingEntity> CitizenInfo<T> get(T e) {
		return e.getCapability(GMCapabilityUser.CITIZEN_INFO).orElse(null);
	}

}
