package com.gm910.occentmod.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.INBTSerializable;

public class GeneralInventory implements IInventory, IDynamicSerializable, INBTSerializable<CompoundNBT> {

	private final int slotsCount;
	private NonNullList<ItemStack> inventoryContents;
	private List<IInventoryChangedListener> listeners;

	public GeneralInventory(int size) {
		inventoryContents = NonNullList.withSize(size, ItemStack.EMPTY);
		slotsCount = size;
	}

	public GeneralInventory(ItemStack... items) {
		inventoryContents = NonNullList.from(ItemStack.EMPTY, items);
		slotsCount = items.length;
	}

	public NonNullList<ItemStack> getList() {
		return inventoryContents;
	}

	protected void setSize(int size) {
		List<ItemStack> prevContents = new ArrayList<>(inventoryContents);
		NonNullList<ItemStack> new_ = NonNullList.withSize(size, ItemStack.EMPTY);
		for (int i = 0; i < prevContents.size(); i++) {
			if (i >= new_.size()) {
				break;
			}
			new_.set(i, prevContents.get(i));
		}
		this.inventoryContents = new_;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventoryContents, index, count);
		if (!itemstack.isEmpty()) {
			this.markDirty();
		}

		return itemstack;
	}

	/**
	 * Add a listener that will be notified when any item in this inventory is
	 * modified.
	 */
	public void addListener(IInventoryChangedListener listener) {
		if (this.listeners == null) {
			this.listeners = Lists.newArrayList();
		}

		this.listeners.add(listener);
	}

	/**
	 * removes the specified IInvBasic from receiving further change notices
	 */
	public void removeListener(IInventoryChangedListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Returns the stack in the given slot.
	 */
	public ItemStack getStackInSlot(int index) {
		return index >= 0 && index < this.inventoryContents.size() ? this.inventoryContents.get(index)
				: ItemStack.EMPTY;
	}

	public ItemStack func_223374_a(Item p_223374_1_, int p_223374_2_) {
		ItemStack itemstack = new ItemStack(p_223374_1_, 0);

		for (int i = this.slotsCount - 1; i >= 0; --i) {
			ItemStack itemstack1 = this.getStackInSlot(i);
			if (itemstack1.getItem().equals(p_223374_1_)) {
				int j = p_223374_2_ - itemstack.getCount();
				ItemStack itemstack2 = itemstack1.split(j);
				itemstack.grow(itemstack2.getCount());
				if (itemstack.getCount() == p_223374_2_) {
					break;
				}
			}
		}

		if (!itemstack.isEmpty()) {
			this.markDirty();
		}

		return itemstack;
	}

	public ItemStack addItem(ItemStack stack) {
		ItemStack itemstack = stack.copy();
		this.func_223372_c(itemstack);
		if (itemstack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.func_223375_b(itemstack);
			return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
		}
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemstack = this.inventoryContents.get(index);
		if (itemstack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.inventoryContents.set(index, ItemStack.EMPTY);
			return itemstack;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.inventoryContents.set(index, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}

		this.markDirty();
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		return this.slotsCount;
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventoryContents) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to
	 * disk later - the game won't think it hasn't changed and skip it.
	 */
	public void markDirty() {
		if (this.listeners != null) {
			for (IInventoryChangedListener iinventorychangedlistener : this.listeners) {
				iinventorychangedlistener.onInventoryChanged(this);
			}
		}

	}

	/**
	 * Don't rename this method to canInteractWith due to conflicts with Container
	 */
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	public void clear() {
		this.inventoryContents.clear();
		this.markDirty();
	}

	public void fillStackedContents(RecipeItemHelper helper) {
		for (ItemStack itemstack : this.inventoryContents) {
			helper.accountStack(itemstack);
		}

	}

	public String toString() {
		return this.inventoryContents.stream().filter((p_223371_0_) -> {
			return !p_223371_0_.isEmpty();
		}).collect(Collectors.toList()).toString();
	}

	private void func_223375_b(ItemStack p_223375_1_) {
		for (int i = 0; i < this.slotsCount; ++i) {
			ItemStack itemstack = this.getStackInSlot(i);
			if (itemstack.isEmpty()) {
				this.setInventorySlotContents(i, p_223375_1_.copy());
				p_223375_1_.setCount(0);
				return;
			}
		}

	}

	private void func_223372_c(ItemStack p_223372_1_) {
		for (int i = 0; i < this.slotsCount; ++i) {
			ItemStack itemstack = this.getStackInSlot(i);
			if (ItemStack.areItemsEqual(itemstack, p_223372_1_)) {
				this.func_223373_a(p_223372_1_, itemstack);
				if (p_223372_1_.isEmpty()) {
					return;
				}
			}
		}

	}

	private void func_223373_a(ItemStack p_223373_1_, ItemStack p_223373_2_) {
		int i = Math.min(this.getInventoryStackLimit(), p_223373_2_.getMaxStackSize());
		int j = Math.min(p_223373_1_.getCount(), i - p_223373_2_.getCount());
		if (j > 0) {
			p_223373_2_.grow(j);
			p_223373_1_.shrink(j);
			this.markDirty();
		}

	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {

		return ops.createMap(ImmutableMap.of(ops.createString("contents"), ops.createList(this.inventoryContents
				.stream().map((e) -> Dynamic.convert(NBTDynamicOps.INSTANCE, ops, e.write(new CompoundNBT()))))));
	}

	public <T> GeneralInventory deserialize(Dynamic<T> dyn) {
		this.inventoryContents = NonNullList.from(ItemStack.EMPTY,
				dyn.get("contents")
						.asList((e) -> ItemStack.read(
								(CompoundNBT) Dynamic.convert(dyn.getOps(), NBTDynamicOps.INSTANCE, e.getValue())))
						.toArray(new ItemStack[0]));

		return this;
	}

	@Override
	public CompoundNBT serializeNBT() {
		return (CompoundNBT) this.serialize(NBTDynamicOps.INSTANCE);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.deserialize(GMNBT.makeDynamic(nbt));
	}

}
