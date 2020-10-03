package com.gm910.occentmod.empires;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gm910.occentmod.api.language.NamePhonemicHelper.PhonemeWord;
import com.gm910.occentmod.api.networking.messages.Networking;
import com.gm910.occentmod.api.util.GMHelper;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.api.util.ParallelSet;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.empires.EmpireDispute.DisputeType;
import com.gm910.occentmod.empires.gods.Pantheon;
import com.gm910.occentmod.init.DataInit;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Race;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;

public class Empire implements INBTSerializable<CompoundNBT> {

	private MinecraftServer server = null;

	Map<DimensionType, LongSet> chunks = new NonNullMap<>(() -> new LongOpenHashBigSet());

	Pair<ChunkPos, DimensionType> center = EMPTY_FLAG;

	private String centerStructureType;

	Set<UUID> citizens = new HashSet<>();

	UUID empireId;

	private EmpireData data;

	private Government government = new Government();

	private Object2DoubleMap<Race> raceWeights = new Object2DoubleOpenHashMap<>();

	private Race favoredRace = Race.MIXED;

	EmpireName name = EmpireName.EMPTY;

	Pantheon pantheon;

	public static final Pair<ChunkPos, DimensionType> EMPTY_FLAG = new Pair<>(new ChunkPos(0, 0),
			DimensionType.OVERWORLD);

	public Empire(EmpireData data, CompoundNBT nbt) {
		this.deserializeNBT(nbt);
		setData(data);
		setServer(data.getServer());
	}

	public Empire(EmpireData data, DimensionType centertype, ChunkPos center, String centerStructureType) {
		setData(data);
		setServer(data.getServer());
		setCenter(centertype, center);
	}

	public void setData(EmpireData data) {
		this.data = data;
	}

	public ServerWorld getCenterWorld() {
		return data.getServer().getWorld(center.getSecond());
	}

	public void initializeEmpire() {
		this.setName(data.giveRandomEmpireName());

		this.randomizeRaceWeights();

		this.pantheon = Pantheon.generatePantheon(this);
	}

	public Race chooseRandomRace(Random rand) {
		// Credit: https://stackoverflow.com/a/6737362
		return GMHelper.weightedRandom(raceWeights);
	}

	public void randomizeRaceWeights() {
		Race favored = Race.getAllIncludingMixed().stream().findAny().get();

		World world = getCenterWorld();

		List<Race> races = new ArrayList<>(Race.getRaces());
		if (favored != Race.MIXED) {
			races.add(0, races.remove(races.indexOf(favored)));
		}
		double outOfRaces = races.size();

		for (int i = 0; i < races.size(); i++) {
			double val = world.rand.nextDouble();
			if (favored != Race.MIXED) {
				if (i == 0) {
					val = world.rand.nextDouble() * (races.size() - 1);
				} else {
					val = world.rand.nextDouble() / (races.size() - 1);
				}
			}
			if (i == races.size() - 1) {
				val = outOfRaces;
			}
			this.raceWeights.put(races.get(i), val);
			outOfRaces -= val;
		}
	}

	public Race getFavoredRace() {
		return favoredRace;
	}

	public Chunk getChunk(DimensionType type, long chunkpos) {
		ChunkPos cpos = new ChunkPos(chunkpos);
		return server.getWorld(type).getChunk(cpos.x, cpos.z);
	}

	public boolean setId() {
		UUID uu = null;
		uu = UUID.randomUUID();
		this.empireId = uu;
		return true;
	}

	public UUID getEmpireId() {
		return empireId;
	}

	public Pantheon getPantheon() {
		return pantheon;
	}

