package com.gm910.occentmod.entities.wizard;

import com.gm910.occentmod.init.DataInit;

import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraftforge.fml.RegistryObject;

@Deprecated
public class WizardActivities {

	public static final RegistryObject<Activity> BATTLE = DataInit.registerActivity(WizardEntity.PREFIX + "_battle");

	public static void forceClinit() {
	}
}
