package com.gm910.occentmod.capabilities.wizardcap;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.damage.MagicDamage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class Wizard implements IWizard {
	
	public static final ResourceLocation LOC = new ResourceLocation(OccultEntities.MODID, "wizardcap");

	private LivingEntity wizard;
	private boolean isWizard = false;
	
	private int maxEnergy = DEFAULT_MAXIMUM_ENERGY;
	private int energy = maxEnergy;
	
	@Override
	public boolean isWizard() {
		return isWizard;
	}

	public void setIsWizard(boolean isWizard) {
		this.isWizard = isWizard;
	}

	@Override
	public int getEnergy() {
		// TODO Auto-generated method stub
		return energy;
	}

	@Override
	public void setEnergy(int amount) {
		energy = amount;
	}

	@Override
	public void changeEnergy(int byAmount) {
		energy += byAmount;
	}

	@Override
	public void checkEnergy() {
		if (this.wizard != null) {
			if (energy <= 0) {
				wizard.attackEntityFrom(MagicDamage.causeSpellDamage(wizard), wizard.getMaxHealth());
			} else if (energy <= 50) {
				wizard.attackEntityFrom(MagicDamage.causeSpellDamage(wizard), 1);
			}
		}
	}

	@Override
	public void $setOwner(LivingEntity wiz) {
		this.wizard = wiz;
	}

	@Override
	public LivingEntity $getOwner() {
		return wizard;
	}

	@Override
	public int getMaxEnergy() {
		// TODO Auto-generated method stub
		return maxEnergy;
	}

	@Override
	public void setMaxEnergy(int energy) {
		maxEnergy = energy;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT comp = new CompoundNBT();
		comp.putInt("Energy", energy);
		comp.putBoolean("Is", isWizard);
		comp.putInt("MaxEnergy", maxEnergy);
		return comp;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		energy = nbt.getInt("Energy");
		isWizard = nbt.getBoolean("Is");
		maxEnergy = nbt.getInt("MaxEnergy");
	}


}
