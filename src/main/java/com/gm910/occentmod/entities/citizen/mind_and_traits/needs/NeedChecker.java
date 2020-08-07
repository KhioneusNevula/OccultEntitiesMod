package com.gm910.occentmod.entities.citizen.mind_and_traits.needs;

import com.gm910.occentmod.entities.citizen.CitizenEntity;

public abstract class NeedChecker<E> {

	private NeedType<E> type;

	private Need<E> need;

	protected CitizenEntity entity;

	public NeedChecker(NeedType<E> type, CitizenEntity entity) {
		this.type = type;
		this.entity = entity;
	}

	public NeedType<E> getType() {
		return type;
	}

	public CitizenEntity getEntity() {
		return entity;
	}

	public Need<E> getNeed() {
		return need;
	}

	public void setNeed(Need<?> need) {
		this.need = (Need<E>) need;
	}

	public final void tick() {
		Need<E> needs = this.findNeeds();
		if (needs != null) {
			this.need = needs;
		}

		boolean fulf = this.fulfillNeeds();

		if (fulf) {
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
	public abstract Need<E> findNeeds();

	/**
	 * return true if the need is fulfilled
	 * 
	 * @return
	 */
	public abstract boolean fulfillNeeds();

	public boolean areNeedsFulfilled() {
		return this.need == null ? true : this.need.isFulfilled();
	}

}
