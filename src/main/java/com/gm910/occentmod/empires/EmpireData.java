package com.gm910.occentmod.empires;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.empires.Empire.EmpireName;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.collect.Sets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EmpireData extends WorldSavedData implements Iterable<Empire> {

	public static final String NAME = OccultEntities.MODID + "_empires";

	private Set<EmpireName> availableNames = new HashSet<>();
	private Set<EmpireName> usedNames = new HashSet<>();

	private int namesIndex = 0;

	public static final ResourceLocation names = GMFiles.rl("empirenames/names.txt");

	public static final ResourceLocation structureTypeName = GMFiles.rl("empire_center");

	private MinecraftServer server;

	private List<Empire> empires;

	public EmpireData(String name) {
		super(name);
		MinecraftForge.EVENT_BUS.register(this);
		empires = new ArrayList<>();
	}

	public EmpireData() {
		this(NAME);
		loadNames();
	}

	public MinecraftServer getServer() {
		return server;
	}

	public List<Empire> getEmpires() {
		return empires;
	}

	public void addEmpire(Empire e) {
		if (e.getEmpireId() == null) {
			boolean set = e.setId();
			if (!set) {
				System.err.println("Unable to register empire!");
				return;
			}
		}
		this.empires.add(e.setServer(server));
		markDirty();
	}

	public Empire createNewEmpire(DimensionType dimension, ChunkPos center) {
		Empire emp = new Empire(this, dimension, center, structureTypeName.toString());
		emp.setName(this.giveRandomName());
		emp.setId();
		this.addEmpire(emp);
		return emp;
	}

	public void removeEmpire(Empire e) {
		this.empires.remove(e);
		markDirty();
	}

	public Empire getEmpire(DimensionType type, ChunkPos anyPos) {
		for (Empire e : this.empires) {
			if (e.isInEmpire(type, anyPos)) {
				return e;
			}
		}
		return null;
	}

	public Empire getEmpire(DimensionType t, BlockPos anyPos) {
		for (Empire e : this.empires) {
			if (e.isInEmpire(t, anyPos)) {
				return e;
			}
		}
		return null;
	}

	public Empire getEmpire(UUID uuid) {
		for (Empire e : this.empires) {
			if (e.getEmpireId() == null) {
				boolean set = e.setId();
				if (!set) {
					System.err.println("Mal-registered empire!");
					this.empires.remove(e);
				}
			} else if (e.getEmpireId().equals(uuid)) {
				return e;
			}
		}
		return null;
	}

	public EmpireData setServer(MinecraftServer world) {
		this.server = world;
		return this;
	}

	public static EmpireData get(MinecraftServer server) {
		DimensionSavedDataManager dimdat = server.getWorld(DimensionType.OVERWORLD).getSavedData();
		return dimdat.getOrCreate(() -> {
			return (new EmpireData()).setServer(server);
		}, NAME);
	}

	public static EmpireData get(ServerWorld world) {
		return get(world.getServer());
	}

	@Override
	public void read(CompoundNBT nbt) {
		empires = GMNBT.createList(nbt.getList("Empires", NBT.TAG_COMPOUND), (gettag) -> {

			return new Empire(this, (CompoundNBT) gettag);
		});
		this.usedNames = new HashSet<>(GMNBT.createList(nbt.getList("UsedNames", NBT.TAG_STRING), (inbt) -> {

			return EmpireName.of(((StringNBT) inbt).getString());
		}));
		this.availableNames = new HashSet<>(GMNBT.createList(nbt.getList("AvailableNames", NBT.TAG_STRING), (inbt) -> {

			return EmpireName.of(((StringNBT) inbt).getString());
		}));

		this.namesIndex = nbt.getInt("NamesIndex");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.put("Empires", GMNBT.makeList(empires, (empire) -> empire.serializeNBT()));
		nbt.put("UsedNames", GMNBT.makeList(usedNames, (str) -> StringNBT.valueOf(str.toString())));
		nbt.put("AvailableNames", GMNBT.makeList(availableNames, (str) -> StringNBT.valueOf(str.toString())));
		nbt.putInt("NamesIndex", namesIndex);
		return nbt;
	}

	public Empire getEmpire(LivingEntity entity) {
		for (Empire emp : empires) {
			if (emp.isInEmpire(entity)) {
				return emp;
			}
		}
		return null;
	}

	public static Set<EmpireData> getAllEmpireData(MinecraftServer server) {
		Set<EmpireData> dat = Sets.newHashSet();
		for (ServerWorld world : server.getWorlds()) {
			dat.add(get(world));
		}
		return dat;
	}

	public Set<Empire> getEmpiresInWorld(DimensionType dimtype) {
		Set<Empire> empsinworld = Sets.newHashSet();
		for (Empire emp : empires) {
			if (!emp.getChunkPositions(dimtype).isEmpty()) {
				empsinworld.add(emp);
			}
		}
		return empsinworld;
	}

	@SubscribeEvent
	public void tick(WorldTickEvent event) {
		if (event.world.isRemote || event.world.getGameTime() % 20 != 0
				|| this.getEmpiresInWorld(event.world.dimension.getType()).isEmpty()) {
			return;
		}
		for (Empire empire : empires) {
			empire.tick(event);
		}
		this.usedNames = this.empires.stream().map((em) -> em.getName()).collect(Collectors.toSet());
		this.availableNames.removeAll(usedNames);
	}

	@Override
	public Iterator<Empire> iterator() {
		// TODO Auto-generated method stub
		return this.empires.iterator();
	}

	public void loadNames() {
		String[] resourceraw = GMFiles.getLines(names, (str) -> {
			return !str.trim().startsWith("#") && !str.trim().startsWith("//") && !str.trim().isEmpty();
		});

		this.availableNames = Sets.newHashSet(resourceraw).stream().map((str) -> EmpireName.of(str))
				.collect(Collectors.toSet());
		this.usedNames = Sets.newHashSet();
	}

	public EmpireName giveRandomName() {
		if (this.availableNames.isEmpty()) {
			this.availableNames.addAll(this.usedNames.stream().map((name) -> {
				return name.addJunk(++namesIndex);
			}).collect(Collectors.toSet()));
		}
		return availableNames.stream().findAny().orElse(EmpireName.EMPTY);
	}

}
