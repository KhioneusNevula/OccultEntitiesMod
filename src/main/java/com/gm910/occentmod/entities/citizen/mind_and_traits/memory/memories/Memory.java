package com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryType;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class Memory<E extends LivingEntity> implements IDynamicSerializable, Cloneable {

	protected MemoryType<?> type;

	private E owner;

	protected long memoryCreationTime;

	private int accessedTimes;

	private Class<E> doerType;

	public Memory(E owner, MemoryType<?> type) {
		this.setOwner(owner);
		this.doerType = (Class<E>) owner.getClass();
		this.type = type;
		this.memoryCreationTime = this.getOwner().world.getGameTime();

	}

	public Class<E> getDoerType() {
		return doerType;
	}

	public long getMemoryCreationTime() {
		return memoryCreationTime;
	}

	public long getTicksExisted() {
		return this.owner.world.getGameTime() - memoryCreationTime;
	}

	public int getAccessedTimes() {
		return accessedTimes;
	}

	public void setAccessedTimes(int accessedTimes) {
		this.accessedTimes = accessedTimes;
	}

	public static <E extends LivingEntity> Memory<E> deserialize(E owner, Dynamic<?> dynamic) {
		ResourceLocation des = new ResourceLocation(dynamic.get("resource").asString(""));
		Memory<E> kno = MemoryType.get(des).deserializer.apply(owner, dynamic.get("data").get().get());
		kno.memoryCreationTime = dynamic.get("creationtime").asLong(0);
		kno.accessedTimes = dynamic.get("accessedtimes").asInt(0);
		return kno;
	}

	/**
	 * Indicate that the memory has been used by the citizen
	 */
	public void access() {
		accessedTimes++;
	}

	@Override
	public <T> T serialize(DynamicOps<T> op) {

		T str = op.createString(type.regName.toString());
		T m = op.createLong(memoryCreationTime);
		T dat = writeData(op);
		T d = op.createInt(accessedTimes);
		return op.createMap(ImmutableMap.of(op.createString("resource"), str, op.createString("data"), dat,
				op.createString("creationtime"), m, op.createString("accessedtimes"), d));
	}

	public abstract <T> T writeData(DynamicOps<T> ops);

	public abstract void affectCitizen(E en);

	public ITextComponent getDisplayText() {
		return type.display(this);
	}

	public MemoryType<?> getType() {
		return type;
	}

	public boolean equals(Object o) {

		return this.getType() == ((Memory<?>) o).getType()
				&& this.writeData(NBTDynamicOps.INSTANCE).equals(((Memory<?>) o).writeData(NBTDynamicOps.INSTANCE));
	}

	public static <E extends LivingEntity, T extends Memory<E>> T copy(E owner, T of) {
		T e = (T) of.getType().deserializer.apply(owner, GMNBT.makeDynamic(of.serialize(NBTDynamicOps.INSTANCE)));
		e.setOwner(owner);
		e.memoryCreationTime = owner.ticksExisted;
		return e;
	}

	@Override
	public Memory<E> clone() throws CloneNotSupportedException {
		return (Memory<E>) super.clone();
	}

	public E getOwner() {
		return owner;
	}

	/**
	 * Whether the memory should be deleted when next accessed
	 * 
	 * @return
	 */
	public boolean isUseless() {
		return false;
	}

	/**
	 * the amount of times the memory has to be accessed to not be forgotten
	 * 
	 * @return
	 */
	public int memTolerance() {
		return 3;
	}

	public void setOwner(E owner) {
		this.owner = owner;
	}

}
