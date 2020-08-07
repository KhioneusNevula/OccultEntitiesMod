package com.gm910.occentmod.entities.citizen.mind_and_traits.task;

import com.gm910.occentmod.init.DataInit;

import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;

public class CitizenSchedule {

	public static void forceClinit() {
	}

	public static final Schedule DEFAULT = DataInit.registerSchedule("default").add(10, Activity.CORE).build();

}
