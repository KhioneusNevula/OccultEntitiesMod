package com.gm910.occentmod.entities.citizen.mind_and_traits.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class MemoryOfBlockRegion extends CitizenMemory {

	private Map<BlockPos, BlockState> blocks = new HashMap<>();

	private DimensionType dim;

	public MemoryOfBlockRegion(CitizenEntity owner, DimensionType dim, Pair<BlockPos, BlockState>... statesPoses) {
		super(owner, CitizenMemoryType.BLOCKSTATE);
		this.dim = dim;
		this.blocks.putAll(
				Sets.newHashSet(statesPoses).stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
	}

	public MemoryOfBlockRegion(CitizenEntity owner, Dynamic<?> dyn) {
		this(owner, DimensionType.getById(dyn.get("dim").asInt(0)),
				dyn.get("blocks").asStream().<Pair<BlockPos, BlockState>>map((e) -> {
					try {
						return Pair.of(BlockPos.fromLong(e.get("pos").asLong(0)),
								NBTUtil.readBlockState(JsonToNBT.getTagFromJson(e.get("state").asString(""))));
					} catch (CommandSyntaxException e1) {
						return null;
					}
				}).filter((e) -> e != null).toArray(Pair[]::new));
	}

	@Override
	public <T> T writeData(DynamicOps<T> ops) {
		T s = ops.createList(blocks.entrySet().stream()
				.map((e) -> ops.createMap(ImmutableMap.of(ops.createString("blocks"),
						ops.createLong(e.getKey().toLong()), ops.createString("state"),
						ops.createString(NBTUtil.writeBlockState(e.getValue()).getString())))));

		return ops.createMap(ImmutableMap.of(ops.createString("dim"), ops.createInt(this.dim.getId()),
				ops.createString("blocks"), s));
	}

	@Override
	public void affectCitizen(CitizenEntity en) {

	}

}
