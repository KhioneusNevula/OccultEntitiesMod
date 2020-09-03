package com.gm910.occentmod.entities.citizen;

import java.util.Set;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.GeneralInventory;
import com.gm910.occentmod.capabilities.GMCaps;
import com.gm910.occentmod.capabilities.formshifting.Formshift;
import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.init.DataInit;
import com.gm910.occentmod.init.EntityInit;
import com.gm910.occentmod.sapience.BodyForm;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.sapience.mind_and_traits.memory.Memories;
import com.gm910.occentmod.sapience.mind_and_traits.needs.Needs;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceData;
import com.gm910.occentmod.sapience.mind_and_traits.personality.Personality;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Genealogy;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity.DynamicSapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.skills.Skills;
import com.gm910.occentmod.sapience.mind_and_traits.task.Autonomy;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.INPC;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class CitizenEntity extends AgeableEntity implements INPC {

	public static Set<MemoryModuleType<?>> MEMORY_TYPES;

	public static Set<SensorType<? extends Sensor<? super CitizenEntity>>> SENSOR_TYPES;

	{
		if (MEMORY_TYPES == null)
			MEMORY_TYPES = ImmutableSet.of(CitizenMemoryAndSensors.VISIBLE_CITIZENS.get(),
					MemoryModuleType.BREED_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
					MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY,
					MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LOOK_TARGET,
					MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_HOSTILE,
					MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.PATH, MemoryModuleType.NEAREST_BED,
					MemoryModuleType.VISIBLE_MOBS);
		if (SENSOR_TYPES == null)
			SENSOR_TYPES = ImmutableSet.of(CitizenMemoryAndSensors.NEAREST_CITIZENS.get(),
					SensorType.INTERACTABLE_DOORS, SensorType.NEAREST_BED, SensorType.NEAREST_BED,
					SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY);
	}

	private CitizenInformation<CitizenEntity> info;

	private EmpireData empdata;
	private OccurrenceData occurrences;
	public static final IAttribute MAX_FOOD_LEVEL = (new RangedAttribute((IAttribute) null,
			OccultEntities.MODID + ".foodLevel", 20.0f, Float.MIN_VALUE, 1024.0D)).setDescription("Max Food Level")
					.setShouldWatch(true);

	public static final DataParameter<Float> FOOD_LEVEL = EntityDataManager.createKey(CitizenEntity.class,
			DataSerializers.FLOAT);

	public static DataParameter<Genetics<CitizenEntity>> GENETICS = null;

	public static DataParameter<SapientIdentity> SAPIENT_IDENTITY = null;

	private double previousFoodPosX;
	private double previousFoodPosY;
	private double previousFoodPosZ;
	private final GeneralInventory inventory = new GeneralInventory(30);

	public CitizenEntity(EntityType<? extends AgeableEntity> type, World worldIn) {
		super(type, worldIn);
		regularInit(worldIn);
	}

	public void regularInit(World worldIn) {
		if (worldIn instanceof ServerWorld) {
			this.setEmpdata(EmpireData.get((ServerWorld) worldIn));
			this.occurrences = OccurrenceData.get((ServerWorld) world);
			this.info = new CitizenInformation<CitizenEntity>(this);
			info.onCreation();
			if (this.firstUpdate) {
				this.info.onCreation();
			}
			this.dataManager.set(GENETICS, this.getGenetics());
			this.dataManager.set(SAPIENT_IDENTITY, this.getTrueIdentity());
		}

	}

	public CitizenEntity(World world) {
		this(EntityInit.CITIZEN.get(), world);
	}

	@Override
	public boolean attemptTeleport(double p_213373_1_, double p_213373_3_, double p_213373_5_, boolean p_213373_7_) {
		this.previousFoodPosX = getPosX();
		this.previousFoodPosY = getPosY();
		this.previousFoodPosZ = getPosZ();
		return super.attemptTeleport(p_213373_1_, p_213373_3_, p_213373_5_, p_213373_7_);
	}

	@Override
	public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			ILivingEntityData spawnDataIn, CompoundNBT dataTag) {

		this.previousFoodPosX = getPosX();
		this.previousFoodPosY = getPosY();
		this.previousFoodPosZ = getPosZ();

		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	public CitizenEntity(World world, Vec3d pos) {
		this(world);
		this.setPosition(pos.x, pos.y, pos.z);

	}

	public EmpireData getEmpireData() {
		return getEmpdata();
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttributes().registerAttribute(MAX_FOOD_LEVEL).setBaseValue(20.0);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
		this.dataManager.set(FOOD_LEVEL, (float) this.getAttribute(MAX_FOOD_LEVEL).getValue());
		this.dataManager.set(GENETICS, new Genetics<>(CitizenEntity.class));

		this.dataManager.set(SAPIENT_IDENTITY,
				this.isServerWorld()
						? new DynamicSapientIdentity(new BodyForm(this), this.getUniqueID(), (ServerWorld) this.world)
						: new SapientIdentity(new BodyForm(this), this.getUniqueID()));
	}

	@Override
	protected void registerData() {
		if (GENETICS == null)
			GENETICS = EntityDataManager.createKey(CitizenEntity.class,
					(IDataSerializer<Genetics<CitizenEntity>>) DataInit.CITIZEN_GENETICS_SERIALIZER.get()
							.getSerializer());

		if (SAPIENT_IDENTITY == null)
			SAPIENT_IDENTITY = (DataParameter<SapientIdentity>) EntityDataManager.createKey(CitizenEntity.class,
					DataInit.SAPIENT_IDENTITY_SERIALIZER.get().getSerializer());
		super.registerData();
		this.dataManager.register(FOOD_LEVEL, 1.0f);
		this.dataManager.register(GENETICS, new Genetics<>(CitizenEntity.class));
		this.dataManager.register(SAPIENT_IDENTITY,
				this.isServerWorld()
						? new DynamicSapientIdentity(new BodyForm(this), this.entityUniqueID, (ServerWorld) this.world)
						: new SapientIdentity(new BodyForm(this), this.getUniqueID()));
	}

	public void setPersonality(Personality personality) {
		this.info.setPersonality(personality);
	}

	public void setKnowledge(Memories<CitizenEntity> gossipKnowledge) {
		this.info.setKnowledge(gossipKnowledge);
	}

	public void setRelationships(Relationships relationships) {
		this.info.setRelationships(relationships);
	}

	public void setGenetics(Genetics<CitizenEntity> genetics) {
		if (this.isServerWorld())
			this.info.setGenetics(genetics);
		else
			this.dataManager.set(GENETICS, genetics);
	}

	public void setAutonomy(Autonomy<CitizenEntity> aut) {
		this.info.setAutonomy(aut);
	}

	public Autonomy<CitizenEntity> getAutonomy() {
		return this.info.getAutonomy();
	}

	public Skills getSkills() {
		return this.info.getSkills();
	}

	public void setSkills(Skills skills) {
		this.info.setSkills(skills);
	}

	public GeneralInventory getInventory() {
		return this.inventory;
	}

	public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
		if (super.replaceItemInInventory(inventorySlot, itemStackIn)) {
			return true;
		} else {
			int i = inventorySlot - 300;
			if (i >= 0 && i < this.inventory.getSizeInventory()) {
				this.inventory.setInventorySlotContents(i, itemStackIn);
				return true;
			} else {
				return false;
			}
		}
	}

	public Set<ItemStack> getFoodFromInventory() {
		Set<ItemStack> stacks = Sets.newHashSet();
		for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem().isFood()) {
				stacks.add(stack);
			}
		}
		return stacks;
	}

	public CitizenEntity constructChild(LivingEntity other1) {

		CitizenEntity child = new CitizenEntity(EntityInit.CITIZEN.get(), world);
		if (other1 instanceof CitizenEntity) {
			CitizenEntity other = (CitizenEntity) other1;

			Set<SapientIdentity> sibs = this.getIdentity().getGenealogy().getChildren();
			sibs.addAll(other.getIdentity().getGenealogy().getChildren());

			child.getTrueIdentity().setGenealogy(new Genealogy(child.getTrueIdentity(), this.copyIdentity(),
					other.copyIdentity(), Sets.newHashSet(), sibs));
			child.setGenetics(this.getGenetics().getChild(other.getGenetics(), child));
		}
		return child;
	}

	@Override
	public AgeableEntity createChild(AgeableEntity ageable) {

		if (!(ageable instanceof CitizenEntity))
			return null;

		return constructChild((LivingEntity) ageable);
	}

	@Override
	public Brain<CitizenEntity> getBrain() {
		return (Brain<CitizenEntity>) super.getBrain();
	}

	@Override
	protected Brain<?> createBrain(Dynamic<?> dynamicIn) {
		Brain<CitizenEntity> brain = new Brain<>(MEMORY_TYPES, SENSOR_TYPES, dynamicIn);
		return brain;
	}

	@Override
	public void tick() {
		super.tick();
		if (!world.isRemote) {

			this.dataManager.set(SAPIENT_IDENTITY, this.getTrueIdentity().copy());
			if (ticksExisted % 80 == 0) {
				this.dataManager.set(GENETICS, this.getGenetics());
				if (this.getDistanceSq(previousFoodPosX, previousFoodPosY, previousFoodPosZ) >= 16) {
					this.exhaust(0.005);
				}
				this.previousFoodPosX = getPosX();
				this.previousFoodPosY = getPosY();
				this.previousFoodPosZ = getPosZ();
				if (this.isSwimming()) {
					this.exhaust(0.03);
				}

				if (this.isBurning()) {
					this.exhaust(0.2);
				}

				if (this.getPose() == Pose.SLEEPING) {
					this.exhaust(-0.01);
				}
			}
			if (this.getFoodLevel() <= 0) {
				this.damageEntity(DamageSource.STARVE, 0.05f);
			}
		}
	}

	public boolean canSeeWithEyes(LivingEntity other) {
		Vec3d vec3d = this.getLook(1.0F).normalize();
		Vec3d vec3d1 = new Vec3d(other.getPosX() - this.getPosX(), other.getPosY() - this.getPosYEye(),
				other.getPosZ() - this.getPosZ());
		double d0 = vec3d1.length();
		vec3d1 = vec3d1.normalize();
		double d1 = vec3d.dotProduct(vec3d1);
		return d1 > 1.0D - 0.025D / d0 ? canEntityBeSeen(other) : false;
	}

	public void decrementFoodLevel(double amount) {
		this.setFoodLevel(this.getFoodLevel() - (float) amount);
	}

	public void exhaust(double amount) {
		decrementFoodLevel(amount);
	}

	@Override
	public void livingTick() {
		super.livingTick();

	}

	@Override
	public void jump() {
		if (isSprinting()) {
			this.exhaust(0.05);
		} else {
			this.exhaust(0.01);
		}
		super.jump();
	}

	public float getFoodLevel() {
		return this.dataManager.get(FOOD_LEVEL); // TODO
	}

	public float getMaxFoodLevel() {
		return (float) this.getAttribute(MAX_FOOD_LEVEL).getValue();
	}

	public void setFoodLevel(float level) {
		this.dataManager.set(FOOD_LEVEL, MathHelper.clamp(level, 0.0F, this.getMaxFoodLevel()));
	}

	@Override
	public ItemStack onFoodEaten(World p_213357_1_, ItemStack stack) {
		this.consume(stack.getItem(), stack);
		p_213357_1_.playSound((PlayerEntity) null, this.getPosX(), this.getPosY(), this.getPosZ(),
				SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F,
				p_213357_1_.rand.nextFloat() * 0.1F + 0.9F);

		return super.onFoodEaten(p_213357_1_, stack);
	}

	public void consume(Item maybeFood, ItemStack stack) {
		if (maybeFood.isFood()) {
			Food food = maybeFood.getFood();
			this.setFoodLevel(this.getFoodLevel() + food.getHealing());
		}
	}

	public Personality<CitizenEntity> getPersonality() {
		return info.getPersonality();
	}

	public Memories<CitizenEntity> getKnowledge() {
		return info.getKnowledge();
	}

	public Relationships getRelationships() {
		return info.getRelationships();
	}

	public Genetics<CitizenEntity> getGenetics() {
		if (this.world.isRemote)
			return this.dataManager.get(GENETICS);
		return info.getGenetics();
	}

	public CitizenInformation<CitizenEntity> getInformation() {
		return info;
	}

	public CitizenInformation<CitizenEntity> getInfo() {
		return info;
	}

	public Needs<CitizenEntity> getNeeds() {
		return info.getNeeds();
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.put("Inventory", this.inventory.serializeNBT());
		if (this.isServerWorld()) {
			compound.put("CitizenInformation", this.info.serialize(NBTDynamicOps.INSTANCE));
		}
	}

	public DynamicSapientIdentity getTrueIdentity() {
		return info.getTrueIdentity();
	}

	public SapientIdentity getIdentity() {
		return this.dataManager.get(SAPIENT_IDENTITY);
	}

	public SapientIdentity copyIdentity() {
		return getIdentity().withCitizen(this.getForm());
	}

	public Emotions getEmotions() {
		return info.getEmotions();
	}

	public BodyForm getForm() {
		return Formshift.get(this).getForm();
	}

	public BodyForm getTrueForm() {
		return Formshift.get(this).getTrueForm();
	}

	public void setIdentity(DynamicSapientIdentity identity) {
		this.info.setIdentity(identity);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		if (this.isServerWorld()) {
			this.info = new CitizenInformation<CitizenEntity>(this,
					GMNBT.makeDynamic(compound.get("CitizenInformation")));
			info.initialize();
		}
		this.inventory.deserializeNBT(compound.getCompound("Inventory"));
	}

	@Override
	public void updateAITasks() {
		super.updateAITasks();
		this.world.getProfiler().startSection("brain");
		this.getBrain().tick((ServerWorld) this.world, this);
		this.world.getProfiler().endSection();
		this.info.update((ServerWorld) world);
		if (getLastDamageSource() != null) {

		}
	}

	@Override
	protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
		ItemStack itemstack = itemEntity.getItem();
		Item item = itemstack.getItem();
		boolean flag = false;

		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			ItemStack itemstack1 = inventory.getStackInSlot(i);
			if (itemstack1.isEmpty()
					|| itemstack1.getItem() == item && itemstack1.getCount() < itemstack1.getMaxStackSize()) {
				flag = true;
				break;
			}
		}

		if (!flag) {
			return;
		}

		int j = inventory.count(item);
		if (j == 256) {
			return;
		}

		if (j > 256) {
			inventory.func_223374_a(item, j - 256);
			return;
		}

		this.onItemPickup(itemEntity, itemstack.getCount());
		ItemStack itemstack2 = inventory.addItem(itemstack);
		if (itemstack2.isEmpty()) {
			itemEntity.remove();
		} else {
			itemstack.setCount(itemstack2.getCount());
		}

	}

	public boolean shouldHeal() {
		return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {

		if (capability.equals(GMCaps.SAPIENT_INFO)) {
			return info == null ? LazyOptional.empty() : LazyOptional.of(() -> this.info).cast();
		}
		return super.getCapability(capability, facing);
	}

	public EmpireData getEmpdata() {
		return empdata;
	}

	public void setEmpdata(EmpireData empdata) {
		this.empdata = empdata;
	}

	public enum HappinessStatus {
		VERY_HAPPY, HAPPY, FINE, SAD, VERY_SAD;
	}

}
