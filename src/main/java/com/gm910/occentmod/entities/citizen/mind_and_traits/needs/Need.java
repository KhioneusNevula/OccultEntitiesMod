package com.gm910.occentmod.entities.citizen.mind_and_traits.needs;

import java.util.Set;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.gm910.occentmod.entities.citizen.mind_and_traits.task.CitizenTask;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;

public class Need<T> implements IDynamicSerializable {

	private T desiredValue;

	private NeedType<T> type;

	private boolean fulfilled;

	public Need(NeedType<T> type, T desiredValue) {
		this.type = type;
		this.desiredValue = desiredValue;
		this.fulfilled = false;
	}

	public T getDesiredValue() {
		return desiredValue;
	}

	public NeedType<T> getType() {
		return type;
	}

	public boolean isFulfilled() {
		return fulfilled;
	}

	public void fulfill() {
		fulfilled = true;
	}

	public void setFulfilled(boolean fulf) {
		fulfilled = fulf;
	}

	@Override
	public <L> L serialize(DynamicOps<L> ops) {

		L data = (L) type.serialize(ops, this);
		L type1 = ops.createString(type.resource.toString());
		L fulf = ops.createBoolean(fulfilled);
		return ops.createMap(ImmutableMap.of(ops.createString("data"), data, ops.createString("type"), type1,
				ops.createString("fulf"), fulf));
	}

	public Set<CitizenTask> getFulfillmentTasks(CitizenEntity en) {
		return this.type.getNeedFulfillmentTask(this, en);
	}

	@Override
	public boolean equals(Object obj) {
		boolean b = false;
		try {
			b = this.type == ((Need) obj).type && this.desiredValue.equals(((Need) obj).desiredValue);
		} catch (ClassCastException e) {

		}
		return b;
	}

}
