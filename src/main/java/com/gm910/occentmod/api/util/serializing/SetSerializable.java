package com.gm910.occentmod.api.util.serializing;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;

public class SetSerializable<E> implements IDynamicSerializable {

	public final Set<E> value;

	public final BiFunction<E, DynamicOps<?>, ?> serializer;

	public final Function<Dynamic<?>, E> deserializer;

	public <T> SetSerializable(Set<E> e, BiFunction<E, DynamicOps<?>, ?> serializer,
			Function<Dynamic<?>, E> deserializer) {
		this.value = e;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public <T> T serialize(DynamicOps<T> o) {
		return o.createList(value.stream().map((m) -> (T) serializer.apply(m, o)));
	}

	public SetSerializable(Dynamic<?> dyn, BiFunction<E, DynamicOps<?>, ?> serializer,
			Function<Dynamic<?>, E> deserializer) {
		this(dyn.asStream().map(deserializer).collect(Collectors.toSet()), serializer, deserializer);
	}

}
