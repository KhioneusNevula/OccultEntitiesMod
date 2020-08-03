package com.gm910.occentmod.empires;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gm910.occentmod.api.util.EnglishNumberToWords;
import com.gm910.occentmod.api.util.GMNBT;
import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.api.util.ParallelSet;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.empires.EmpireDispute.DisputeType;
import com.gm910.occentmod.init.AIInit;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;

public class Empire implements INBTSerializable<CompoundNBT> {

	private MinecraftServer server = null;

	private Map<DimensionType, LongSet> chunks = new NonNullMap<>(() -> new LongOpenHashBigSet());

	private Pair<ChunkPos, DimensionType> center = EMPTY_FLAG;

	private String centerStructureType;

	private Set<UUID> citizens = new HashSet<>();

	private UUID empireId;

	private EmpireData data;

	private EnumMap<LeaderType, UUID> leaders = new EnumMap<>(LeaderType.class);

	private EmpireName name = EmpireName.EMPTY;

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

	public Chunk getChunk(DimensionType type, long chunkpos) {
		ChunkPos cpos = new ChunkPos(chunkpos);
		return server.getWorld(type).getChunk(cpos.x, cpos.z);
	}

	public boolean setId() {
		UUID uu = null;
		int toler = 50;
		for (; toler >= 0 && (toler < 20 && data.getEmpire(uu) == null); toler--) {
			uu = UUID.randomUUID();
		}
		if (toler < 0 || uu == null) {
			return false;
		} else {
			this.empireId = uu;
			return true;
		}
	}

	public UUID getEmpireId() {
		return empireId;
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
		nbt.put("Leaders", GMNBT.makeList(leaders.keySet(), (type) -> {
			CompoundNBT subtag = new CompoundNBT();
			subtag.putString("Type", type.name());
			if (leaders.get(type) != null)
				subtag.putUniqueId("ID", leaders.get(type));
			return subtag;
		}));
		nbt.putString("Name", name.toString());
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
		this.leaders = new EnumMap<>(GMNBT.createMap((ListNBT) nbt.get("Leaders"), (inbt) -> {
			CompoundNBT tag = (CompoundNBT) inbt;
			LeaderType lead = LeaderType.valueOf(tag.getString("Type"));
			UUID uu = null;
			if (tag.contains("ID")) {
				uu = tag.getUniqueId("ID");
			}
			return new Pair<>(lead, uu);
		}));
		if (!nbt.getBoolean("CenterFlag")) {
			this.center = new Pair<>(new ChunkPos(nbt.getLong("Center")),
					DimensionType.getById(nbt.getInt("CenterDim")));
		} else {
			this.center = EMPTY_FLAG;
		}
		this.name = EmpireName.of(nbt.getString("Name"));
		this.empireId = nbt.getUniqueId("ID");
		this.centerStructureType = nbt.getString("SType");
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
		return new ParallelSet<>(chunks.get(type), ChunkPos::new, ChunkPos::asLong);
	}

	public MinecraftServer getServer() {
		return server;
	}

	public EmpireName getName() {
		return name;
	}

	public String getSingleName() {
		return name.getName();
	}

	public void setSingleName(String name) {
		this.name = this.name.withName(name);
	}

	public void setName(EmpireName name) {
		this.name = name;
	}

	public void setFullName(String name) {
		this.name = EmpireName.of(name);
	}

	public Empire setServer(MinecraftServer server) {
		this.server = server;
		return this;
	}

	public LivingEntity getLeader(LeaderType type) {
		return this.leaders.get(type) != null
				? (LivingEntity) ServerPos.getEntityFromUUID(this.leaders.get(type), server)
				: null;
	}

