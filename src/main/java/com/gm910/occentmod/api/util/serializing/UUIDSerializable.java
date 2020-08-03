package com.gm910.occentmod.api.util.serializing;

import java.util.UUID;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;

public class UUIDSerializable implements IDynamicSerializable {

	public final UUID value;

	public UUIDSerializable(UUID e) {
		this.value = e;
	}

	@Override
	public <T> T serialize(DynamicOps<T> o) {
		return o.createString(value.toString());
	}

	public UUIDSerializable(Dynamic<?> dyn) {
		this.value = UUID.fromString(dyn.asString(UUID.randomUUID().toString()));
	}

}
