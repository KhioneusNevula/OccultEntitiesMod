package com.gm910.occentmod.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.vaettr.Vaettr;
import com.gm910.occentmod.vaettr.Vaettr.VaettrType;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class VaettrData extends WorldSavedData {

	public static final String NAME = OccultEntities.MODID + "_vaettir";

	private final Map<UUID, Vaettr> VAETTIR = new HashMap<>();

	private MinecraftServer server;

	public VaettrData(String name) {
		super(name);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public VaettrData() {
		this(NAME);
	}

	public MinecraftServer getServer() {
		return server;
	}

	public VaettrData setServer(MinecraftServer server) {
		this.server = server;
		return this;
	}

	public static VaettrData get(MinecraftServer server) {
		DimensionSavedDataManager dimdat = server.getWorld(DimensionType.OVERWORLD).getSavedData();
		return dimdat.getOrCreate(() -> {
			return (new VaettrData()).setServer(server);
		}, NAME);
	}

	public static VaettrData get(World server) {
		DimensionSavedDataManager dimdat = server.getServer().getWorld(DimensionType.OVERWORLD).getSavedData();
		return dimdat.getOrCreate(() -> {
			return (new VaettrData()).setServer(server.getServer());
		}, NAME);
	}

	public Map<UUID, Vaettr> getVaettir() {
		return VAETTIR;
	}

	public UUID addVaettr(Vaettr vaettr) {

		if (getForVaettr(vaettr) != null)
			return getForVaettr(vaettr);

		UUID uu = null;
		boolean flag = false;
		for (int i = 0; i < 256 && !flag; i++) {
			uu = UUID.randomUUID();
			if (!VAETTIR.containsKey(uu)) {
				flag = true;
			}
		}

		if (!flag) {
			System.out.println("UNABLE TO INSTANTIATE VAETTR!");
			return null;
		}

		VAETTIR.put(uu, vaettr);
		vaettr.setData(this);
		markDirty();
		return uu;
	}

	public UUID getForVaettr(Vaettr vaettr) {
		if (VAETTIR.containsValue(vaettr)) {
			for (UUID uu : VAETTIR.keySet()) {
				if (VAETTIR.get(uu) == vaettr) {
					return uu;
				}
			}
		}
		return null;
	}

	public Vaettr getVaettr(UUID uu) {
		return VAETTIR.get(uu);
	}

	public Vaettr removeVaettr(UUID uu) {

		Vaettr v = VAETTIR.remove(uu);
		System.out.println("Removing vaettr " + v);
		v.setDead(true);
		markDirty();
		return v;
	}

	public Map<UUID, Vaettr> getTileVaettir() {
		Map<UUID, Vaettr> map = new HashMap<>();
		for (UUID uu : VAETTIR.keySet()) {
			if (VAETTIR.get(uu).hasTileEntity()) {
				map.put(uu, VAETTIR.get(uu));
			}
		}
		return map;
	}

	public Map<UUID, Vaettr> getEntityVaettir() {
		Map<UUID, Vaettr> map = new HashMap<>();
		for (UUID uu : VAETTIR.keySet()) {
			if (VAETTIR.get(uu).hasEntity()) {
				map.put(uu, VAETTIR.get(uu));
			}
		}
		return map;
	}

	public Map<UUID, Vaettr> getVanir() {
		Map<UUID, Vaettr> map = new HashMap<>();
		for (UUID uu : VAETTIR.keySet()) {
			if (!VAETTIR.get(uu).hasTileEntity() && !VAETTIR.get(uu).hasEntity()) {
				map.put(uu, VAETTIR.get(uu));
			}
		}
		return map;
	}

	public Map<UUID, Vaettr> getByName(String name) {
		Map<UUID, Vaettr> map = new HashMap<>();
		for (UUID uu : VAETTIR.keySet()) {
			if (!VAETTIR.get(uu).getName().equals(name)) {
				map.put(uu, VAETTIR.get(uu));
			}
		}
		return map;
	}

	public Map<UUID, Vaettr> getByType(VaettrType name) {
		Map<UUID, Vaettr> map = new HashMap<>();
		for (UUID uu : VAETTIR.keySet()) {
			if (!VAETTIR.get(uu).getType().equals(name)) {
				map.put(uu, VAETTIR.get(uu));
			}
		}
		return map;
	}

	public UUID removeVaettr(Vaettr vaet) {
		UUID uu = getForVaettr(vaet);
		if (uu != null) {
			System.out.println("Removing vaettr " + vaet);
			VAETTIR.remove(uu).setDead(true);
			markDirty();
			return uu;
		}
		return null;
	}

	@Override
	public void read(CompoundNBT nbt) {
		VAETTIR.clear();
		ListNBT vaettir = nbt.getList("Vaettir", NBT.TAG_COMPOUND);
		for (INBT tt : vaettir) {
			CompoundNBT tag = (CompoundNBT) tt;
			UUID vaetuu = UUID.fromString(tag.getString("ID"));
			CompoundNBT data = tag.getCompound("Data");
			Vaettr vaettr = new Vaettr(data);
			vaettr.setData(this);
			VAETTIR.put(vaetuu, vaettr);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT nbt = compound;
		ListNBT vaettir = new ListNBT();
		for (Entry<UUID, Vaettr> entry : VAETTIR.entrySet()) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString("ID", entry.getKey().toString());
			tag.put("Data", entry.getValue().serializeNBT());
		}
		nbt.put("Vaettir", vaettir);
		return nbt;
	}

	@SubscribeEvent
	public void tick(ServerTickEvent event) {
		boolean dirty = false;
		for (UUID uu : new ArrayList<>(VAETTIR.keySet())) {
			Vaettr vaettr = VAETTIR.get(uu);
			if (vaettr.isDead()) {
				vaettr.onDeath();
				this.VAETTIR.remove(getForVaettr(vaettr));
			} else {
				Thread ticker = new Thread(() -> vaettr.tick());
				ticker.start();
			}
			dirty = true;
		}
		if (dirty)
			markDirty();
	}

}
