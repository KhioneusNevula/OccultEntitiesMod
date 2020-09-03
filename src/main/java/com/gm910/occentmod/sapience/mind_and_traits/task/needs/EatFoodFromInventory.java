package com.gm910.occentmod.sapience.mind_and_traits.task.needs;

import java.util.Optional;
import java.util.Set;

import com.gm910.occentmod.api.networking.messages.Networking;
import com.gm910.occentmod.api.networking.messages.types.TaskParticles;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.sapience.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.Occurrence;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.task.ImmediateTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.Necessity;
import com.gm910.occentmod.sapience.mind_and_traits.task.TaskType;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;

public class EatFoodFromInventory extends ImmediateTask<CitizenEntity>
		implements INeedsTask<CitizenEntity, NeedType<CitizenEntity, Float>> {

	private Optional<ItemStack> foodOp;

	public EatFoodFromInventory() {
		super(CitizenEntity.class, ImmutableMap.of());
		addContext(Context.CORE);
		foodOp = Optional.empty();
	}

	public EatFoodFromInventory(Dynamic<?> dyn) {
		this();
		if (dyn.get("food").get().isPresent()) {
			try {
				foodOp = Optional.of(ItemStack.read(JsonToNBT.getTagFromJson(dyn.get("food").asString(""))));
			} catch (CommandSyntaxException e) {
				foodOp = Optional.empty();
			}
		} else {
			foodOp = Optional.empty();
		}
	}

	@Override
	public boolean shouldContinueExecuting(ServerWorld worldIn, CitizenEntity entityIn, long gameTimeIn) {
		return INeedsTask.super.shouldContinueExecuting(worldIn, entityIn, gameTimeIn);
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		if (foodOp.isPresent()) {
			return ops.createMap(ImmutableMap.of(ops.createString("food"),
					ops.createString(foodOp.get().serializeNBT().getString())));

		}
		return ops.emptyMap();

	}

	public Optional<ItemStack> getFoodOp() {
		return foodOp;
	}

	@Override
	public boolean shouldExecute(ServerWorld worldIn, CitizenEntity owner) {
		Set<ItemStack> foods = getFoodFromInventory(owner);
		foodOp = foods.stream().findAny();
		return foodOp.isPresent() && super.shouldExecute(worldIn, owner);
	}

	@Override
	public void startExecuting(ServerWorld worldIn, CitizenEntity owner, long gameTime) {

		ItemStack foodOp = this.foodOp.get().copy();
		owner.onFoodEaten(worldIn, foodOp);
		int randIters = owner.getRNG().nextInt(20);
		for (int i = 0; i < randIters; i++) {
			Networking.sendToTracking(new TaskParticles(new ItemParticleData(ParticleTypes.ITEM, foodOp),
					owner.getPosX() + owner.getRNG().nextDouble() * 2 - 1,
					owner.getPosY() + owner.getEyeHeight() + owner.getRNG().nextDouble() * 2 - 1,
					owner.getPosZ() + owner.getRNG().nextDouble() * 2 - 1, owner.dimension,
					owner.getRNG().nextDouble() * 2 - 1, owner.getRNG().nextDouble() * 2 - 1,
					owner.getRNG().nextDouble() * 2 - 1, true, false, false), owner);
		}
	}

	public Set<ItemStack> getFoodFromInventory(LivingEntity owner) {
		IInventory inventory = SapientInfo.get(owner).getInventory();
		Set<ItemStack> stacks = Sets.newHashSet();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem().isFood()) {
				stacks.add(stack);
			}
		}
		return stacks;
	}

	@Override
	public NeedType<CitizenEntity, Float> getNeedType() {
		return NeedType.HUNGER;
	}

	@Override
	public SapientDeed getDeed(SapientIdentity doer) {
		return new EatenFoodDeed(doer, foodOp);
	}

	@Override
	protected void resetTask(ServerWorld worldIn, CitizenEntity entityIn, long gameTimeIn) {
		super.resetTask(worldIn, entityIn, gameTimeIn);
		this.foodOp = Optional.empty();
	}

	@Override
	public Necessity getNecessity(CitizenEntity en) {
		return en.getFoodLevel() < en.getMaxFoodLevel() / 6 ? Necessity.NECESSARY : Necessity.PREFERABLE;
	}

	@Override
	public TaskType<CitizenEntity, EatFoodFromInventory> getType() {
		return TaskType.EAT_FOOD_FROM_INVENTORY;
	}

	public static class EatenFoodDeed extends SapientDeed {

		public static final OccurrenceType<EatenFoodDeed> DEED = new OccurrenceType<>(GMFiles.rl("eaten_food_deed"),
				(wuld) -> {
					return new EatenFoodDeed();
				});

		private Optional<ItemStack> food = Optional.empty();

		public EatenFoodDeed() {
			super(DEED);
		}

		public EatenFoodDeed(SapientIdentity citizen, Optional<ItemStack> food) {
			super(DEED, citizen);
			this.food = food;
		}

		@Override
		public void readAdditionalData(Dynamic<?> dyn) {
			food = dyn.get("food").get().flatMap((e) -> {
				try {
					return Optional.of(ItemStack.read(JsonToNBT.getTagFromJson(e.asString(""))));
				} catch (CommandSyntaxException e1) {
					return Optional.empty();
				}
			});
		}

		@Override
		public <T> T writeAdditionalData(DynamicOps<T> ops) {
			if (food.isPresent()) {
				return ops.createMap(ImmutableMap.of(ops.createString("food"),
						ops.createString(food.get().serializeNBT().getString())));
			}
			return ops.empty();
		}

		@Override
		public Object[] getDataForDisplay(LivingEntity en) {
			return new Object[] { food.orElse(ItemStack.EMPTY) };
		}

		@Override
		public OccurrenceEffect getEffect() {
			return new OccurrenceEffect(ImmutableMap.of(this.citizen, Connotation.HELPFUL));
		}

		@Override
		public boolean isSimilarTo(Occurrence other) {
			return other instanceof EatenFoodDeed && ((EatenFoodDeed) other).food.orElse(ItemStack.EMPTY)
					.getItem() == this.food.orElse(ItemStack.EMPTY).getItem();
		}

	}

}
