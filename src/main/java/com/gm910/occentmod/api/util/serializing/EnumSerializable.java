package com.gm910.occentmod.api.util.serializing;

import com.gm910.occentmod.api.util.GMHelper;
import com.gm910.occentmod.api.util.ModReflect;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;

public class EnumSerializable<M extends Enum<M>> implements IDynamicSerializable {

	public final M value;

	public final Class<M> clazz;

	public EnumSerializable(M e) {
		this(e, (Class<M>) e.getClass());
	}

	public EnumSerializable(M e, Class<M> clazz) {
		this.value = e;
		this.clazz = clazz;
	}

	@Override
	public <T> T serialize(DynamicOps<T> o) {
		return o.createString(value.name());
	}

	public EnumSerializable(Dynamic<?> dyn, Class<M> clazz) {
		String default1 = ModReflect.run(clazz, GMHelper.getArrayClass(clazz), "values", null, null)[0].name();
		this.value = ModReflect.run(clazz, clazz, "valueOf", null, (dyn.asString(default1)));
		this.clazz = clazz;
	}

}
