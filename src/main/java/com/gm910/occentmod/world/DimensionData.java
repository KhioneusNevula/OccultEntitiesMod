package com.gm910.occentmod.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.init.DimensionInit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class DimensionData extends WorldSavedData {

	public static final String NAME = OccultEntities.MODID + "_dimensions";

	public final Map<ResourceLocation, DimensionInfo> DIMENSIONS = new HashMap<>();

	public static List<DimensionInfo> dimensionsToRegister = new ArrayList<>();

	// public static final ResourceLocation USIFIA_RL = new
	// ResourceLocation(Elkloria.MODID, "usifia");

	public DimensionData(String name) {
		super(name);

	}

	public static RegistryObject<ModDimension> registerWorldMaker(String name,
			BiFunction<World, DimensionType, ? extends Dimension> dimensionconstructor) {

		return DimensionInit.WORLD_MAKERS.register(name,
				() -> OccultDimensionFactory.withFactory(dimensionconstructor));
	}

	public DimensionType registerDimension(DimensionInfo inf) {
		return inf.registerDimensionType(null);
	}

	public DimensionInfo getDimensionInfo(ResourceLocation name) {
		return DIMENSIONS.get(name);
	}

	public DimensionInfo getDimensionInfo(DimensionType type) {
		for (DimensionInfo inf : DIMENSIONS.values()) {
			if (inf.getDimensionType() == type) {
				return inf;
			}
		}
		return null;
	}

	public DimensionInfo getDimensionInfo(UUID owner) {
		for (DimensionInfo inf : DIMENSIONS.values()) {
			if (inf.playerOwner == owner) {
				return inf;
			}
		}
		return null;
	}

	public DimensionInfo getDimensionInfo(PlayerEntity owner) {
		return getDimensionInfo(owner.getUniqueID());
	}

	public DimensionType createDimension(ResourceLocation name, String worldmaker, boolean hasSkyLight) {
		return createDimension(name, DimensionInit.objFrom(worldmaker), hasSkyLight);
	}

	public DimensionType createDimension(ResourceLocation name, RegistryObject<ModDimension> worldmaker,
			boolean hasSkyLight) {
		if (DimensionInit.from(worldmaker) != null) {
			if (!DIMENSIONS.containsKey(name)) {
				DimensionInfo inf = new DimensionInfo(name, worldmaker.getId().getPath(), hasSkyLight);
				DimensionType createdDim = inf.registerDimensionType(null);
				if (createdDim == null)
					return null;
				DIMENSIONS.put(name, inf);
				return createdDim;
			} else {
				return DIMENSIONS.get(name).registerDimensionType(null);
			}
		}
		return null;
	}

	public static DimensionType $registerDimension(ResourceLocation rname,
			BiFunction<World, DimensionType, ? extends Dimension> dimensionconstructor, boolean hasSkyLight) {
		if (DimensionType.byName(rname) == null) {
			DimensionType createdDim = DimensionManager.registerDimension(rname,
					new OccultDimensionFactory(dimensionconstructor), null, hasSkyLight);
			return createdDim;
		}
		return DimensionType.byName(rname);
	}

	public static DimensionType $registerDimension(ResourceLocation rname, RegistryObject<ModDimension> obj,
			boolean hasSkyLight) {
		if (DimensionType.byName(rname) == null) {
			DimensionType createdDim = DimensionManager.registerDimension(rname, obj.get(), null, hasSkyLight);
			return createdDim;
		}
		return DimensionType.byName(rname);
	}

	public static DimensionType $registerDimension(ResourceLocation rname, String worldmaker, boolean hasSkyLight) {

		return $registerDimension(rname, DimensionInit.objFrom(worldmaker), hasSkyLight);
	}

	public static DimensionType registerInitialDimension(ResourceLocation rname,
			RegistryObject<ModDimension> worldmaker, boolean hasSkyLight) {
		DimensionInfo inf = new DimensionInfo(rname, DimensionInit.nameFrom(worldmaker), hasSkyLight);
		DimensionType createdDim = DimensionType.byName(rname);
		if (DimensionType.byName(rname) == null) {
			createdDim = inf.registerDimensionType();
			dimensionsToRegister.add(inf);
		}
		return createdDim;
	}

	public static DimensionType registerInitialDimension(ResourceLocation rname, String worldmaker,
			boolean hasSkyLight) {
		return registerInitialDimension(rname, DimensionInit.objFrom(worldmaker), hasSkyLight);
	}

	public DimensionData() {
		this(NAME);
	}

	@Override
	public void read(CompoundNBT nbt) {
		DIMENSIONS.clear();
		ListNBT ls = nbt.getList("Dimensions", NBT.TAG_COMPOUND);
		for (INBT fn : ls) {
			CompoundNBT tag = (CompoundNBT) fn;
			DimensionInfo inf = DimensionInfo.fromNBT(tag);
			if (inf != null) {
				DIMENSIONS.put(inf.name, inf);
			}
		}
		registerStoredDimensions();
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT nbt = new CompoundNBT();
		ListNBT dimlist = new ListNBT();
		for (ResourceLocation rsource : DIMENSIONS.keySet()) {
			dimlist.add(DIMENSIONS.get(rsource).toNBT());
		}

		nbt.put("Dimensions", dimlist);

		return nbt;
	}

	public void storeInitialDimensions() {
		for (DimensionInfo inf : dimensionsToRegister) {
			this.DIMENSIONS.put(inf.name, inf);

		}
	}

	public static void registerInitialDimensionsStatic() {
		// USIFIA = registerInitialDimension(USIFIA_RL, DimensionInit.USIFIA, true);
	}

	/**
	 * Unregisters all dimensions created by mod
	 */
	public void unregisterStoredDimensions() {
		for (ResourceLocation dim : this.DIMENSIONS.keySet()) {
			DimensionInfo tp = DIMENSIONS.get(dim);
			DimensionManager.unregisterDimension(tp.getId());
		}
	}

	/**
	 * Registers all dimensions created by mod
	 */
	public void registerStoredDimensions() {
		for (ResourceLocation dim : this.DIMENSIONS.keySet()) {
			DimensionInfo inf = DIMENSIONS.get(dim);
			DimensionType type = inf.registerDimensionType(null);
		}
	}

	public static DimensionData get(MinecraftServer server) {
		DimensionSavedDataManager dimdat = server.getWorld(DimensionType.OVERWORLD).getSavedData();
		return dimdat.getOrCreate(() -> {
			return new DimensionData();
		}, NAME);
	}

	/**
	 * This class is a way of storing dimension information for the modular
	 * dimension system I'm trying to make...
	 * 
	 * @author GameCraft910
	 *
	 */
	public static class DimensionInfo {

		private ResourceLocation name;
		private String worldmaker;
		private boolean hasSkyLight;
		@Nullable
		private UUID playerOwner;

		public DimensionInfo(ResourceLocation name, String worldmaker, boolean hasSkyLight) {
			this.name = name;
			this.worldmaker = worldmaker;
			this.hasSkyLight = hasSkyLight;
		}

		public DimensionInfo(DimensionType from) {
			this.name = DimensionType.getKey(from);
			this.worldmaker = DimensionInit.nameFrom(from.getModType());
			this.hasSkyLight = from.hasSkyLight();
		}

		public DimensionInfo setPlayerOwner(UUID play) {
			this.playerOwner = play;
			return this;
		}

		public UUID getPlayerOwner() {
			return playerOwner;
		}

		public static DimensionInfo fromNBT(CompoundNBT nbt) {
			DimensionInfo inf = null;
			if (nbt.contains("Name", NBT.TAG_STRING) && nbt.contains("Worldmaker", NBT.TAG_STRING)
					&& nbt.contains("SkyLight")) {
				inf = new DimensionInfo();
				inf.name = new ResourceLocation(nbt.getString("Name"));
				inf.worldmaker = nbt.getString("Worldmaker");
				inf.hasSkyLight = nbt.getBoolean("SkyLight");
			}
			if (inf != null) {
				if (nbt.hasUniqueId("Owner")) {
					inf.playerOwner = nbt.getUniqueId("Owner");
				}
			}
			return inf;
		}

		private DimensionInfo() {
		}

		public ResourceLocation getName() {
			return name;
		}

		public String getWorldmaker() {
			return worldmaker;
		}

		public ModDimension getActualWorldmaker() {
			return ForgeRegistries.MOD_DIMENSIONS.getValue(new ResourceLocation(worldmaker));
		}

		public boolean hasSkyLight() {
			return hasSkyLight;
		}

		public CompoundNBT toNBT() {
			CompoundNBT nbt = new CompoundNBT();

			nbt.putString("Name", name.toString());
			nbt.putString("Worldmaker", worldmaker);
			nbt.putBoolean("SkyLight", hasSkyLight);
			if (this.playerOwner != null)
				nbt.putUniqueId("Owner", playerOwner);
			return nbt;
		}

		/**
		 * Gets the dimension type, assuming it's already been created
		 * 
		 * @return
		 */
		public DimensionType getDimensionType() {
			return DimensionType.byName(name);
		}

		public ServerWorld getWorld(MinecraftServer serv) {
			return serv.getWorld(this.getDimensionType());
		}

		/**
		 * Registers this dimensionInfo to the forge dimension registry as a proper
		 * dimension If it already is registered, simply returns the existing
		 * dimensionType corresponding to it
		 * 
		 * @param data
		 * @return
		 */
		public DimensionType registerDimensionType(@Nullable PacketBuffer data) {
			if (DimensionType.byName(name) == null) {
				DimensionType createdDim = DimensionManager.registerDimension(name, DimensionInit.from(worldmaker),
						data, hasSkyLight);
				return createdDim;
			}
			return getDimensionType();
		}

		/**
		 * Registers this dimensionInfo to the forge dimension registry as a proper
		 * dimension If it already is registered, simply returns the existing
		 * dimensionType corresponding to it
		 * 
		 * @return
		 */
		public DimensionType registerDimensionType() {
			return registerDimensionType(null);
		}

		public int getId() {
			return getDimensionType().getId();
		}
	}

}
