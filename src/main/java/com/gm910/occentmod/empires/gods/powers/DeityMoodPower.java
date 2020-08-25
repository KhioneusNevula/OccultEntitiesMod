package com.gm910.occentmod.empires.gods.powers;

import com.gm910.occentmod.capabilities.citizeninfo.CitizenInfo;
import com.gm910.occentmod.empires.Empire;
import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.empires.gods.DeityElement;
import com.gm910.occentmod.empires.gods.DeityPower;
import com.gm910.occentmod.entities.citizen.mind_and_traits.emotions.Mood;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public abstract class DeityMoodPower extends DeityPower {

	private Mood mood;

	public DeityMoodPower(ResourceLocation loc, Mood mood, DeityElement... elements) {
		super(loc, elements);
		this.mood = mood;
	}

	public Mood getMood() {
		return mood;
	}

	@Override
	public void usePower(Deity owner, Event e) {

		Empire em = owner.getEmpire();
		deployPower(e, owner, em, CitizenInfo.get(owner).getEmotions().getTimeLeft(mood));
	}

	public abstract void deployPower(Event e, Deity owner, Empire em, int moodTimeLeft);

}
