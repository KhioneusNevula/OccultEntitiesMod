package com.gm910.occentmod.empires;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.longs.LongOpenHashBigSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;

public class EmpireInfo implements IDynamicSerializable {

	public static Set<EmpireInfo> clientSideEmpireInfo = new HashSet<>();

	private Map<DimensionType, LongSet> chunks = new NonNullMap<>(() -> new LongOpenHashBigSet());

	private Pair<ChunkPos, DimensionType> center = Empire.EMPTY_FLAG;
	private Set<SapientIdentity> citizens = new HashSet<>();

	private UUID empireId;
	private Government government = new Government();

	private EmpireName name = EmpireName.EMPTY;

	private Set<SapientIdentity> deities = new HashSet<>();
	private SapientIdentity headDeity;

	public EmpireInfo(Empire empire) {
		this.chunks.putAll(empire.chunks);
		this.center = empire.center;
		this.citizens = empire.citizens.stream()
				.filter((e) -> ServerPos.getEntityFromUUID(e, empire.getServer()) != null).map((e) -> SapientInfo
						.get((LivingEntity) ServerPos.getEntityFromUUID(e, empire.getServer())).getIdentity())
				.collect(Collectors.toSet());
		this.empireId = empire.empireId;
		this.government = empire.getGovernment().copy();
		this.name = empire.getName();
		this.deities = empire.pantheon.getDeities().stream().map((d) -> SapientInfo.get(d).getIdentity())
				.collect(Collectors.toSet());
		this.headDeity = Optional.ofNullable(empire.pantheon.getHead()).map((m) -> SapientInfo.get(m).getIdentity())
				.orElse(null);

	}

	public EmpireInfo() {
	}

	public UUID getEmpireId() {
		return empireId;
	}

	public Pair<ChunkPos, DimensionType> getCenter() {
		return center;
	}

	public Map<DimensionType, LongSet> getChunks() {
		return chunks;
	}

	public Set<ChunkPos> getChunksInDim(DimensionType type) {
		return this.chunks.get(type).stream().map((e) -> new ChunkPos(e)).collect(Collectors.toSet());
	}

	public Set<SapientIdentity> getCitizens() {
		return citizens;
	}

	public Set<SapientIdentity> getDeities() {
		return deities;
	}

	public Government getGovernment() {
		return government;
	}

	public SapientIdentity getHeadDeity() {
		return headDeity;
	}

	public EmpireName getName() {
		return name;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Map<T, T> map = new HashMap<>();
		map.put(ops.createString("chunks"),
				ops.createMap(chunks.entrySet().stream()
						.map((entry) -> Pair.of(ops.createInt(entry.getKey().getId()),
								ops.createLongList(entry.getValue().stream().mapToLong((l) -> l))))
						.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
		map.put(ops.createString("center"), ops.createMap(ImmutableMap.of(ops.createLong(center.getFirst().asLong()),
				ops.createInt(center.getSecond().getId()))));
		map.put(ops.createString("citizens"), ops.createList(citizens.stream().map((e) -> e.serialize(ops))));
		map.put(ops.createString("id"), ops.createString(this.empireId.toString()));
		map.put(ops.createString("government"), this.government.serialize(ops));
		map.put(ops.createString("name"), ops.createString(this.name.writeData()));
		map.put(ops.createString("deities"), ops.createList(this.deities.stream().map((e) -> e.serialize(ops))));
		map.put(ops.createString("headDeity"), headDeity.serialize(ops));
		return ops.createMap(map);
	}

	public <T> EmpireInfo deserialize(Dynamic<T> dyn) {
		this.chunks.putAll(dyn.get("chunks").asMap((d) -> DimensionType.getById(d.asInt(0)), (d) -> {
			LongSet set1 = new LongOpenHashSet();
			set1.addAll(d.asStream().map((e) -> e.asLong(0)).collect(Collectors.toSet()));

			return set1;
		}));
		this.center = dyn.get("center")
				.asMap((e) -> new ChunkPos(e.asLong(0)), (e) -> DimensionType.getById(e.asInt(0))).entrySet().stream()
				.findFirst().map((m) -> Pair.of(m.getKey(), m.getValue())).get();

		this.citizens
				.addAll(dyn.get("citizens").asStream().map((e) -> new SapientIdentity(e)).collect(Collectors.toSet()));
		this.empireId = UUID.fromString(dyn.get("id").asString(""));
		this.government.deserialize(dyn.get("government").get().get());
		this.name = EmpireName.fromData(dyn.get("name").asString(""));
		this.deities.addAll(dyn.asList((e) -> new SapientIdentity(e)));
		this.headDeity = new SapientIdentity(dyn.get("headDeity").get().get());
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return ((EmpireInfo) obj).serialize(NBTDynamicOps.INSTANCE).equals(this.serialize(NBTDynamicOps.INSTANCE));
	}

	public EmpireInfo copy() {
		return new EmpireInfo()
				.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, this.serialize(NBTDynamicOps.INSTANCE)));
	}

}
