package com.gm910.occentmod.entities.citizen.mind_and_traits.memory.memories;

import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.entities.citizen.mind_and_traits.memory.MemoryType;
import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;

public class MemoryOfBlockstate<E extends LivingEntity> extends Memory<E> {

	private ServerPos storedPos;

	private BlockState storedState;

	public MemoryOfBlockstate(E owner, BlockState type, ServerPos pos) {
		super(owner, MemoryType.BLOCKSTATE);

		this.storedState = type;
		this.storedPos = pos;
	}

	public MemoryOfBlockstate(E owner, Dynamic<?> dyn) throws CommandSyntaxException {
		this(owner, NBTUtil.readBlockState(JsonToNBT.getTagFromJson(dyn.get("state").asString(""))),
				ServerPos.deserialize(dyn.get("pos").get().get()));
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		INBT nbt = NBTUtil.writeBlockState(storedState);

		return ops.createMap(ImmutableMap.of(ops.createString("state"), ops.createString(nbt.getString()),
				ops.createString("pos"), storedPos.serialize(ops)));
	}

	public BlockState getStoredState() {
		return storedState;
	}

	public ServerPos getStoredPos() {
		return storedPos;
	}

	@Override
	public void affectCitizen(E en) {

	}

}
