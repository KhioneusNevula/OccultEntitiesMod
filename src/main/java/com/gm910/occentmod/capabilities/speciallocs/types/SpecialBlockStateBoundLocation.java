package com.gm910.occentmod.capabilities.speciallocs.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import com.gm910.occentmod.api.functionalinterfaces.ModFunc.TriFunction;
import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocation;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationManager;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationType;
import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public abstract class SpecialBlockStateBoundLocation extends SpecialLocation {

	public SpecialBlockStateBoundLocation(BlockLocationType type, BlockPos pos, SpecialLocationManager manager) {
		super(type, pos, manager);
	}

	public Set<BlockState> getBlockStates() {
		return ((BlockLocationType) this.getType()).getBlocks();
	}

	public static class BlockLocationType extends SpecialLocationType<SpecialBlockStateBoundLocation> {

		private Set<BlockState> blocks;

		public BlockLocationType(String modid, String registryname, Set<BlockState> blocks,
				TriFunction<Dynamic<?>, BlockPos, SpecialLocationManager, SpecialBlockStateBoundLocation> deserializer,
				BiFunction<BlockPos, SpecialLocationManager, SpecialBlockStateBoundLocation> supplier) {
			super(SpecialBlockStateBoundLocation.class, deserializer, null, null, supplier, null);
			this.blocks = blocks;
			this.generator = (event, manager) -> {
				NonNullMap<BlockPos, List<SpecialBlockStateBoundLocation>> locs = new NonNullMap<>(
						() -> new ArrayList<>());

				return locs;
			};
			this.onChange = (event, manager) -> {
				List<SpecialBlockStateBoundLocation> locs = manager.getByType(event.getPos(), this);
				for (SpecialBlockStateBoundLocation loc : locs) {
					if (!loc.getBlockStates().contains(event.getState())) {
						manager.remove(event.getPos(), loc);
					}
				}
				if (locs.isEmpty() && blocks.contains(event.getWorld().getBlockState(event.getPos()))) {
					manager.add(event.getPos(), supplier.apply(event.getPos(), manager));
				}
				if (locs.size() > 1) {
					SpecialLocation loc1 = locs.get(0);
					manager.clear(event.getPos());
					manager.add(event.getPos(), loc1);
				}
			};
			SpecialLocationManager.registerPointType(modid, "blocklocation_" + registryname, this);
		}

		public Set<BlockState> getBlocks() {
			return blocks;
		}

	}

}
