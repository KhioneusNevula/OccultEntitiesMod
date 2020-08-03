package com.gm910.occentmod.api.util.serializing;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;

public class StringSerializable implements IDynamicSerializable {

	public final String value;

	public StringSerializable(String e) {
		this.value = e;
	}

	@Override
	public <T> T serialize(DynamicOps<T> o) {
		return o.createString(value);
	}

	public StringSerializable(Dynamic<?> dyn) {
		this.value = dyn.asString("");
	}

}
