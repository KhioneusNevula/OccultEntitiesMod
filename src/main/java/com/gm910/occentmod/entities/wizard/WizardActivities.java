package com.gm910.occentmod.entities.wizard;

import com.gm910.occentmod.init.AIInit;

import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraftforge.fml.RegistryObject;

public class WizardActivities {

	public static final RegistryObject<Activity> BATTLE = AIInit.registerActivity(WizardEntity.PREFIX + "_battle");

	public static void forceClinit() {
	}
}
