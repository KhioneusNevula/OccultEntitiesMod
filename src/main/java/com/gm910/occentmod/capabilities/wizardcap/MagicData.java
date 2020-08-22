package com.gm910.occentmod.capabilities.wizardcap;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.capabilities.IModCapability;
import com.gm910.occentmod.empires.gods.Deity;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MagicData implements IModCapability<CapabilityProvider<?>>, INBTSerializable<CompoundNBT> {

	public static final ResourceLocation LOC = new ResourceLocation(OccultEntities.MODID, "magicdatacap");

	private CapabilityProvider<?> owner;

	private LivingEntity living;

	private Deity deity;

	private Object2IntMap<MysticEffect> effects = new Object2IntOpenHashMap<>();

	@Override
	public void $setOwner(CapabilityProvider<?> wiz) {
		this.owner = wiz;
		if (wiz instanceof LivingEntity && ((LivingEntity) wiz).isServerWorld()) {
			living = (LivingEntity) wiz;

			deity = null;
		} else if (wiz instanceof Deity) {
			deity = (Deity) wiz;
			living = null;
		} else {
			throw new IllegalArgumentException(
					wiz + " must be instanceof " + Deity.class + " or " + LivingEntity.class);
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	public Deity getDeity() {
		return deity;
	}

	public LivingEntity getLiving() {
		return living;
	}

	public boolean isDeity() {
		return this.deity != null;
	}

	public boolean isLiving() {
		return this.living != null;
	}

	@Override
	public CapabilityProvider<?> $getOwner() {
		return owner;
	}

	@SubscribeEvent
	public void tick(ServerTickEvent event) {
		for (MysticEffect eff : Sets.newHashSet(this.effects.keySet())) {
			int timeLeft = effects.getInt(eff);
			if (timeLeft > 0) {
				effects.put(eff, timeLeft - 1);
			} else if (timeLeft == 0) {
				effects.removeInt(eff);
			}
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT comp = new CompoundNBT();
		comp.put("Effects", GMNBT.makeList(this.effects.keySet(), (e) -> {
			CompoundNBT nbt = new CompoundNBT();
			nbt.put("Effect", e.serialize(NBTDynamicOps.INSTANCE));
			nbt.putInt("Time", effects.getInt(e));
			return nbt;
		}));
		return comp;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.effects = new Object2IntOpenHashMap<>(GMNBT.createMap((ListNBT) nbt.get("Effects"), (n) -> {
			CompoundNBT tag = (CompoundNBT) n;

			return Pair.<MysticEffect, Integer>of(new MysticEffect(GMNBT.makeDynamic(tag.get("Effect"))),
					tag.getInt("Time"));
		}));
	}

	public static MagicData get(CapabilityProvider<?> e) {
		return e.getCapability(com.gm910.occentmod.capabilities.CapabilityProvider.MAGIC_DATA).orElse(null);
	}

}
