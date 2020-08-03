package com.gm910.occentmod.capabilities.speciallocs;

import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;

public abstract class SpecialLocation implements IDynamicSerializable {

	private final SpecialLocationType<?> type;

	private BlockPos pos;

	private SpecialLocationManager manager;

	public SpecialLocation(SpecialLocationType<?> type, BlockPos pos, SpecialLocationManager manager) {
		this.type = type;
		this.pos = pos;
		this.manager = manager;
	}

	public SpecialLocationType<?> getType() {
		return type;
	}

	public ResourceLocation getRegistryKey() {
		return SpecialLocationManager.getForPoint(this);
	}

	public SpecialLocationManager getManager() {
		return manager;
	}

	public BlockPos getPos() {
		return pos;
	}

	protected void setPos(BlockPos pos) {
		this.pos = pos;
	}

	protected void setManager(SpecialLocationManager manager) {
		this.manager = manager;
	}

	public void blockChange(BlockEvent.NeighborNotifyEvent event, SpecialLocationManager manager) {
	}
}
