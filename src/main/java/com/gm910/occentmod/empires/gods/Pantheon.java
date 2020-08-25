package com.gm910.occentmod.empires.gods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.empires.Empire;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

public class Pantheon implements IDynamicSerializable {

	private Deity head;
	private Empire empire;

	private List<Deity> gods = new ArrayList<Deity>();

	private Object2IntMap<DeityElement> elementToDeityMap = new Object2IntOpenHashMap<>();

	public Pantheon(Empire em, Dynamic<?> dyn) {
		this.empire = em;
		Map<DeityElement, Integer> map = dyn.get("deityelements")
				.asMap((e) -> DeityElement.get(new ResourceLocation(e.asString(""))), (e) -> e.asInt(0));
		elementToDeityMap.putAll(map);
		this.gods.addAll(dyn.get("deities").asList((d) -> new Deity(em, d)));
		head = new Deity(em, dyn.get("head").get().get());
	}

	public boolean isMonotheistic() {
		for (DeityElement d : elementToDeityMap.keySet()) {
			if (elementToDeityMap.getInt(d) != getIndex(head)) {
				return false;
			}
		}
		return true;
	}

	public int getIndex(Deity d) {
		return gods.indexOf(d);
	}

	public Deity getFromIndex(int index) {
		return gods.get(index);
	}

	public Pantheon(Empire em, Deity head, Set<Deity> others) {
		this.empire = em;
		this.head = head;
		gods = new ArrayList<>();
		Set<Deity> withoutHead = new HashSet<>(others);
		withoutHead.remove(head);
		gods.addAll(withoutHead);
		gods.add(0, head);
		for (Deity deity : gods) {
			Set<DeityElement> elements = deity.getElements();
			elements.forEach((e) -> elementToDeityMap.put(e, getIndex(deity)));
		}
		for (Deity deity : gods) {
			if (!elementToDeityMap.containsValue(this.getIndex(deity))) {
				this.gods.remove(deity);
			}
		}
	}

	public static Pantheon generatePantheon(Empire em) {
		ServerWorld world = em.getCenterWorld();
		ChunkPos pos = em.getCenter().getFirst();
		Set<DeityElement> viableElements = Sets.newHashSet(DeityElement.DEATH);
		Set<DeityElement> potentialElements = getPotentialElements(pos, world);
		potentialElements.forEach((e) -> {
			if (world.rand.nextBoolean()) {
				viableElements.add(e);
			}
		});
		int deityCount = 1 + world.rand.nextInt(viableElements.size() - 1);
		List<Set<DeityElement>> elsets = new ArrayList<>(deityCount);
		Set<Deity> deities = new HashSet<>();
		int listIndex = 0;
		for (DeityElement el : viableElements) {
			Set<DeityElement> sata = new HashSet<>();
			if (listIndex >= deityCount) {
				sata = elsets.stream().filter((e) -> e != null).findAny().get();
				sata.add(el);
				continue;
			}
			sata.add(el);
			elsets.set(listIndex, sata);
			listIndex++;
		}
		deities = elsets.stream().map((e) -> new Deity(em, em.getData().giveRandomCitizenName(), e))
				.collect(Collectors.toSet());
		return new Pantheon(em, deities.stream().findAny().get(), deities);
	}

	public static Set<DeityElement> getPotentialElements(ChunkPos pos, ServerWorld world) {
		Set<DeityElement> potentialElements = Sets.newHashSet(DeityElement.EARTH, DeityElement.SKY, DeityElement.WAR,
				DeityElement.ALCHEMY, DeityElement.PLANES);
		if (world.getBiome(pos.asBlockPos()).getDefaultTemperature() >= 1.75) {
			potentialElements.addAll(Sets.newHashSet(DeityElement.FIRE, DeityElement.SUN));
		}

		for (int x = -48; x <= 48; x++) {
			for (int z = -48; z <= 48; z++) {
				BlockPos bpos = new BlockPos(x, 100, z);
				if (world.getBiome(bpos).isHighHumidity() || world.getBiome(bpos).getCategory() == Biome.Category.BEACH
						|| world.getBiome(bpos).getCategory() == Biome.Category.OCEAN
						|| world.getBiome(bpos).getCategory() == Biome.Category.RIVER
						|| world.getBiome(bpos).getCategory() == Biome.Category.SWAMP) {
					potentialElements.add(DeityElement.WATER);
				}
				if (world.getBiome(bpos).isHighHumidity() || world.getBiome(bpos).getCategory() == Biome.Category.FOREST
						|| world.getBiome(bpos).getCategory() == Biome.Category.JUNGLE
						|| world.getBiome(bpos).getCategory() == Biome.Category.PLAINS
						|| world.getBiome(bpos).getCategory() == Biome.Category.SWAMP) {
					potentialElements
							.addAll(Sets.newHashSet(DeityElement.NATURE, DeityElement.CREATURES, DeityElement.LOVE));
				}
				if (world.getBiome(bpos).getCategory() == Biome.Category.SWAMP) {
					potentialElements.add(DeityElement.ELIXIR);
				}
			}
		}

		int lavacount = 0;
		for (int x = -48; x <= 48; x++) {
			for (int z = -48; z <= 48; z++) {
				for (int y = 50; y <= 256; y++) {
					BlockPos bpos = new BlockPos(x, y, z);
					if (world.getFluidState(bpos).getFluid() == Fluids.LAVA) {
						lavacount++;
					}
				}
			}
		}
		if (lavacount >= 12) {
			potentialElements.add(DeityElement.LAVA);
		}

		return potentialElements;
	}

	public Empire getEmpire() {
		return empire;
	}

	public Deity getHead() {
		return head;
	}

	public List<Deity> getDeities() {
		return Lists.newArrayList(gods);
	}

	public Deity getForElement(DeityElement e) {
		return this.getFromIndex(this.elementToDeityMap.getInt(e));
	}

	public Set<DeityElement> getElements(Deity e) {
		return elementToDeityMap.object2IntEntrySet().stream()
				.filter((entry) -> entry.getIntValue() == this.getIndex(e)).map((m) -> m.getKey())
				.collect(Collectors.toSet());
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T head = this.head.serialize(ops);
		T mapa = ops.createMap(elementToDeityMap.object2IntEntrySet().stream().map((entry) -> {
			return Pair.of(ops.createString(entry.getKey().getResource().toString()),
					ops.createInt(entry.getIntValue()));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		T deit = ops.createList(gods.stream().map((d) -> d.serialize(ops)));
		return ops.createMap(ImmutableMap.of(ops.createString("head"), head, ops.createString("deityelements"), mapa,
				ops.createString("deities"), deit));
	}

}
