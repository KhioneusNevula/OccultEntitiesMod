package com.gm910.occentmod.blockenchant;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.IWorldTickable;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocation;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationManager;
import com.gm910.occentmod.capabilities.speciallocs.SpecialLocationType;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent.WorldTickEvent;

public class SpecialEnchantmentLocation extends SpecialLocation implements IWorldTickable {
	public static final SpecialLocationType<SpecialEnchantmentLocation> TYPE = (new SpecialLocationType<>(
			SpecialEnchantmentLocation.class, SpecialEnchantmentLocation::deserialize))
					.register(new ResourceLocation(OccultEntities.MODID, "enchantment"));

	private BlockEnchantment enchantment;

	public SpecialEnchantmentLocation(SpecialLocationType<?> type, BlockPos pos, SpecialLocationManager manager) {
		super(type, pos, manager);
	}

	public SpecialEnchantmentLocation(BlockEnchantment data, BlockPos pos, SpecialLocationManager manager) {
		this(TYPE, pos, manager);
		this.enchantment = data;
	}

	public BlockEnchantment getEnchantment() {
		return enchantment;
	}

	public void setEnchantment(BlockEnchantment enchant) {
		this.enchantment = enchant;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {

		return enchantment.serialize(ops);
	}

	public static SpecialEnchantmentLocation deserialize(Dynamic<?> dynamic, BlockPos pos,
			SpecialLocationManager manager) {

		return new SpecialEnchantmentLocation(BlockEnchantment.deserialize(dynamic), pos, manager);
	}

	@Override
	public void tick(WorldTickEvent event, long gameTime, long dayTime) {
		if (enchantment != null) {
			enchantment.tick(event, gameTime, dayTime);
		}
	}

	@Override
	public boolean canTickWhileUnloaded() {
		// TODO Auto-generated method stub
		return enchantment != null ? enchantment.canTickWhileUnloaded() : false;
	}
}
