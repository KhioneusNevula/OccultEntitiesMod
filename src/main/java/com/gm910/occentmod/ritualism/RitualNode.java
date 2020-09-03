package com.gm910.occentmod.ritualism;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.math.BlockPos;

public abstract class RitualNode implements IDynamicSerializable {

	private Ritual owner;

	private RitualNodeType<?> type;

	public RitualNode(RitualNodeType<?> type, Ritual owner) {
		this.owner = owner;
		this.type = type;
	}

	public RitualNodeType<?> getType() {
		return type;
	}

	public Ritual getOwner() {
		return owner;
	}

	public void setOwner(Ritual owner) {
		this.owner = owner;
	}

	public abstract boolean accepts(CompoundNBT nbt);

	public abstract CompoundNBT runFunction(BlockPos positionAt);

	public BlockPos getPosition() {
		return this.owner.getPosition(this);
	}

	public abstract <T> T write(DynamicOps<T> ops);

	@Override
	public final <T> T serialize(DynamicOps<T> ops) {
		return ops.createMap(ImmutableMap.of(ops.createString("id"), ops.createString(this.type.resource.toString()),
				ops.createString("data"), this.write(ops)));
	}

	public abstract <T> void read(Dynamic<T> dynamic);

}
