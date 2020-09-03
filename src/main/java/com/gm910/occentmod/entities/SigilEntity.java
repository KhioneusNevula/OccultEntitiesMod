package com.gm910.occentmod.entities;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;

public class SigilEntity extends LivingEntity {

	public SigilEntity(EntityType<? extends SigilEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {

		return Lists.newArrayList();
	}

	@Override
	public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
	}

	@Override
	public HandSide getPrimaryHand() {
		return HandSide.RIGHT;
	}

}