	public void setPantheon(Pantheon pantheon) {
		this.pantheon = pantheon;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("Citizens", GMNBT.makeUUIDList(citizens));
		ListNBT list = new ListNBT();
		for (DimensionType type : chunks.keySet()) {
			if (this.chunks.get(type).isEmpty())
				continue;
			CompoundNBT tag = new CompoundNBT();
			tag.putInt("Dim", type.getId());
			tag.putLongArray("Chunks", this.chunks.get(type).toLongArray());
			list.add(tag);
		}
		nbt.put("Conquered", list);
		nbt.put("Government", this.government.serialize(NBTDynamicOps.INSTANCE));
		nbt.put("Pantheon", this.pantheon.serialize(NBTDynamicOps.INSTANCE));
		nbt.putString("Name", name.writeData());
		if (center != EMPTY_FLAG) {
			nbt.putInt("CenterDim", this.center.getSecond().getId());
			nbt.putLong("Center", this.center.getFirst().asLong());
		} else {
			nbt.putBoolean("CenterFlag", true);
		}
		nbt.putString("SType", this.centerStructureType);
		if (empireId == null) {
			boolean done = this.setId();
			if (!done) {
				throw new RuntimeException("Can't set empire id!");
			}
		}
		nbt.putUniqueId("ID", empireId);
		nbt.putInt("FavoredRace", favoredRace.id);
		nbt.put("RaceWeights", GMNBT.makeList(this.raceWeights.keySet(), (key) -> {
			CompoundNBT nbt1 = new CompoundNBT();
			nbt1.putInt("Key", key.getId());
			nbt1.putDouble("Weight", raceWeights.getDouble(key));
			return nbt1;
		}));
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.citizens = new HashSet<>(GMNBT.createUUIDList((ListNBT) nbt.get("Citizens")));

		this.chunks = GMNBT.createMap((ListNBT) nbt.get("Conquered"), (inbt) -> {
			CompoundNBT tag = (CompoundNBT) inbt;
			DimensionType type = DimensionType.getById(tag.getInt("Dim"));
			LongOpenHashBigSet set = new LongOpenHashBigSet(tag.getLongArray("Chunks"));
			return new Pair<>(type, set);
		});
		this.government.deserialize(GMNBT.makeDynamic(nbt.get("Government")));
		this.pantheon = new Pantheon(this, GMNBT.makeDynamic(nbt.get("Pantheon")));
		if (!nbt.getBoolean("CenterFlag")) {
			this.center = new Pair<>(new ChunkPos(nbt.getLong("Center")),
					DimensionType.getById(nbt.getInt("CenterDim")));
		} else {
			this.center = EMPTY_FLAG;
		}
		this.name = EmpireName.fromData(nbt.getString("Name"));
		this.empireId = nbt.getUniqueId("ID");
		this.centerStructureType = nbt.getString("SType");
		this.favoredRace = Race.fromId(nbt.getInt("FavoredRace"));
		this.raceWeights = new Object2DoubleOpenHashMap<Race>(
				GMNBT.createMap((ListNBT) nbt.get("RaceWeights"), (inbt) -> {
					CompoundNBT nbt1 = (CompoundNBT) inbt;
					return Pair.<Race, Double>of(Race.fromId(nbt1.getInt("Key")), nbt1.getDouble("Weight"));
				}));
	}

	public Set<Long> getChunkLongs(DimensionType type) {
		return chunks.get(type);
	}

	public Set<Chunk> getChunks(DimensionType type) {
		return chunks.get(type).stream()
				.<Chunk>map((ch) -> this.server.getWorld(type).getChunk(new ChunkPos(ch).x, new ChunkPos(ch).z))
				.collect(Collectors.toSet());
	}

	public Set<Chunk> getChunks() {
		Set<Chunk> chunks = new HashSet<>();
		for (DimensionType type : this.chunks.keySet()) {
			chunks.addAll(this.chunks.get(type).stream()
					.map((ch) -> this.server.getWorld(type).getChunk(new ChunkPos(ch).x, new ChunkPos(ch).z))
					.collect(Collectors.toSet()));
		}
		return chunks;
	}

	public Pair<ChunkPos, DimensionType> getCenter() {
		return center;
	}

	public void setCenter(DimensionType type, ChunkPos center) {
		this.center = new Pair<>(center, type);
		chunks.get(type).add(center.asLong());
	}

	public EmpireData getData() {
		return data == null ? EmpireData.get(this.server) : data;
	}

