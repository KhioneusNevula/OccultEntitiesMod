package com.gm910.occentmod.capabilities.speciallocs.types;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocation;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationManager;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class SpecialNBTLocation extends SpecialLocation {

	public static final SpecialLocationType<SpecialNBTLocation> TYPE = (new SpecialLocationType<>(
			SpecialNBTLocation.class, SpecialNBTLocation::deserialize))
					.register(new ResourceLocation(OccultEntities.MODID, "nbtlocation"));

	private CompoundNBT data;

	public SpecialNBTLocation(SpecialLocationType<?> type, BlockPos pos, SpecialLocationManager manager) {
		super(type, pos, manager);
	}

	public SpecialNBTLocation(CompoundNBT data, BlockPos pos, SpecialLocationManager manager) {
		this(TYPE, pos, manager);
		this.data = data;
	}

	public CompoundNBT getData() {
		return data;
	}

	public void setData(CompoundNBT data) {
		this.data = data;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {

		return ops.createString(data.getString());
	}

	public static SpecialNBTLocation deserialize(Dynamic<?> dynamic, BlockPos pos, SpecialLocationManager manager) {
		try {
			return new SpecialNBTLocation(JsonToNBT.getTagFromJson(dynamic.asString("")), pos, manager);
		} catch (CommandSyntaxException e) {
			return null;
		}
	}

}
