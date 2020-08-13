package com.gm910.occentmod.capabilities.mental_inventory;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.capabilities.CapabilityProvider;
import com.gm910.occentmod.world.mindrealm.ModItemStackHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class MindInventory implements INBTSerializable<CompoundNBT>, IInventoryChangedListener {

	public static final ResourceLocation LOC = new ResourceLocation(OccultEntities.MODID, "mind_inventory");

	private final ModItemStackHandler mindInventory = new ModItemStackHandler(36);
	private final ModItemStackHandler mindHands = new ModItemStackHandler(2);
	private final ModItemStackHandler mindArmor = new ModItemStackHandler(4);

	private final ModItemStackHandler physicalInventory = new ModItemStackHandler(36);
	private final ModItemStackHandler physicalHands = new ModItemStackHandler(2);
	private final ModItemStackHandler physicalArmor = new ModItemStackHandler(4);

	private boolean isInMind = false;

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT comp = new CompoundNBT();
		comp.putBoolean("Mind", isInMind);
		comp.put("mindInv", mindInventory.serializeNBT());
		comp.put("mindHands", mindHands.serializeNBT());
		comp.put("mindArmor", mindArmor.serializeNBT());
		comp.put("physInv", physicalInventory.serializeNBT());
		comp.put("physHands", physicalHands.serializeNBT());
		comp.put("physArmor", physicalArmor.serializeNBT());
		return comp;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.isInMind = nbt.getBoolean("Mind");
		this.mindInventory.deserializeNBT(nbt.getCompound("mindInv"));
		this.mindHands.deserializeNBT(nbt.getCompound("mindHands"));
		this.mindArmor.deserializeNBT(nbt.getCompound("mindArmor"));
		this.physicalInventory.deserializeNBT(nbt.getCompound("physInv"));
		this.physicalHands.deserializeNBT(nbt.getCompound("physHands"));
		this.physicalArmor.deserializeNBT(nbt.getCompound("physArmor"));
	}

	public void setInMind(boolean isInMind) {
		this.isInMind = isInMind;
	}

	public boolean isInMind() {
		return isInMind;
	}

	public ModItemStackHandler getMindArmor() {
		return mindArmor;
	}

	public ModItemStackHandler getMindHands() {
		return mindHands;
	}

	public ModItemStackHandler getMindInventory() {
		return mindInventory;
	}

	public ModItemStackHandler getPhysicalArmor() {
		return physicalArmor;
	}

	public ModItemStackHandler getPhysicalHands() {
		return physicalHands;
	}

	public ModItemStackHandler getPhysicalInventory() {
		return physicalInventory;
	}

	public void updateInventory(PlayerEntity player) {
		if (player.getCapability(CapabilityProvider.MIND_INVENTORY).orElse(null) != this) {
			return;
		}
		PlayerInventory inventory = player.inventory;
		ModItemStackHandler main = isInMind ? mindInventory : physicalInventory;
		ModItemStackHandler armor = isInMind ? mindArmor : physicalArmor;
		ModItemStackHandler hands = isInMind ? mindHands : physicalHands;

		for (int i = 0; i < main.getSlots(); i++) {
			main.setStackInSlot(i, inventory.mainInventory.get(i));
		}
		for (int i = 0; i < armor.getSlots(); i++) {
			armor.setStackInSlot(i, inventory.armorInventory.get(i));
		}
		// hands.setStackInSlot(0, player.getHeldItemMainhand());
		hands.setStackInSlot(0, player.getHeldItemOffhand());

	}

	public void transferInventoryToPlayer(PlayerEntity player) {
		PlayerInventory inventory = player.inventory;
		ModItemStackHandler main = isInMind ? mindInventory : physicalInventory;
		ModItemStackHandler armor = isInMind ? mindArmor : physicalArmor;
		ModItemStackHandler hands = isInMind ? mindHands : physicalHands;

		for (int i = 0; i < main.getSlots(); i++) {
			inventory.mainInventory.set(i, main.getStackInSlot(i));
		}
		for (int i = 0; i < armor.getSlots(); i++) {
			inventory.armorInventory.set(i, armor.getStackInSlot(i));
		}
		// player.setHeldItem(Hand.MAIN_HAND, hands.getStackInSlot(0));
		player.setHeldItem(Hand.OFF_HAND, hands.getStackInSlot(0));
	}

	/**
	 * Switches to other inventory and sets isInMind to the corresponding value;
	 */
	public void switchInventory(PlayerEntity player) {
		if (player.getCapability(CapabilityProvider.MIND_INVENTORY).orElse(null) != this) {
			return;
		}

		this.updateInventory(player);

		isInMind = !isInMind;

		this.transferInventoryToPlayer(player);

	}

	public static MindInventory get(PlayerEntity player) {
		return player.getCapability(CapabilityProvider.MIND_INVENTORY).orElse(null);
	}

	@Override
	public void onInventoryChanged(IInventory invBasic) {

	}

}
