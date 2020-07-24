package com.gm910.occentmod.capabilities.wizardcap;

import com.gm910.occentmod.capabilities.IModCapability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IWizard extends INBTSerializable<CompoundNBT>, IModCapability<LivingEntity> {
	
	public static final int DEFAULT_MAXIMUM_ENERGY = 5000;

	public boolean isWizard();
	
	
	public void setIsWizard(boolean isWizard);
	
	/**
	 * Returns the amount of energy in the wizard (measured in merlynes, meaning about 5000 merlynes maximum)
	 */
	public int getEnergy();
	
	/**
	 * Sets energy amount in wizard
	 * @param amount value in merlynes
	 */
	public void setEnergy(int amount);
	
	/**
	 * Changes energy amount in wizard
	 * @param byAmount value in merlynes
	 */
	public void changeEnergy(int byAmount);
	
	/**
	 * Kills wizard if energy is too low
	 */
	public void checkEnergy();
	
	//public boolean canCastSpell(Spell sp);
	
	//public void castSpell(Spell sp);
	
	
	/**
	 * Returns the maximum energy the wizard can hold, default 5000
	 * @return
	 */
	public int getMaxEnergy();
	
	/**
	 * Sets max energy wizard can hold, default 5000
	 */
	public void setMaxEnergy(int energy);

	
}
