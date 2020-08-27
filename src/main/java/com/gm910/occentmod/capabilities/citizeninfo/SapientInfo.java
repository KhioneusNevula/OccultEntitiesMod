package com.gm910.occentmod.capabilities.citizeninfo;

import javax.annotation.Nullable;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.capabilities.GMCaps;
import com.gm910.occentmod.capabilities.IModCapability;
import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.Memories;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Needs;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.SapientIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.SapientIdentity.DynamicCitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.religion.Religion;
import com.gm910.occentmod.entities.citizen.mind_and_traits.skills.Skills;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.Autonomy;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public abstract class SapientInfo<E extends LivingEntity>
		implements IModCapability<E>, INBTSerializable<CompoundNBT>, IDynamicSerializable {

	public static final ResourceLocation LOC = new ResourceLocation(OccultEntities.MODID, "sapientinfo");

	private E owner;

	protected Personality personality;
	protected Memories<E> knowledge;
	protected Relationships relationships;
	protected DynamicCitizenIdentity identity;
	protected Genetics<E> genetics;
	protected Autonomy<E> autonomy;
	protected Needs<E> needs;
	protected Emotions emotions;
	protected Skills skills;
	protected Religion<E> religion;

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

	@Nullable
	public Autonomy<E> getAutonomy() {
		return autonomy;
	}

	@Nullable
	public Emotions getEmotions() {
		return emotions;
	}

	@Nullable
	public Genetics<E> getGenetics() {
		return genetics;
	}

	@Nullable
	public SapientIdentity getIdentity() {
		return identity.copy();
	}

	@Nullable
	public DynamicCitizenIdentity getTrueIdentity() {
		return this.identity;
	}

	@Nullable
	public Memories<E> getKnowledge() {
		return knowledge;
	}

	@Nullable
	public Needs<E> getNeeds() {
		return needs;
	}

	@Nullable
	public Personality getPersonality() {
		return personality;
	}

	@Nullable
	public Relationships getRelationships() {
		return relationships;
	}

	@Nullable
	public Religion<E> getReligion() {
		return religion;
	}

	@Nullable
	public Skills getSkills() {
		return skills;
	}

	public void setAutonomy(Autonomy<E> autonomy) {
		this.autonomy = autonomy;
	}

	public void setEmotions(Emotions emotions) {
		this.emotions = emotions;
	}

	public void setGenetics(Genetics<E> genetics) {
		this.genetics = genetics;
	}

	public void setIdentity(SapientIdentity identity) {
		if (identity instanceof DynamicCitizenIdentity) {
			this.identity = (DynamicCitizenIdentity) identity;
		} else {
			this.identity = new DynamicCitizenIdentity(identity);
		}
	}

	public void setKnowledge(Memories<E> knowledge) {
		this.knowledge = knowledge;
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

	public void setReligion(Religion<E> religion) {
		this.religion = religion;
	}

	public void setSkills(Skills skills) {
		this.skills = skills;
	}

	public abstract IInventory getInventory();

	@Override
	public CompoundNBT serializeNBT() {
		return new CompoundNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

	}

	public <T> void deserialize(Dynamic<T> dyn) {
		this.deserializeNBT((CompoundNBT) Dynamic.convert(dyn.getOps(), NBTDynamicOps.INSTANCE, dyn.getValue()));
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		CompoundNBT nb = this.serializeNBT();

		return Dynamic.convert(NBTDynamicOps.INSTANCE, ops, nb);
	}

	public static <T extends LivingEntity> LazyOptional<SapientInfo<T>> getLazy(T e) {
		return e.getCapability(GMCaps.SAPIENT_INFO).cast();
	}

	public static <T extends LivingEntity> SapientInfo<T> get(T e) {
		LazyOptional<SapientInfo<T>> laz = getLazy(e);
		if (!laz.isPresent()) {
			throw new IllegalArgumentException(e + (e != null && e.hasCustomName() ? e.getCustomName().toString() : "")
					+ " is not an entity with SapientInfo! ");
		}
		return laz.orElse(null);
	}

	public abstract void onCreation();

}
