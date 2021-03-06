package com.gm910.occentmod.capabilities.citizeninfo;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.capabilities.GMCaps;
import com.gm910.occentmod.capabilities.IModCapability;
import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.sapience.InformationHolder;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.sapience.mind_and_traits.memory.Memories;
import com.gm910.occentmod.sapience.mind_and_traits.needs.Needs;
import com.gm910.occentmod.sapience.mind_and_traits.personality.Personality;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity.DynamicSapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.religion.Religion;
import com.gm910.occentmod.sapience.mind_and_traits.skills.Skills;
import com.gm910.occentmod.sapience.mind_and_traits.task.Autonomy;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public abstract class SapientInfo<E extends LivingEntity>
		implements IModCapability<E>, INBTSerializable<CompoundNBT>, IDynamicSerializable {

	public static final ResourceLocation LOC = new ResourceLocation(OccultEntities.MODID, "sapientinfo");

	private E owner;

	protected Personality<E> personality;
	protected Memories<E> knowledge;
	protected Relationships relationships;
	protected DynamicSapientIdentity identity;
	protected Genetics<E> genetics;
	protected Autonomy<E> autonomy;
	protected Needs<E> needs;
	protected Emotions emotions;
	protected Skills skills;
	protected Religion<E> religion;
	protected GameProfile profile;

	protected Object2IntMap<InformationHolder> tickIntervals = new Object2IntOpenHashMap<>();

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
	public DynamicSapientIdentity getTrueIdentity() {
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
	public Personality<E> getPersonality() {
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
		if (identity instanceof DynamicSapientIdentity) {
			this.identity = (DynamicSapientIdentity) identity;
		} else {
			this.identity = new DynamicSapientIdentity(identity);
		}
	}

	public void setKnowledge(Memories<E> knowledge) {
		this.knowledge = knowledge;
	}

	public void setNeeds(Needs<E> needs) {
		this.needs = needs;
	}

	public void setPersonality(Personality<E> personality) {
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
		assert laz.isPresent() : e + (e != null && e.hasCustomName() ? e.getCustomName().toString() : "")
				+ " is not an entity with SapientInfo! ";
		return laz.orElse(null);
	}

	public static boolean isSapient(LivingEntity target) {
		return getLazy(target).isPresent();
	}

	public abstract void onCreation();

	/**
	 * Checks whether a given task is appropriate for this entity class if the task
	 * is for a superclass like LivingEntity
	 * 
	 * @param tasque
	 * @return
	 */
	public boolean isAppropriate(SapientTask<?> tasque) {
		return true;
	}

	public PlayerEntity getPlayerDelegate() {
		return null;
	}

	public GameProfile getProfile() {
		return profile;
	}

	public void runRegularTick(ServerWorld world) {
		if (this.$getOwner().isAlive() && world.getEntityByUuid(this.$getOwner().getUniqueID()) == this.$getOwner()) {
			System.out.println("Running sapient info regular tick for " + this.$getOwner());
			if (this.tickIntervals.getInt(getKnowledge()) == 0) {
				this.tickIntervals.put(getKnowledge(), 1 + this.$getOwner().getRNG().nextInt(10));

			}
			if (this.$getOwner().ticksExisted % this.tickIntervals.getInt(getKnowledge()) == 0) {
				world.getProfiler().startSection("knowledge");
				this.getKnowledge().update();
				world.getProfiler().endSection();
			}

			if (this.tickIntervals.getInt(getPersonality()) == 0) {
				this.tickIntervals.put(getPersonality(), 1 + this.$getOwner().getRNG().nextInt(10));

			}
			if (this.$getOwner().ticksExisted % this.tickIntervals.getInt(getPersonality()) == 0) {
				world.getProfiler().startSection("personality");
				this.getPersonality().update();
				world.getProfiler().endSection();
			}

			if (this.tickIntervals.getInt(getRelationships()) == 0) {
				this.tickIntervals.put(getRelationships(), 1 + this.$getOwner().getRNG().nextInt(10));

			}
			if (this.$getOwner().ticksExisted % this.tickIntervals.getInt(getRelationships()) == 0) {
				world.getProfiler().startSection("relationships");
				this.getRelationships().update();
				world.getProfiler().endSection();
			}

			if (this.tickIntervals.getInt(getAutonomy()) == 0) {
				this.tickIntervals.put(getAutonomy(), 1 + this.$getOwner().getRNG().nextInt(10));

			}
			if (this.$getOwner().ticksExisted % this.tickIntervals.getInt(getAutonomy()) == 0) {
				world.getProfiler().startSection("autonomy");
				this.getAutonomy().update();
				world.getProfiler().endSection();
			}

			if (this.tickIntervals.getInt(getNeeds()) == 0) {
				this.tickIntervals.put(getNeeds(), 1 + this.$getOwner().getRNG().nextInt(10));

			}
			if (this.$getOwner().ticksExisted % this.tickIntervals.getInt(getNeeds()) == 0) {
				world.getProfiler().startSection("needs");
				this.getNeeds().update();
				world.getProfiler().endSection();
			}

			if (this.tickIntervals.getInt(getEmotions()) == 0) {
				this.tickIntervals.put(getEmotions(), 1 + this.$getOwner().getRNG().nextInt(20));

			}
			if (this.$getOwner().ticksExisted % this.tickIntervals.getInt(getEmotions()) == 0) {
				world.getProfiler().startSection("emotions");
				this.getEmotions().update();
				world.getProfiler().endSection();
			}
			if (this.tickIntervals.getInt(getReligion()) == 0) {
				this.tickIntervals.put(getReligion(), 1 + this.$getOwner().getRNG().nextInt(20));

			}
			if (this.$getOwner().ticksExisted % this.tickIntervals.getInt(getReligion()) == 0) {
				world.getProfiler().startSection("religion");
				this.getReligion().update();
				world.getProfiler().endSection();
			}
		}
	}

	public void update(ServerWorld world) {
		this.runRegularTick(world);

		System.out.println("Sapient tick END of " + this.owner + "" + this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
