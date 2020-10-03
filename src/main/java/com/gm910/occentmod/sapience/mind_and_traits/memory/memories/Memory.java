package com.gm910.occentmod.sapience.mind_and_traits.memory.memories;

import org.apache.commons.lang3.math.Fraction;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Emotions.EmotionType;
import com.gm910.occentmod.sapience.mind_and_traits.memory.MemoryType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
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

	private Fraction memTolerance = Fraction.getFraction(3, 1);

	public Memory(E owner, MemoryType<?> type) {
		this.setOwner(owner);
		this.doerType = (Class<E>) owner.getClass();
		this.type = type;
		this.memoryCreationTime = this.getOwner().world.getGameTime();

	}

	public Class<E> getOwnerType() {
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
		Memory<E> kno = (Memory<E>) MemoryType.get(des).deserializer.apply(owner, dynamic.get("data").get().get());
		kno.memoryCreationTime = dynamic.get("creationtime").asLong(0);
		kno.accessedTimes = dynamic.get("accessedtimes").asInt(0);
		return kno;
	}

	/**
	 * Indicate that the memory has been used by the citizen
	 */
	public Memory<E> access() {
		accessedTimes++;
		this.affectMood();
		return this;
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

	public void affectMood() {
		SapientInfo.get(this.owner).getEmotions().changeLevel(EmotionType.COMFORT, this.getOpinion().getValue() * 0.1f);
	}

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
		if (e == null) {
			throw new IllegalStateException("Memory " + of + "'s deserializer from type " + of.getType() + " : <"
					+ of.getType().deserializer + "> is dysfunctional");
		}
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

	public Connotation getOpinion() {
		return Connotation.INDIFFERENT;
	}

	/**
	 * the amount of times the memory has to be accessed per [denominator] days to
	 * not be forgotten
	 * 
	 * @return
	 */
	public Fraction memTolerance() {
		return memTolerance;
	}

	public void setMemTolerance(int memTolerance, int perDays) {
		this.memTolerance = Fraction.getFraction(memTolerance, perDays);
	}

	public void setMemTolerance(Fraction val) {
		this.memTolerance = val;
	}

	public void setOwner(E owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return this.getDisplayText().getFormattedText();
	}

}
