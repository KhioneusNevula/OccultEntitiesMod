package com.gm910.occentmod.entities.citizen.mind_and_traits.task.needs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.gm910.occentmod.api.networking.messages.Networking;
import com.gm910.occentmod.api.networking.messages.types.TaskParticles;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality.NumericPersonalityTrait;
import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.ReactionDeterminer;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.ImmediateTask;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;

public class EatFoodFromInventory extends ImmediateTask implements INeedsTask<NeedType<Float>> {

	private Inventory inventory;

	public EatFoodFromInventory() {
		super(ImmutableMap.of());
		addContext(Context.CORE);
	}

	@Override
	public void startExecuting(ServerWorld world, CitizenEntity entity, long gameTime) {
		inventory = entity.getInventory();
	}

	@Override
	public void updateTask(ServerWorld worldIn, CitizenEntity owner, long gameTime) {
		Set<ItemStack> foods = getFoodFromInventory();
		Optional<ItemStack> foodOp = foods.stream().findAny();
		if (!foodOp.isPresent()) {
			// TODO
		} else {
			owner.onFoodEaten(worldIn, foodOp.get());
			int randIters = owner.getRNG().nextInt(20);
			for (int i = 0; i < randIters; i++) {
				Networking.sendToTracking(new TaskParticles(new ItemParticleData(ParticleTypes.ITEM, foodOp.get()),
						owner.getPosX() + owner.getRNG().nextDouble() * 2 - 1,
						owner.getPosY() + owner.getEyeHeight() + owner.getRNG().nextDouble() * 2 - 1,
						owner.getPosZ() + owner.getRNG().nextDouble() * 2 - 1, owner.dimension,
						owner.getRNG().nextDouble() * 2 - 1, owner.getRNG().nextDouble() * 2 - 1,
						owner.getRNG().nextDouble() * 2 - 1, true, false, false), owner);
			}
		}

	}

	@Override
	public boolean shouldContinueExecuting(ServerWorld worldIn, CitizenEntity entityIn, long gameTimeIn) {
		return INeedsTask.super.shouldContinueExecuting(worldIn, entityIn, gameTimeIn)
				&& !getFoodFromInventory().isEmpty();
	}

	@Override
	public Map<NumericPersonalityTrait, ReactionDeterminer<ImmediateTask>> getPotentialWitnessReactions() {
		return new HashMap<>();
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
	public NeedType<Float> getNeedType() {
		// TODO Auto-generated method stub
		return NeedType.HUNGER;
	}

	@Override
	public boolean isUrgent(CitizenEntity en) {
		// TODO Auto-generated method stub
		return en.getFoodLevel() < en.getMaxFoodLevel() / 6;
	}

}
