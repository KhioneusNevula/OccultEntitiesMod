package com.gm910.occentmod.world.mindrealm;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class ModItemStackHandler extends ItemStackHandler {

	public ModItemStackHandler() {
	}

	public ModItemStackHandler(int size) {
		super(size);
	}

	public ModItemStackHandler(NonNullList<ItemStack> stacks) {
		super(stacks);
	}

	public NonNullList<ItemStack> getStacksRaw() {
		return this.stacks;
	}

}
