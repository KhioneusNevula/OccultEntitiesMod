package com.gm910.occentmod.empires.gods.citinfo;

import java.util.Set;

import com.gm910.occentmod.sapience.mind_and_traits.needs.Need;
import com.gm910.occentmod.sapience.mind_and_traits.needs.NeedChecker;
import com.gm910.occentmod.sapience.mind_and_traits.needs.NeedType;
import com.gm910.occentmod.sapience.mind_and_traits.needs.Needs;
import com.google.common.collect.Sets;

import net.minecraft.entity.LivingEntity;

public class EmptyNeeds<M extends LivingEntity> extends Needs<M> {
	public EmptyNeeds(M entity) {
		super(entity);
	}

	@Override
	public void addNeed(Need<M, ?> need) {
	}

	@Override
	public <T> NeedChecker<M, T> getChecker(NeedType<M, T> type) {
		return null;
	}

	@Override
	public Set<Need<M, ?>> getNeeds(NeedType<M, ?> type) {
		return Sets.newHashSet();
	}

	@Override
	public Set<NeedType<M, ?>> getNeedTypes() {
		return Sets.newHashSet();
	}

	@Override
	public int getRandomCheckInterval(NeedType<M, ?> t) {
		return -1;
	}

	@Override
	public long getTicksExisted() {
		return this.getEntityIn().ticksExisted;
	}

	@Override
	public boolean hasNeed(NeedType<M, ?> type) {
		return false;
	}

	@Override
	public void newRandomCheckInterval(NeedType<M, ?> t) {
	}

	@Override
	public Needs<M> registerNeeds(Set<NeedType<M, ?>> needTypes) {
		return this;
	}

	@Override
	public void removeNeed(Need<M, ?> need) {
	}

	@Override
	public void tick() {
	}
}