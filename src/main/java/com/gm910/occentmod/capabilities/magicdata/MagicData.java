package com.gm910.occentmod.capabilities.magicdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.networking.messages.Networking;
import com.gm910.occentmod.api.networking.messages.types.TaskSyncCapability;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MagicData implements IModCapability<LivingEntity>, INBTSerializable<CompoundNBT> {

	public static final ResourceLocation LOC = new ResourceLocation(OccultEntities.MODID, "magicdatacap");

	private LivingEntity owner;

	private Map<UUID, MysticEffect> properEffects = new HashMap<UUID, MysticEffect>();

	private Object2IntMap<UUID> time = new Object2IntOpenHashMap<>();

	private Map<UUID, ITextComponent> displays = new HashMap<>();

	@Override
	public void $setOwner(LivingEntity wiz) {
		this.owner = wiz;

		MinecraftForge.EVENT_BUS.register(this);
	}

	public boolean isDeity() {
		return this.owner instanceof Deity;
	}

	@Override
	public LivingEntity $getOwner() {
		return owner;
	}

	public Set<MysticEffect> getEffects() {
		return Sets.newHashSet(this.properEffects.values());
	}

	public UUID getId(MysticEffect uu) {
		for (UUID a : properEffects.keySet()) {
			if (properEffects.get(a).equals(uu)) {
				return a;
			}
		}
		return null;
	}

	public MysticEffect getById(UUID u) {
		return properEffects.get(u);
	}

	public int timeLeft(MysticEffect eff) {
		return this.time.getInt(getId(eff));
	}

	public void removeEffect(MysticEffect eff) {
		for (UUID uu : new HashSet<>(properEffects.keySet())) {
			if (properEffects.get(uu) != null && properEffects.get(uu).equals(eff)) {
				properEffects.remove(uu);
			}
		}
		this.time.removeInt(getId(eff));
		this.displays.remove(getId(eff));
	}

	public void addEffect(MysticEffect eff, int time) {
		UUID id = UUID.randomUUID();
		this.properEffects.put(id, eff);
		this.time.put(id, time);
		if (this.owner != null) {
			this.displays.put(id, eff.makeDisplayText(this.owner.world));
		}
	}

	@SubscribeEvent
	public void tick(LivingUpdateEvent event) {

		if (!event.getEntityLiving().isEntityEqual(this.owner)) {

			return;
		}

		if (this.owner.world.isRemote) {
			return;
		}

		for (MysticEffect eff : Sets.newHashSet(this.properEffects.values())) {
			int timeLeft = time.getInt(getId(eff));
			if (timeLeft > 0) {
				time.put(getId(eff), timeLeft - 1);
			} else if (timeLeft == 0) {
				time.removeInt(getId(eff));
			}
		}
		if (!this.isDeity() && this.owner.ticksExisted % 20 == 0) {

			if (!(this.owner.world.getChunkProvider() instanceof ServerChunkProvider)) {
				throw new IllegalStateException(
						"At server tick event, the chunk provider was a " + this.owner.world.getChunkProvider());
			}

			Networking.sendToTracking(new TaskSyncCapability(this.owner.getEntityId(), "MAGIC_DATA", this.owner),
					this.owner);
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT comp = new CompoundNBT();
		comp.put("Times", GMNBT.makeList(this.time.keySet(), (e) -> {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putUniqueId("Effect", e);
			nbt.putInt("Time", time.getInt(e));
			return nbt;
		}));
		comp.put("Effects", GMNBT.makeList(this.properEffects.keySet(), (e) -> {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putUniqueId("Id", e);
			nbt.put("Effect", properEffects.get(e).serialize(NBTDynamicOps.INSTANCE));
			return nbt;
		}));
		return comp;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.time = new Object2IntOpenHashMap<>(GMNBT.createMap((ListNBT) nbt.get("Times"), (n) -> {
			CompoundNBT tag = (CompoundNBT) n;

			return Pair.<UUID, Integer>of(tag.getUniqueId("Effect"), tag.getInt("Time"));
		}));
		this.properEffects = GMNBT.createMap((ListNBT) nbt.get("Times"), (n) -> {
			CompoundNBT tag = (CompoundNBT) n;

			return Pair.<UUID, MysticEffect>of(tag.getUniqueId("Id"),
					new MysticEffect(GMNBT.makeDynamic(tag.get("Effect"))));
		});
	}

	public static MagicData get(CapabilityProvider<?> e) {
		return e.getCapability(com.gm910.occentmod.capabilities.GMCaps.MAGIC_DATA).orElse(null);
	}

}
