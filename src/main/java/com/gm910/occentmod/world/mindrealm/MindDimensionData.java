package com.gm910.occentmod.world.mindrealm;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.capabilities.mental_inventory.MindInventory;
import com.gm910.occentmod.init.DimensionInit;
import com.gm910.occentmod.util.GMFiles;
import com.gm910.occentmod.world.DimensionData;
import com.gm910.occentmod.world.DimensionData.DimensionInfo;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MindDimensionData extends WorldSavedData {
	public static final String NAME = OccultEntities.MODID + "_minddimensions";

	private ServerWorld world;

	private UUID playerOwner;

	private Set<UUID> friendlyEntities = new HashSet<>();

	public MindDimensionData(String name) {
		super(name);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public MindDimensionData() {
		this(NAME);
	}

	public ServerWorld getWorld() {
		return world;
	}

	public Set<UUID> getFriendlyEntityIds() {
		return Sets.newHashSet(friendlyEntities);
	}

	public Set<Entity> getFriendlyEntities() {
		return friendlyEntities.stream().map((mapa) -> ServerPos.getEntityFromUUID(mapa, world.getServer()))
				.collect(Collectors.toSet());
	}

	public void addFriendlyEntity(UUID en) {
		this.friendlyEntities.add(en);
	}

	public void addFriendlyEntity(Entity e) {
		this.addFriendlyEntity(e.getUniqueID());
	}

	public void removeFriendlyEntity(UUID e) {
		this.friendlyEntities.remove(e);
	}

	public void removeFriendlyEntity(Entity e) {
		this.removeFriendlyEntity(e.getUniqueID());
	}

	public boolean isFriendly(UUID e) {
		return this.friendlyEntities.contains(e);
	}

	public boolean isFriendly(Entity e) {
		return this.isFriendly(e.getUniqueID());
	}

	public Set<UUID> getFriendlyEntitySetRaw() {
		return this.friendlyEntities;
	}

	public UUID getPlayerOwner() {
		return playerOwner;
	}

	public void setPlayerOwner(UUID playerOwner) {
		this.playerOwner = playerOwner;
	}

	public void setPlayerOwner(PlayerEntity playerOwner) {
		this.setPlayerOwner(playerOwner.getUniqueID());
	}

	public void setPlayerOwner(GameProfile playerOwner) {
		if (playerOwner.getId() == null)
			return;
		this.setPlayerOwner(playerOwner.getId());
	}

	public PlayerEntity getOwnerEntity() {
		return (PlayerEntity) ServerPos.getEntityFromUUID(this.playerOwner, this.world.getServer());
	}

	public MindDimensionData setWorld(ServerWorld server) {
		this.world = server;
		return this;
	}

	public static MindDimensionData get(ServerWorld server) {
		DimensionSavedDataManager dimdat = server.getSavedData();
		return dimdat.getOrCreate(() -> {
			return (new MindDimensionData()).setWorld(server);
		}, NAME);
	}

	public static MindDimensionData createMindWorld(PlayerEntity player) {
		DimensionData dat = DimensionData.get(player.getServer());
		if (dat.getDimensionInfo(player) != null) {
			return MindDimensionData.get(player.getServer().getWorld(dat.getDimensionInfo(player).getDimensionType()));
		}
		DimensionType type = dat.createDimension(
				GMFiles.rl("mindrealm_r_" + (player.getUniqueID().toString()).replace("-", "_")), DimensionInit.MIND,
				true);
		dat.getDimensionInfo(type).setPlayerOwner(player.getUniqueID());
		return MindDimensionData.get(player.getServer().getWorld(type));

	}

	public static MindDimensionData get(PlayerEntity player) {
		DimensionData dat = DimensionData.get(player.getServer());
		DimensionInfo inf = dat.getDimensionInfo(player);
		MindDimensionData mdat = MindDimensionData.createMindWorld(player);
		return mdat;
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.playerOwner = nbt.getUniqueId("Owner");
		this.friendlyEntities = new HashSet<>(GMNBT.createUUIDList((ListNBT) nbt.get("Friendlies")));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT nbt = compound;
		compound.putUniqueId("Owner", playerOwner);
		compound.put("Friendlies", GMNBT.makeUUIDList(friendlyEntities));
		return nbt;
	}

	@SubscribeEvent
	public void dimensionChange(PlayerChangedDimensionEvent event) {

		DimensionType type = get(event.getPlayer()).getWorld().dimension.getType();

		PlayerEntity player = event.getPlayer();

		MindInventory inventory = MindInventory.get(player);

		inventory.updateInventory(player);

		if (event.getTo() == type) {
			inventory.setInMind(true);
			inventory.transferInventoryToPlayer(player);
		}

		if (event.getFrom() == type) {
			inventory.setInMind(false);
			inventory.transferInventoryToPlayer(player);
		}
	}

	@SubscribeEvent
	public void tick(PlayerTickEvent event) {

		PlayerEntity player = event.player;

		if (player.world.isRemote)
			return;

		MindInventory inventory = MindInventory.get(player);

		inventory.updateInventory(event.player);

	}

	@SubscribeEvent
	public void clone(Clone event) {
		if (event.getOriginal().world.isRemote)
			return;

		DimensionType type = get(event.getPlayer()).getWorld().dimension.getType();

		CompoundNBT dat = MindInventory.get(event.getOriginal()).serializeNBT();
		MindInventory inv = MindInventory.get(event.getPlayer());
		inv.deserializeNBT(dat);
		if (!event.isWasDeath() || event.getPlayer().world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
			if (event.getPlayer().world.dimension.getType() != type) {
				inv.setInMind(false);
			} else {
				inv.setInMind(true);
			}
			inv.transferInventoryToPlayer(event.getPlayer());
		}

		if (event.isWasDeath() && !event.getPlayer().world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
			inv.getPhysicalArmor().getStacksRaw().clear();
			inv.getPhysicalHands().getStacksRaw().clear();
			inv.getPhysicalInventory().getStacksRaw().clear();
		}

	}

}
