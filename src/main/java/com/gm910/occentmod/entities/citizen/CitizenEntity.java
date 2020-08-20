package com.gm910.occentmod.entities.citizen;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.capabilities.formshifting.Formshift;
import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.empires.EmpireData;
import com.gm910.occentmod.entities.citizen.mind_and_traits.BodyForm;
import com.gm910.occentmod.entities.citizen.mind_and_traits.CitizenInformation;
import com.gm910.occentmod.entities.citizen.mind_and_traits.CitizenMemoryAndSensors;
import com.gm910.occentmod.entities.citizen.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.Race;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.CauseEffectTheory;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.CauseEffectTheory.Certainty;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryHolder;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryOfDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryOfOccurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.Needs;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.OccurrenceData;
import com.gm910.occentmod.entities.citizen.mind_and_traits.occurrence.deeds.CitizenDeed;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.PersonalityTrait.TraitLevel;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.CitizenIdentity.DynamicCitizenIdentity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Genealogy;
import com.gm910.occentmod.entities.citizen.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.entities.citizen.mind_and_traits.skills.Skills;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.Autonomy;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenAction;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask;
import com.gm910.occentmod.init.EntityInit;
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
			MEMORY_TYPES = ImmutableSet.of(CitizenMemoryAndSensors.VISIBLE_CITIZENS.get(), MemoryModuleType.WALK_TARGET,
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
			this.occurrences = OccurrenceData.get((ServerWorld) world);
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
			info.getTrueIdentity().setName(EmpireData.get((ServerWorld) world).giveRandomCitizenName());
			info.getTrueIdentity().setRace(this.getGenetics().getRace());
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

	public void setKnowledge(MemoryHolder gossipKnowledge) {
		this.info.setKnowledge(gossipKnowledge);
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

	public Skills getSkills() {
		return this.info.getSkills();
	}

	public void setSkills(Skills skills) {
		this.info.setSkills(skills);
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

	public CitizenEntity constructChild(LivingEntity other1) {

		CitizenEntity child = new CitizenEntity(EntityInit.CITIZEN.get(), world);
		if (other1 instanceof CitizenEntity) {
			CitizenEntity other = (CitizenEntity) other1;

			Set<CitizenIdentity> sibs = this.getIdentity().getGenealogy().getChildren();
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
		if (MEMORY_TYPES == null)
			MEMORY_TYPES = ImmutableSet.of();
		if (SENSOR_TYPES == null)
			SENSOR_TYPES = ImmutableSet.of();
		Brain<CitizenEntity> brain = new Brain<>(MEMORY_TYPES, SENSOR_TYPES, dynamicIn);
		return brain;
	}

	/**
	 * Reacts to an action done by the second parameter citizen entity; this action
	 * is assumed to be a CitizenTask
	 * 
	 * @param action
	 * @param doer
	 */
	public void react(CitizenAction action, CitizenEntity doer) {
		Set<CitizenTask> mapa = action.getPotentialReactions();
		CitizenDeed deed = ((CitizenTask) action).getDeed(doer.getIdentity());
		this.reaction(deed, mapa);
		this.getKnowledge().receiveKnowledge(new MemoryOfDeed(this, deed));

	}

	/**
	 * Performs a generic reaction by adding all tasks in the set to the execution
	 * list of the autonomy controller
	 * 
	 * @param event
	 */
	public void reaction(Occurrence occur, Set<CitizenTask> event) {
		Map<PersonalityTrait, TraitLevel> trets = this.getPersonality().generateTraitReactionMap();
		for (CitizenTask tasque : event) {
			if (tasque.canExecute(this)) {

				this.getAutonomy().considerTask(tasque.isUrgent(this) ? 0 : getAutonomy().getImmediateTasks().size(),
						tasque, tasque.isUrgent(this));
			}
		}
	}

	/**
	 * Reacts to a generic event occurring in the world
	 * 
	 * @param event
	 */
	public void reactToEvent(Occurrence event) {
		Set<CitizenTask> tasques = event.getPotentialWitnessReactions();
		this.reaction(event, tasques);
		MemoryOfOccurrence meme = new MemoryOfOccurrence(this, event);
		for (MemoryOfOccurrence occ : this.getKnowledge()
				.<MemoryOfOccurrence>getByPredicate((e) -> e instanceof MemoryOfOccurrence)) {

			if (occ.couldEventBeCauseOf(meme)) {
				CauseEffectTheory theo = new CauseEffectTheory(this, occ.getEvent(), event, null);
				Set<CauseEffectTheory> theors = this.getKnowledge().getByPredicate((e) -> e instanceof CauseEffectTheory
						&& ((CauseEffectTheory) e).fitsObservation(theo.getCause(), theo.getEffect()));
				if (!theors.isEmpty()) {
					for (CauseEffectTheory t : theors) {
						if (t.getCertainty() != Certainty.TRUE) {
							t.incrementObservation(1);
							t.getEffect().getEffect().getEffects().putAll(event.getEffect().getEffects());
						}
					}
				} else {
					this.getKnowledge().addKnowledge(theo);
				}
			}
		}
		this.getKnowledge().receiveKnowledge(meme);

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
			Optional<Collection<CitizenEntity>> vis = this.brain
					.getMemory(CitizenMemoryAndSensors.VISIBLE_CITIZENS.get());
			if (vis.isPresent() && !vis.get().isEmpty()) {

				for (CitizenEntity citizen : vis.get()) {
					Set<CitizenTask> tasks = citizen.getAutonomy().getRunningTasks().stream()
							.filter((e) -> e instanceof CitizenTask && ((CitizenTask) e).isVisible(citizen, this))
							.map((a) -> (CitizenTask) a).collect(Collectors.toSet());
					for (CitizenTask task : tasks) {
						CitizenDeed deed = task.getDeed(citizen.getIdentity());
						if (deed == null)
							continue;
						this.reactToEvent(deed);
					}
				}
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

	public MemoryHolder getKnowledge() {
		return info.getKnowledge();
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
		return info.getIdentity().withCitizen(this.getForm());
	}

	public CitizenIdentity copyIdentity() {
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
		if (getLastDamageSource() != null) {

		}
	}

	public static UUID getModifiedUniqueIdentity(LivingEntity entity) {
		return entity.getUniqueID();
	}

	public boolean shouldHeal() {
		return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
	}

	public enum Mood {
		VERY_HAPPY, HAPPY, FINE, SAD, VERY_SAD;
	}

}
