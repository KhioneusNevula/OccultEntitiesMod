package com.gm910.occentmod.empires;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.api.language.NamePhonemicHelper;
import com.gm910.occentmod.api.language.NamePhonemicHelper.PhonemeWord;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.empires.gods.Deity;
import com.gm910.occentmod.empires.gods.Pantheon;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity.DynamicSapientIdentity;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.collect.Sets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
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

	private Set<EmpireName> usedNames = new HashSet<>();

	private String[] endings;

	private Set<PhonemeWord> usedCitizenNames = new HashSet<>();

	private Set<DynamicSapientIdentity> usedIdentities = new HashSet<>();

	private int namesIndex = 0;

	public static final ResourceLocation namesAndEndings = GMFiles.rl("empirenames/names.txt");

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
		// loadCitizenNames();
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

	public Set<Pantheon> getPantheons() {
		Set<Pantheon> pantheons = new HashSet<>();
		for (Empire e : this.empires) {
			pantheons.add(e.getPantheon());
		}
		return pantheons;
	}

	public Set<Deity> getAllDeities() {
		Set<Deity> deities = this.getPantheons().stream().flatMap((pan) -> pan.getDeities().stream())
				.collect(Collectors.toSet());
		return deities;
	}

	public Deity getDeityById(UUID id) {
		return this.getAllDeities().stream().filter((e) -> e.getUniqueID().equals(id)).findAny().orElse(null);
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

			return EmpireName.fromData(((StringNBT) inbt).getString());
		}));

		GMNBT.createList((ListNBT) nbt.get("UsedIdentities"), (inbt) -> {

			return new DynamicSapientIdentity(GMNBT.makeDynamic(inbt),
					this.getServer().getWorld(DimensionType.OVERWORLD));
		});

		this.usedCitizenNames = new HashSet<>(GMNBT.<INBT, PhonemeWord>createList((ListNBT) nbt.get("UsedCNames"),
				(n) -> new PhonemeWord(GMNBT.makeDynamic(n))));

		this.namesIndex = nbt.getInt("NamesIndex");
	}

	public Set<DynamicSapientIdentity> getUsedIdentities() {
		return usedIdentities;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.put("Empires", GMNBT.makeList(empires, (empire) -> empire.serializeNBT()));
		nbt.put("UsedNames", GMNBT.makeList(usedNames, (str) -> StringNBT.valueOf(str.writeData())));
		nbt.putInt("NamesIndex", namesIndex);
		nbt.put("UsedIdentities", GMNBT.makeList(this.usedIdentities, (id) -> {

			return id.serialize(NBTDynamicOps.INSTANCE);
		}));
		nbt.put("UsedCNames", GMNBT.makeList(this.usedCitizenNames, (in) -> in.serialize(NBTDynamicOps.INSTANCE)));
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
		if (event.world.isRemote)
			return;
		if (this.getEmpiresInWorld(event.world.dimension.getType()).isEmpty()) {
			return;
		}
		for (Deity deity : this.getAllDeities()) {
			deity.tick();
		}
		if (event.world.getGameTime() % 20 != 0) {
			return;
		}
		for (Empire empire : empires) {
			empire.tick(event);
		}
		this.usedNames = this.empires.stream().map((em) -> em.getName()).collect(Collectors.toSet());
	}

	@Override
	public Iterator<Empire> iterator() {
		// TODO Auto-generated method stub
		return this.empires.iterator();
	}

	public String[] loadEndings() {
		String[] phonicNames = GMFiles.getLines(namesAndEndings, (str) -> {
			return str.trim().startsWith("$");
		});
		return phonicNames;
	}

	public void loadNames() {
		/*String[] resourceraw = GMFiles.getLines(names, (str) -> {
			return !str.trim().startsWith("#") && !str.trim().startsWith("$") && !str.trim().startsWith("//")
					&& !str.trim().isEmpty();
		});*/
		endings = GMFiles.getLines(namesAndEndings, (str) -> {
			return str.trim().startsWith("$");
		});

		this.usedNames = Sets.newHashSet();
	}

	public EmpireName giveRandomEmpireName() {
		if (endings.length == 0) {
			throw new ResourceLocationException("File " + namesAndEndings + " with EmpireNameEndings not found!");
		}
		EmpireName nom = null;
		int toler1 = 0;
		boolean done1 = false;
		while (!done1 || toler1 > 20) {
			EmpireNameEnding correct = null;
			PhonemeWord generated = NamePhonemicHelper.generateName(new Random());
			int toler = 0;
			boolean done = false;
			while (correct == null ? true : !done || toler > 20) {
				correct = new EmpireNameEnding(Sets.newHashSet(endings).stream().findAny().get());
				toler++;
				done = correct.matchesEnding(generated.getEnding());
			}
			toler1++;
			if (!done) {
				continue;
			}
			nom = EmpireName.of(generated, correct);
			done1 = !this.usedNames.contains(nom);
		}
		if (!done1) {
			throw new IllegalStateException("Name generator timed out!");
		}
		this.usedNames.add(nom);
		return nom;
	}

	public PhonemeWord giveRandomCitizenName() {

		Set<PhonemeWord> names = new HashSet<>(this.usedCitizenNames);
		PhonemeWord nomb = null;
		boolean hasName = false;
		for (int tolerance = 0; tolerance < 50 && !hasName; tolerance++) {
			nomb = NamePhonemicHelper.generateName(new Random());
			if (!names.contains(nomb)) {
				hasName = true;
			}
		}
		Set<PhonemeWord> newNames = new HashSet<>();
		if (!hasName) {
			for (PhonemeWord word : this.usedCitizenNames) {
				for (PhonemeWord word2 : this.usedCitizenNames) {
					newNames.add(word.add(NamePhonemicHelper.getFrom("-")).concat(word2));
				}
			}
			nomb = newNames.stream().findAny().get();
		}
		this.usedCitizenNames.add(nomb);
		return nomb;
	}

	public DynamicSapientIdentity get(LivingEntity e) {
		return this.usedIdentities.stream().filter((m) -> m.getTrueId().equals(e.getUniqueID())).findAny().orElse(null);
	}

}
