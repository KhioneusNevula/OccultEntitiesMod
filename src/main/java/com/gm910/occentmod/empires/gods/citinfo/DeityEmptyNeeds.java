package com.gm910.occentmod.empires.gods.citinfo;

import java.util.Set;

import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.sapience.mind_and_traits.needs.Need;
import com.gm910.occentmod.sapience.mind_and_traits.needs.NeedChecker;
import com.gm910.occentmod.sapience.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.sapience.mind_and_traits.needs.Needs;
import com.google.common.collect.Sets;

public class DeityEmptyNeeds extends Needs<Deity> {
	public DeityEmptyNeeds(Deity entity) {
		super(entity);
	}

	@Override
	public void addNeed(Need<Deity, ?> need) {
	}

	@Override
	public <T> NeedChecker<Deity, T> getChecker(NeedType<Deity, T> type) {
		return null;
	}

	@Override
	public Set<Need<Deity, ?>> getNeeds(NeedType<Deity, ?> type) {
		return Sets.newHashSet();
	}

	@Override
	public Set<NeedType<Deity, ?>> getNeedTypes() {
		return Sets.newHashSet();
	}

	@Override
	public int getRandomCheckInterval(NeedType<Deity, ?> t) {
		return -1;
	}

	@Override
	public long getTicksExisted() {
		return this.getEntityIn().ticksExisted;
	}

	@Override
	public boolean hasNeed(NeedType<Deity, ?> type) {
		return false;
	}

	@Override
	public void newRandomCheckInterval(NeedType<Deity, ?> t) {
	}

	@Override
	public Needs<Deity> registerNeeds(Set<NeedType<Deity, ?>> needTypes) {
		return this;
	}

	@Override
	public void removeNeed(Need<Deity, ?> need) {
	}

	@Override
	public void tick() {
	}
}