package com.gm910.occentmod.sapience.mind_and_traits.memory.memories;

import com.gm910.occentmod.init.GMDeserialize;
import com.gm910.occentmod.sapience.mind_and_traits.memory.MemoryType;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;

public class MemoryOfSerializable<E, Owner extends LivingEntity> extends Memory<Owner> {

	private E value;

	private GMDeserialize<E> deserializer;

	public MemoryOfSerializable(Owner owner, GMDeserialize<E> valueClass) {
		super(owner, MemoryType.SERIALIZABLE);
		this.deserializer = valueClass;
	}

	public GMDeserialize<E> getDeserializer() {
		return deserializer;
	}

	public void setDeserializer(GMDeserialize<E> deserializer) {
		this.deserializer = deserializer;
	}

	public MemoryOfSerializable(Owner owner, GMDeserialize<E> deserializer, E value) {
		this(owner, deserializer);
		this.value = value;
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		return ops.createMap(ImmutableMap.of(ops.createString("value"), deserializer.serialize(value, ops),
				ops.createString("deserializer"), ops.createString(this.deserializer.getResource().toString())));
	}

	@Override
	public void affectCitizen(Owner en) {

	}

	public E getValue() {
		return value;
	}

	public void setValue(E value) {
		this.value = value;
	}

}
