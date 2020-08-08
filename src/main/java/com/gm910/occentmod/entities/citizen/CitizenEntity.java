package com.gm910.occentmod.entities.citizen;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.entities.citizen.mind_and_traits.CitizenInformation;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race;
import com.gm910.occentmod.entities.citizen.mind_and_traits.gossip.GossipHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Needs;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality.NumericPersonalityTrait;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality.ReactionType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.ReactionDeterminer;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity.DynamicCitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Genealogy;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.Autonomy;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenAction;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.ImmediateTask;
import com.gm910.occentmod.init.EntityInit;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CitizenEntity extends AgeableEntity implements INPC {

	public static Set<MemoryModuleType<?>> MEMORY_TYPES;

	public static Set<SensorType<? extends Sensor<? super CitizenEntity>>> SENSOR_TYPES;

	{
		if (MEMORY_TYPES == null)
			MEMORY_TYPES = ImmutableSet.of();
		if (SENSOR_TYPES == null)
			SENSOR_TYPES = ImmutableSet.of();
	}

	private CitizenInformation<CitizenEntity> info;
	private EmpireData empdata;
	public static final IAttribute MAX_FOOD_LEVEL = (new RangedAttribute((IAttribute) null,
			OccultEntities.MODID + ".foodLevel", 20.0f, Float.MIN_VALUE, 1024.0D)).setDescription("Max Food Level")
					.setShouldWatch(true); // Forge: set smallest max-health value to fix MC-119183. This gets rounded
											// to float so we use the smallest positive float value.

	public static final DataParameter<Float> FOOD_LEVEL = EntityDataManager.createKey(CitizenEntity.class,
			DataSerializers.FLOAT);

	private double previousFoodPosX;
	private double previousFoodPosY;
	private double previousFoodPosZ;
	private final Inventory inventory = new Inventory(30);

	public CitizenEntity(EntityType<? extends AgeableEntity> type, World worldIn) {
		super(type, worldIn);
		this.info = new CitizenInformation<CitizenEntity>(this);
		info.initialize();
		if (worldIn instanceof ServerWorld) {
			this.empdata = EmpireData.get((ServerWorld) worldIn);
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

		if (this.world instanceof ServerWorld) {
			ServerWorld world = (ServerWorld) this.world;
			Set<Empire> emps = empdata.getInRadius(world.dimension.getType(), this.getPosition(), 20);
			Optional<Empire> empo = emps.stream().findAny();
			Race tryRace = Race.getRaces().stream().findAny().get();
			if (empo.isPresent()) {
				tryRace = empo.get().chooseRandomRace(this.rand);
				this.getTrueIdentity().setEmpire(empo.get());
			}
			this.getGenetics().initGenes(tryRace, this);
			this.info.initValues(world);

		}

		this.previousFoodPosX = getPosX();
		this.previousFoodPosY = getPosY();
		this.previousFoodPosZ = getPosZ();

		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	public CitizenEntity(World world, Vec3d pos) {
		this(world);
		this.setPosition(pos.x, pos.y, pos.z);
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttributes().registerAttribute(MAX_FOOD_LEVEL).setBaseValue(20.0);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
		this.dataManager.set(FOOD_LEVEL, (float) this.getAttribute(MAX_FOOD_LEVEL).getValue());
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(FOOD_LEVEL, 1.0f);
	}

	public void setPersonality(Personality personality) {
		this.info.setPersonality(personality);
	}

	public void setGossipKnowledge(GossipHolder gossipKnowledge) {
		this.info.setGossipKnowledge(gossipKnowledge);
	}

	public void setRelationships(Relationships relationships) {
		this.info.setRelationships(relationships);
	}

	public void setGenetics(Genetics<CitizenEntity> genetics) {
		this.info.setGenetics(genetics);
	}

	public void setAutonomy(Autonomy aut) {
		this.info.setAutonomy(aut);
	}

	public Autonomy getAutonomy() {
		return this.info.getAutonomy();
	}

	public Inventory getInventory() {
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

	@Override
	public AgeableEntity createChild(AgeableEntity ageable) {

		if (!(ageable instanceof CitizenEntity))
			return null;
		CitizenEntity other = (CitizenEntity) ageable;

		CitizenEntity child = new CitizenEntity(EntityInit.CITIZEN.get(), world);
		child.setGenetics(this.getGenetics().getChild(other.getGenetics(), child));
		child.getTrueIdentity().setGenealogy(new Genealogy(child.getTrueIdentity(), this.copyIdentity(),
				other.copyIdentity(), Sets.newHashSet(), this.getIdentity().getGenealogy().getChildren()));

		return child;
	}

	@Override
	public Brain<CitizenEntity> getBrain() {
		return (Brain<CitizenEntity>) super.getBrain();
	}

	@Override
	protected Brain<?> createBrain(Dynamic<?> dynamicIn) {
		if (MEMORY_TYPES == null)
			MEMORY_TYPES = ImmutableSet.of();
		if (SENSOR_TYPES == null)
			SENSOR_TYPES = ImmutableSet.of();
		Brain<CitizenEntity> brain = new Brain<>(MEMORY_TYPES, SENSOR_TYPES, dynamicIn);
		return brain;
	}

	public void react(CitizenAction action) {
		Map<NumericPersonalityTrait, ReactionDeterminer<ImmediateTask>> mapa = action.getPotentialReactions();
		Object2IntMap<ImmediateTask> rxns = Object2IntMaps.emptyMap();
		Personality per = this.getPersonality();
		for (NumericPersonalityTrait trait : mapa.keySet()) {
			ReactionDeterminer<ImmediateTask> determiner = mapa.get(trait);
			float f = per.getTrait(trait);
			ReactionType type = trait.getWeightedRandomReaction(f);
			ImmediateTask react = determiner.get(type);
			if (react != null) {
				rxns.put(react, rxns.getInt(react) + 1);
			}
		}
		Set<ImmediateTask> reactions = new HashSet<>();
		for (ImmediateTask rxn : rxns.keySet()) {
			if (rxns.getInt(rxn) == mapa.size()) {
				reactions.add(rxn);
			}
		}

		/// TODO
	}

	@Override
	public void tick() {
		super.tick();
		if (!world.isRemote) {
			if (ticksExisted % 80 == 0) {
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

	public Personality getPersonality() {
		return info.getPersonality();
	}

	public GossipHolder getGossipKnowledge() {
		return info.getGossipKnowledge();
	}

	public Relationships getRelationships() {
		return info.getRelationships();
	}

	public Genetics<CitizenEntity> getGenetics() {
		return info.getGenetics();
	}

	public CitizenInformation<CitizenEntity> getInformation() {
		return info;
	}

	public CitizenInformation<CitizenEntity> getInfo() {
		return info;
	}

	public Needs getNeeds() {
		return info.getNeeds();
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.put("CitizenInformation", this.info.serialize(NBTDynamicOps.INSTANCE));
	}

	public DynamicCitizenIdentity getTrueIdentity() {
		return info.getTrueIdentity();
	}

	public CitizenIdentity getIdentity() {
		return info.getIdentity();
	}

	public CitizenIdentity copyIdentity() {
		return getIdentity();
	}

	public void setIdentity(DynamicCitizenIdentity identity) {
		this.info.setIdentity(identity);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.info = new CitizenInformation<CitizenEntity>(this, GMNBT.makeDynamic(compound.get("CitizenInformation")));
	}

	@Override
	public void updateAITasks() {
		super.updateAITasks();
		this.world.getProfiler().startSection("brain");
		this.getBrain().tick((ServerWorld) this.world, this);
		this.world.getProfiler().endSection();
		this.info.update((ServerWorld) world);

	}

	public static UUID getModifiedUniqueIdentity(LivingEntity entity) {
		return entity.getUniqueID();
	}

	public boolean shouldHeal() {
		return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
	}

}
