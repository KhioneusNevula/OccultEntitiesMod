package com.gm910.occentmod.sapience.mind_and_traits.needs;

import java.util.Set;

import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.IDynamicSerializable;

public class Need<M extends LivingEntity, T> implements IDynamicSerializable {

	private T desiredValue;

	private NeedType<M, T> type;

	private boolean fulfilled;

	private boolean inDanger;

	public Need(NeedType<M, T> type, T desiredValue) {
		this.type = type;
		this.desiredValue = desiredValue;
		this.fulfilled = false;
	}

	public Need<M, T> makeDangerous() {
		this.inDanger = true;
		return this;
	}

	public boolean isInDanger() {
		return inDanger;
	}

	public T getDesiredValue() {
		return desiredValue;
	}

	public NeedType<M, T> getType() {
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

	public Set<SapientTask<M>> getFulfillmentTasks(M en) {
		return this.type.getNeedFulfillmentTask(this, en);
	}

	@Override
	public boolean equals(Object obj) {
		boolean b = false;
		try {
			b = this.type == ((Need<?, ?>) obj).type && this.inDanger == ((Need<?, ?>) obj).inDanger
					&& this.desiredValue.equals(((Need<?, ?>) obj).desiredValue);
		} catch (ClassCastException e) {

		}
		return b;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return (this.isFulfilled() ? "Fulfilled " : "Unfulfilled ") + this.getClass().getSimpleName() + " of type "
				+ this.type + " with value " + this.desiredValue;
	}

}
