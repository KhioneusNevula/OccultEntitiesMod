package com.gm910.occentmod.entities.citizen.mind_and_traits.genetics;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gm910.occentmod.OccultEntities;
import com.gm910.occentmod.entities.citizen.mind_and_traits.genetics.genetype.GeneType;
import com.gm910.occentmod.util.GMFiles;
import com.google.common.collect.Sets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unchecked")
public class Race {

	public static final Race HUMAN = Race.create("human").setGeneTypes(GeneType.MOVEMENT_SPEED);
	public static final Race FAIRY = Race.create("fairy").setGeneTypes(GeneType.FAIRY_ANIMAL_EFFECT,
			GeneType.FAIRY_EARS, GeneType.FLICK, GeneType.FAIRY_PLANT_EFFECT, GeneType.FLICK_SIGHT,
			GeneType.MOVEMENT_SPEED, GeneType.WINGS);
	public static final Race TROLL = Race.create("troll").setGeneTypes(GeneType.DAMAGE, GeneType.HORNS,
			GeneType.MINE_TIER, GeneType.SHOCKWAVE, GeneType.MOVEMENT_SPEED);
	public static final Race DRACONIAN = Race.create("draconian").setGeneTypes(GeneType.DAMAGE, GeneType.DRAGON_FIRE,
			GeneType.DRAGON_TAIL, GeneType.HORNS, GeneType.MOVEMENT_SPEED, GeneType.WEREDRAGON, GeneType.WINGS);
	public static final Race MIXED = new Race(GMFiles.rl("mixed"));

	public static final Set<Race> TYPES = new HashSet<>();

	/**
	 * All races, including spirit and transformed
	 */
	public static final Set<Race> ALL_TYPES = new HashSet<>();

	private static int nextUseId = 1;

	public final ResourceLocation regName;

	public final int id;

	private Set<GeneType<?, ? extends LivingEntity>> geneTypes = new HashSet<>();

	private Race(ResourceLocation regName) {
		this.regName = regName;
		this.id = nextUseId++;
		ALL_TYPES.add(this);
	}

	public Race additionalTasks() {

		TYPES.add(this);
		return this;
	}

	public HashSet<GeneType<?, ? extends LivingEntity>> getGeneTypes() {
		return Sets.newHashSet(geneTypes);
	}

	public Race setGeneTypes(GeneType<?, ? extends LivingEntity>... type) {
		this.geneTypes.addAll(Sets.newHashSet(type));
		return this;
	}

	public ResourceLocation getRegName() {
		return regName;
	}

	public int getId() {
		return id;
	}

	public static int getNextUseId() {
		return nextUseId;
	}

	public static Race create(ResourceLocation name) {
		return new Race(name).additionalTasks();
	}

	private static Race create(String name) {
		return new Race(new ResourceLocation(OccultEntities.MODID, name)).additionalTasks();
	}

	public static Race fromName(ResourceLocation loc) {
		for (Race type : TYPES) {
			if (type.regName.equals(loc)) {
				return type;
			}
		}
		return MIXED.getRegName().equals(loc) ? MIXED : null;
	}

	public static Race fromId(int loc) {
		for (Race type : TYPES) {
			if (type.id == loc) {
				return type;
			}
		}
		return MIXED.getId() == loc ? MIXED : null;
	}

	public static Race fromId(String loc) {
		return fromId(Integer.parseInt(loc));
	}

	@Override
	public boolean equals(Object o) {
		return regName.equals(((Race) o).regName);
	}

	public static Set<Race> getRaces() {
		return new HashSet<>(TYPES);
	}

	public static Set<Race> getAllIncludingMixed() {
		Set<Race> set = new HashSet<>(TYPES);
		set.add(MIXED);
		return set;
	}

	/*public static enum PhysicalTrait implements IDynamicSerializable {
		DRAGON_HORNS, TROLL_HORNS, DRAGON_WINGS, FAIRY_WINGS, FAIRY_EARS, DRAGON_TAIL;
	
		@Override
		public <T> T serialize(DynamicOps<T> o) {
	
			return o.createString(this.name());
		}
	
		public static PhysicalTrait deserialize(Dynamic<?> dyn) {
			return valueOf(dyn.asString(""));
		}
	}*/

	public static class SpiritRace extends Race {

		/**
		 * Takes a physical form as a CitizenEntity; nature-based powers
		 */
		public static final SpiritRace NATURE = new SpiritRace(GMFiles.rl("nature"));

		/**
		 * Possesses beings; light-based powers
		 */
		public static final SpiritRace SPIRIT = new SpiritRace(GMFiles.rl("spirit"));
		/**
		 * Possesses beings; has fire-based powers
		 */
		public static final SpiritRace DEMON = new SpiritRace(GMFiles.rl("demon"));

		/**
		 * Takes a physical form as a CitizenEntity
		 */
		public static final SpiritRace DEITY = new SpiritRace(GMFiles.rl("deity"));

		private static final Map<ResourceLocation, SpiritRace> TYPES = new HashMap<>();

		private ResourceLocation rec;

		public SpiritRace(ResourceLocation rl) {
			super(rl);
			this.rec = rl;
			TYPES.put(rl, this);
		}

		public ResourceLocation getRec() {
			return rec;
		}

		public static SpiritRace get(ResourceLocation rl) {
			return TYPES.get(rl);
		}

		public static Collection<SpiritRace> getAll() {
			return TYPES.values();
		}

	}

	public static class TransformedRace extends Race {

		/**
		 * Drinks blood to survive, can turn invisible and burns in sunlight. Children
		 * may not inherit the burning in sunlight
		 */
		public static final TransformedRace VAMPIRE = new TransformedRace(GMFiles.rl("vampire"));

		/**
		 * Turns into a hard-to-control wolf-form every few nights
		 */
		public static final TransformedRace WEREWOLF = new TransformedRace(GMFiles.rl("werewolf"));

		private static final Map<ResourceLocation, TransformedRace> TYPES = new HashMap<>();

		private ResourceLocation rec;

		public TransformedRace(ResourceLocation rl) {
			super(rl);
			this.rec = rl;
			TYPES.put(rl, this);
		}

		public ResourceLocation getRec() {
			return rec;
		}

		public static TransformedRace get(ResourceLocation rl) {
			return TYPES.get(rl);
		}

		public static Collection<TransformedRace> getAll() {
			return TYPES.values();
		}

	}
}
