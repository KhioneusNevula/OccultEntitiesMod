package com.gm910.occentmod.empires.gods.citinfo;

import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.skills.SkillType;
import com.gm910.occentmod.entities.citizen.mind_and_traits.skills.Skills;

public class DeityEmptySkills extends Skills {

	private Deity deit;

	public DeityEmptySkills(Deity deit) {
		this.deit = deit;
	}

	@Override
	public int getSkill(SkillType trait) {
		return trait.getMax();
	}

	@Override
	public long getTicksExisted() {
		return deit.ticksExisted;
	}

	@Override
	public void setSkill(SkillType trait, int value) {
	}
}