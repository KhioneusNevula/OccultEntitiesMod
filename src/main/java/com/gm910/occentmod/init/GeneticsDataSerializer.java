package com.gm910.occentmod.init;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Genetics;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;

public class GeneticsDataSerializer<E extends LivingEntity> implements IDataSerializer<Genetics<E>> {
	@Override
	public void write(PacketBuffer buf, Genetics<E> value) {
		buf.writeCompoundTag((CompoundNBT) value.serialize(NBTDynamicOps.INSTANCE));
	}

	@Override
	public Genetics<E> read(PacketBuffer buf) {
		return new Genetics<>(GMNBT.makeDynamic(buf.readCompoundTag()));
	}

	@Override
	public Genetics<E> copyValue(Genetics<E> value) {
		return value.copy();
	}
}