	public Set<ChunkPos> getChunkPositions(DimensionType type) {
		return new ParallelSet<>(ChunkPos.class, long.class, chunks.get(type), ChunkPos::new, ChunkPos::asLong);
	}

	public MinecraftServer getServer() {
		return server;
	}

	public EmpireName getName() {
		return name;
	}

	public PhonemeWord getSingleName() {
		return name.getRegularName();
	}

	public void setName(EmpireName name) {
		this.name = name;
	}

	public void setFullName(String name) {
		this.name = EmpireName.fromData(name);
	}

	public Empire setServer(MinecraftServer server) {
		this.server = server;
		return this;
	}

	public Government getGovernment() {
		return this.government;
	}

	public EmpireInfo createInfo() {
		return new EmpireInfo(this);
	}

	public Set<LivingEntity> getCitizenEntities() {
		return new ParallelSet<LivingEntity, UUID>(LivingEntity.class, UUID.class, this.citizens,
				(uu) -> (LivingEntity) ServerPos.getEntityFromUUID(uu, server), LivingEntity::getUniqueID);
	}

	public Set<UUID> getCitizens() {
		return citizens;
	}

	public boolean containsCitizen(DimensionType world) {
		for (UUID uu : this.citizens) {
			if (server.getWorld(world).getEntityByUuid(uu) != null) {
				return true;
			}
		}
		return false;
	}

	public void tick(WorldTickEvent event) {
		if (event.world.isRemote)
			return;
		if (chunks.get(event.world.dimension.getType()).isEmpty() && !containsCitizen(event.world.dimension.getType()))
			return;
		if (event.world.getGameTime() % 20 == 0) {
			Networking.sendToAll(new TaskSendEmpireToClient(this));
		}
	}

	public void blockUpdate(NeighborNotifyEvent event) {
		ServerWorld world = (ServerWorld) event.getWorld();
		if (this.chunks.get(world.dimension.getType()).isEmpty()) {
			return;
		}
		if (world.getDimension().getType() == center.getSecond()) {
			Chunk chunk = this.getChunk(center.getSecond(), center.getFirst().asLong());
			PointOfInterestManager manager = world.getPointOfInterestManager();
			Stream<PointOfInterest> pois = manager.getInChunk(DataInit.THRONE_POI.get().getPredicate(),
					center.getFirst(), PointOfInterestManager.Status.ANY);

			if (!pois.findAny().isPresent()) {
				this.center = EMPTY_FLAG;
			}
		}
	}

	public boolean isInEmpire(LivingEntity en) {
		return this.getCitizenEntities().contains(en);
	}

	public void addToEmpire(LivingEntity en) {
		this.citizens.add(en.getUniqueID());
	}

	public boolean isInEmpire(DimensionType world, BlockPos pos) {
		return isInEmpire(world, new ChunkPos(pos));
	}

	public boolean isInEmpire(DimensionType type, ChunkPos pos) {
		return chunks.get(type).contains(pos.asLong());
	}

	protected void addToEmpire(DimensionType world, ChunkPos pos) {
		chunks.get(world).add(pos.asLong());
	}

	public void removeFromEmpire(DimensionType world, ChunkPos chunk) {
		chunks.get(world).remove(chunk.asLong());
		if (chunks.get(world).isEmpty())
			chunks.remove(world);
	}

	public boolean canAddToEmpire(DimensionType world, ChunkPos pos) {
		for (Empire emp : this.getData()) {
			if (emp.isInEmpire(world, pos)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * If this position belongs to another empire, an empire dispute will be
	 * returned of type Conquered Territory
	 * 
	 * @param pos
	 * @return
	 */
	public EmpireDispute conquer(DimensionType type, ChunkPos pos) {
		EmpireData data = this.getData();
		Empire other = data.getEmpire(type, pos);
		if (other != null) {
			return new EmpireDispute(this, other, DisputeType.CONQUERED_TERRITORY);
		} else {
			this.addToEmpire(type, pos);
			return null;
		}
	}

	/*public void collapseEmpire() {
		getData().removeEmpire(this);
		this.chunks.clear();
		this.citizens.clear();
		this.leaders.clear();
	}*/

}
