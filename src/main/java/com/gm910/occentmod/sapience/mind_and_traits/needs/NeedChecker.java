package com.gm910.occentmod.sapience.mind_and_traits.needs;

import net.minecraft.entity.LivingEntity;

public abstract class NeedChecker<M extends LivingEntity, E> {

	private NeedType<M, E> type;

	private Need<M, E> need;

	protected M entity;

	public NeedChecker(NeedType<M, E> type, M entity) {
		this.type = type;
		this.entity = entity;
	}

	public NeedType<M, E> getType() {
		return type;
	}

	public M getEntity() {
		return entity;
	}

	public Need<M, E> getNeed() {
		return need;
	}

	public void setNeed(Need<?, ?> need) {
		this.need = (Need<M, E>) need;
	}

	public final void tick() {
		Need<M, E> needs = this.findNeeds();
		if (needs != null) {
			this.need = needs;
		}

		boolean fulf = this.fulfillNeeds();

		if (fulf && need != null) {
			need.fulfill();
			need = null;
		}
	}

	/**
	 * Return a need or null if there are none
	 * 
	 * @param autonomy
	 * @return
	 */
	public abstract Need<M, E> findNeeds();

	/**
	 * return true if the need is fulfilled
	 * 
	 * @return
	 */
	public abstract boolean fulfillNeeds();

	public boolean areNeedsFulfilled() {
		return this.need == null ? true : this.need.isFulfilled();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " of type " + type + " with need " + this.need;
	}

}