	public Set<LivingEntity> getCitizenEntities() {
		return new ParallelSet<LivingEntity, UUID>(this.citizens,
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
	}

	public void blockUpdate(NeighborNotifyEvent event) {
		ServerWorld world = (ServerWorld) event.getWorld();
		if (this.chunks.get(world.dimension.getType()).isEmpty()) {
			return;
		}
		if (world.getDimension().getType() == center.getSecond()) {
			Chunk chunk = this.getChunk(center.getSecond(), center.getFirst().asLong());
			PointOfInterestManager manager = world.getPointOfInterestManager();
			Stream<PointOfInterest> pois = manager.getInChunk(AIInit.THRONE_POI.get().getPredicate(), center.getFirst(),
					PointOfInterestManager.Status.ANY);

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

	public void collapseEmpire() {
		getData().removeEmpire(this);
		this.chunks.clear();
		this.citizens.clear();
		this.leaders.clear();
	}

	public static enum LeaderType {
		RULER, ARCHMAGE, ARCHBISHOP, PENDRAGON
	}

	/**
	 * NAME, ADJECTIVE, DEMONYM, DEMONYMPLURAL
	 * 
	 * @author borah
	 *
	 */
	public static class EmpireName {

		public static EmpireName EMPTY = new EmpireName("", "", "", "");

		private final String[] names;
		private final String[] demonyms;
		private final String[] demonymPlurals;
		private final String[] adjectives;

		public EmpireName(String name, String adjective, String demonym, String demonymPlural) {
			this(new String[] { name }, new String[] { adjective }, new String[] { demonym },
					new String[] { demonymPlural });
		}

		/**
		 * NAME, ADJECTIVE, DEMONYM, DEMONYMPLURAL
		 * 
		 * @author borah
		 *
		 */
		public EmpireName(String[] name, String[] adjective, String[] demonym, String[] demonymPlural) {
			this.names = name;
			this.adjectives = adjective;
			this.demonyms = demonym;
			this.demonymPlurals = demonymPlural;
		}

		public String[] getNames() {
			return names;
		}

		public String[] getAdjectives() {
			return adjectives;
		}

		public String[] getDemonyms() {
			return demonyms;
		}

		public String[] getDemonymPlurals() {
			return demonymPlurals;
		}

		public String getAdjective(int index) {
			return adjectives[index];
		}

		public String getName(int index) {
			return names[index];
		}

		public String getDemonym(int index) {
			return demonyms[index];
		}

		public String getDemonymPlural(int index) {
			return demonymPlurals[index];
		}

		public String getAdjective() {
			return adjectives.length > 0 ? adjectives[new Random().nextInt(adjectives.length)] : "";
		}

		public String getName() {
			return names.length > 0 ? names[new Random().nextInt(names.length)] : "";
		}

		public String getDemonym() {
			return demonyms.length > 0 ? demonyms[new Random().nextInt(demonyms.length)] : "";
		}

		public String getDemonymPlural() {
			return demonymPlurals.length > 0 ? demonymPlurals[new Random().nextInt(demonymPlurals.length)] : "";
		}

		public static EmpireName of(String nameCombined) {
			String[] parts = nameCombined.split(",");
			for (int i = 0; i < parts.length; i++) {
				parts[i] = parts[i].trim();
			}
			if (parts.length != 4) {
				return EmpireName.EMPTY;
			}
			String[] names = parts[0].split("/");
			String[] adjs = parts[1].split("/");
			String[] demonyms = parts[2].split("/");
			String[] demoplurs = parts[3].split("/");
			return new EmpireName(names, adjs, demonyms, demoplurs);
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return String.join("/", names) + "," + String.join("/", adjectives) + "," + String.join("/", demonyms) + ","
					+ String.join("/", demonymPlurals);
		}

		public EmpireName withName(String[] name) {
			return new EmpireName(name, adjectives, demonyms, demonymPlurals);
		}

		public EmpireName withAdjective(String[] adjective) {
			return new EmpireName(names, adjective, demonyms, demonymPlurals);
		}

		public EmpireName withDemonym(String[] demonym) {
			return new EmpireName(names, adjectives, demonym, demonymPlurals);
		}

		public EmpireName withDemonymPlural(String[] demonymPlural) {
			return new EmpireName(names, adjectives, demonyms, demonymPlural);
		}

		public EmpireName withName(String name) {
			return new EmpireName(new String[] { name }, adjectives, demonyms, demonymPlurals);
		}

		public EmpireName withAdjective(String adjective) {
			return new EmpireName(names, new String[] { adjective }, demonyms, demonymPlurals);
		}

		public EmpireName withDemonym(String demonym) {
			return new EmpireName(names, adjectives, new String[] { demonym }, demonymPlurals);
		}

		public EmpireName withDemonymPlural(String demonymPlural) {
			return new EmpireName(names, adjectives, demonyms, new String[] { demonymPlural });
		}

		public EmpireName addJunk(int index) {
			Function<String, String> func = (str) -> {
				String ord = EnglishNumberToWords.fullOrdinal(Locale.US, index);
				return Character.toUpperCase(ord.charAt(0)) + ord.substring(1) + "-" + str;
			};
			return this
					.withAdjective(Sets.newHashSet(this.adjectives).stream().map(func).collect(Collectors.toSet())
							.toArray(new String[0]))
					.withName(Sets.newHashSet(this.names).stream().map(func).collect(Collectors.toSet())
							.toArray(new String[0]))
					.withDemonym(Sets.newHashSet(this.demonyms).stream().map(func).collect(Collectors.toSet())
							.toArray(new String[0]))
					.withDemonymPlural(Sets.newHashSet(this.demonymPlurals).stream().map(func)
							.collect(Collectors.toSet()).toArray(new String[0]));
		}

		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return this.toString().equals(obj.toString());
		}

	}

}
