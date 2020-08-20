package com.gm910.occentmod.entities.citizen.mind_and_traits;

import net.minecraft.util.IDynamicSerializable;

public abstract class InformationHolder implements IDynamicSerializable {

	protected void tick() {

	}

	public void update() {
		tick();
	}

	public abstract long getTicksExisted();
}
