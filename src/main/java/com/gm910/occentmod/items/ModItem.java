package com.gm910.occentmod.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModItem extends Item {

	protected boolean hasEternalGlint;

	public ModItem(Item.Properties prop) {
		super(prop);
	}

	/**
	 * Sets whether the item is always glinting as if enchanted
	 * 
	 * @param hasEternalEffect
	 * @return
	 */
	public ModItem setHasEternalGlint(boolean hasEternalEffect) {
		this.hasEternalGlint = hasEternalEffect;
		return this;
	}

	public boolean hasEternalGlint() {
		return hasEternalGlint;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {

		return super.hasEffect(stack) || hasEternalGlint;
	}

}
