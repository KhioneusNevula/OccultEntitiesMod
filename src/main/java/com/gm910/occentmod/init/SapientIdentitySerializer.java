package com.gm910.occentmod.init;

import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;

public class SapientIdentitySerializer implements IDataSerializer<SapientIdentity> {
	@Override
	public void write(PacketBuffer buf, SapientIdentity value) {
		buf.writeCompoundTag((CompoundNBT) value.serialize(NBTDynamicOps.INSTANCE));
	}

	@Override
	public SapientIdentity read(PacketBuffer buf) {
		return new SapientIdentity(GMNBT.makeDynamic(buf.readCompoundTag()));
	}

	@Override
	public SapientIdentity copyValue(SapientIdentity value) {
		return value.copy();
	}
}