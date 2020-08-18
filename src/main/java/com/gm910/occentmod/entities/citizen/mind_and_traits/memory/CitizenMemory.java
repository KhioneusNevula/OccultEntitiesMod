package com.gm910.occentmod.entities.citizen.mind_and_traits.memory;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class CitizenMemory implements IDynamicSerializable {

	private CitizenMemoryType<?> type;

	protected CitizenEntity owner;

	private int age;

	/**
	 * Number of time the memory is "used" by the citizen; checked against its age
	 * to determine if it is forgotten
	 */
	private int accesses;

	/**
	 * Whether the memory is more or less forgotten from not enough recalling
	 */
	private boolean isDead;

	public CitizenMemory(CitizenEntity owner, CitizenMemoryType<?> type) {
		this.owner = owner;
		this.type = type;
	}

	public static CitizenMemory deserialize(CitizenEntity owner, Dynamic<?> dynamic) {
		ResourceLocation des = new ResourceLocation(dynamic.get("resource").asString(""));
		CitizenMemory kno = CitizenMemoryType.get(des).deserializer.apply(owner, dynamic.get("data").get().get());
		return kno;
	}

	@Override
	public <T> T serialize(DynamicOps<T> op) {

		T str = op.createString(type.regName.toString());
		T dat = writeData(op);

		return op.createMap(ImmutableMap.of(op.createString("resource"), str, op.createString("data"), dat));
	}

	public abstract <T> T writeData(DynamicOps<T> ops);

	public abstract void affectCitizen(CitizenEntity en);

	public ITextComponent getDisplayText() {
		return type.display(this);
	}

}
