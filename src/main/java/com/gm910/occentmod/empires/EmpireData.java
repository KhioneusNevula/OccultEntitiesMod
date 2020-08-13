package com.gm910.occentmod.empires;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.entities.citizen.mind_and_traits.BodyForm;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
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

	private Set<String> usedCitizenNames = new HashSet<>();

	private Map<BodyForm, Set<UUID>> formEntityCorrespondences = new NonNullMap<>(() -> Sets.newHashSet());

	private Map<UUID, BodyForm> trueForms = new HashMap<>();

	private int namesIndex = 0;

	public static final ResourceLocation names = GMFiles.rl("empirenames/names.txt");
	public static final ResourceLocation cit_names = GMFiles.rl("citizennames/names.txt");

	public static final ResourceLocation structureTypeName = GMFiles.rl("empire_center");

	private MinecraftServer server;

	private List<Empire> empires;

	private HashSet<String> availableCitizenNames;

	public EmpireData(String name) {
		super(name);
		MinecraftForge.EVENT_BUS.register(this);
		empires = new ArrayList<>();
	}

	public EmpireData() {
		this(NAME);
		loadNames();
		loadCitizenNames();
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
		emp.setId();
		this.addEmpire(emp);
		emp.initializeEmpire();
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

	public Set<Empire> getInRadius(DimensionType type, BlockPos anyPos, double maxRadius) {
		Set<Empire> emps = this.getEmpiresInWorld(type);

		for (Empire empire : emps) {
			boolean cont = false;
			for (ChunkPos pos : empire.getChunkPositions(type)) {
				for (int x = 0; x < 16; x++) {
					for (int y = 0; y < 256; y++) {
						for (int z = 0; z < 16; z++) {
							BlockPos pos1 = new BlockPos(pos.getXStart(), 0, pos.getZStart()).add(x, y, z);
							if (pos1.withinDistance(anyPos, maxRadius)) {
								emps.add(empire);
								cont = true;
								continue;
							}
						}
						if (cont)
							continue;
					}
					if (cont)
						continue;
				}
				if (cont)
					continue;
			}
			if (cont)
				continue;
		}
		return emps;
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

		this.usedCitizenNames = new HashSet<>(
				GMNBT.<INBT, String>createList((ListNBT) nbt.get("UsedCNames"), (n) -> ((StringNBT) n).getString()));
		this.availableCitizenNames = new HashSet<>(
				GMNBT.<INBT, String>createList((ListNBT) nbt.get("AvCNames"), (n) -> ((StringNBT) n).getString()));
		this.availableNames = new HashSet<>(GMNBT.createList(nbt.getList("AvailableNames", NBT.TAG_STRING), (inbt) -> {

			return EmpireName.of(((StringNBT) inbt).getString());
		}));

		this.formEntityCorrespondences = new NonNullMap<>(() -> Sets.newHashSet());
		((NonNullMap<BodyForm, Set<UUID>>) formEntityCorrespondences)
				.setAs(GMNBT.createMap((ListNBT) nbt.get("FormEntities"), (ta) -> {
					CompoundNBT tag = (CompoundNBT) ta;
					return Pair.of(new BodyForm(tag.getUniqueId("Id")),
							new HashSet<>(((ListNBT) tag.get("Entities")).stream()
									.<UUID>map((in) -> UUID.fromString(((StringNBT) in).getString()))
									.collect(Collectors.toSet())));
				}));

		this.trueForms = GMNBT.createMap((ListNBT) nbt.get("TrueForms"), (i) -> {
			return Pair.of(((CompoundNBT) i).getUniqueId("Id"), new BodyForm(((CompoundNBT) i).getUniqueId("Form")));
		});

		this.namesIndex = nbt.getInt("NamesIndex");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.put("Empires", GMNBT.makeList(empires, (empire) -> empire.serializeNBT()));
		nbt.put("UsedNames", GMNBT.makeList(usedNames, (str) -> StringNBT.valueOf(str.toString())));
		nbt.put("AvailableNames", GMNBT.makeList(availableNames, (str) -> StringNBT.valueOf(str.toString())));
		nbt.putInt("NamesIndex", namesIndex);
		nbt.put("FormEntities", GMNBT.makeList(formEntityCorrespondences.entrySet(), (entry) -> {
			CompoundNBT get = new CompoundNBT();
			get.putUniqueId("Id", entry.getKey().getFormId());
			get.put("Entities", GMNBT.makeList(entry.getValue(), (s) -> StringNBT.valueOf(s.toString())));
			return get;
		}));
		nbt.put("TrueForms", GMNBT.makeList(trueForms.entrySet(), (entry) -> {
			CompoundNBT get = new CompoundNBT();
			get.putUniqueId("Id", entry.getKey());
			get.putUniqueId("Form", entry.getValue().getFormId());
			return get;
		}));
		nbt.put("UsedCNames", GMNBT.makeList(this.usedCitizenNames, (in) -> StringNBT.valueOf(in)));
		nbt.put("AvCNames", GMNBT.makeList(this.availableCitizenNames, (in) -> StringNBT.valueOf(in)));
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

	public void loadCitizenNames() {
		String[] resourceraw = GMFiles.getLines(cit_names, (str) -> {
			return !str.trim().startsWith("#") && !str.trim().startsWith("//") && !str.trim().isEmpty();
		});

		this.availableCitizenNames = Sets.newHashSet(resourceraw);
		this.usedCitizenNames = Sets.newHashSet();
	}

	public EmpireName giveRandomEmpireName() {
		if (this.availableNames.isEmpty()) {
			this.availableNames.addAll(this.usedNames.stream().map((name) -> {
				return name.addJunk(++namesIndex);
			}).collect(Collectors.toSet()));
		}
		EmpireName n = availableNames.stream().findAny().orElse(EmpireName.EMPTY);
		this.usedNames.add(n);
		return n;
	}

	public String giveRandomCitizenName() {

		if (this.availableCitizenNames.isEmpty()) {
			List<String> namese = new ArrayList<>(usedCitizenNames);
			List<String> names = new ArrayList<>(usedCitizenNames.stream()
					.flatMap((s) -> Lists.newArrayList(s.split("-")).stream()).collect(Collectors.toSet()));
			for (int i = 0; i < namese.size(); i++) {
				for (int u = 0; u < names.size(); u++) {
					availableCitizenNames.add(namese.get(i) + "-" + names.get(u));
				}
			}
		}

		String n = availableCitizenNames.stream().findAny().orElse("");
		this.usedCitizenNames.add(n);
		return n;
	}

	public BodyForm birthBodyForm(UUID entity) {
		BodyForm form = new BodyForm();
		this.trueForms.put(entity, form);
		return form;
	}

	public void reincarnateTrueForm(UUID entity, BodyForm newForm) {
		this.trueForms.put(entity, newForm);
	}

	public boolean doesMoreThanOneEntityHaveForm(BodyForm form) {
		return formEntityCorrespondences.get(form).size() > 1
				|| formEntityCorrespondences.get(form).size() > 0 && trueForms.containsValue(form);
	}

	public void changeEntityForm(UUID entity, BodyForm other) {
		this.formEntityCorrespondences.get(other).add(entity);
	}

	public void revertEntityForm(UUID entity) {
		for (BodyForm form : this.formEntityCorrespondences.keySet()) {
			if (formEntityCorrespondences.get(form).contains(entity)) {
				formEntityCorrespondences.get(form).remove(entity);
			}
		}
	}

}
