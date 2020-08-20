package com.gm910.occentmod.entities.citizen.mind_and_traits.memory;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class CitizenMemory implements IDynamicSerializable {

	protected CitizenMemoryType<?> type;

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

	private long memoryCreationTime;

	public CitizenMemory(CitizenEntity owner, CitizenMemoryType<?> type) {
		this.owner = owner;
		this.type = type;
		this.memoryCreationTime = this.owner.world.getGameTime();
	}

	public long getMemoryCreationTime() {
		return memoryCreationTime;
	}

	public static CitizenMemory deserialize(CitizenEntity owner, Dynamic<?> dynamic) {
		ResourceLocation des = new ResourceLocation(dynamic.get("resource").asString(""));
		CitizenMemory kno = CitizenMemoryType.get(des).deserializer.apply(owner, dynamic.get("data").get().get());
		kno.memoryCreationTime = dynamic.get("creationtime").asLong(0);
		return kno;
	}

	@Override
	public <T> T serialize(DynamicOps<T> op) {

		T str = op.createString(type.regName.toString());
		T m = op.createLong(memoryCreationTime);
		T dat = writeData(op);

		return op.createMap(ImmutableMap.of(op.createString("resource"), str, op.createString("data"), dat,
				op.createString("creationtime"), m));
	}

	public abstract <T> T writeData(DynamicOps<T> ops);

	public abstract void affectCitizen(CitizenEntity en);

	public ITextComponent getDisplayText() {
		return type.display(this);
	}

	public CitizenMemoryType<?> getType() {
		return type;
	}

	public boolean equals(Object o) {

		return this.getType() == ((CitizenMemory) o).getType()
				&& this.writeData(NBTDynamicOps.INSTANCE).equals(((CitizenMemory) o).writeData(NBTDynamicOps.INSTANCE));
	}

	public static <T extends CitizenMemory> T copy(T of) {
		return (T) of.getType().deserializer.apply(of.owner, GMNBT.makeDynamic(of.serialize(NBTDynamicOps.INSTANCE)));
	}

}
