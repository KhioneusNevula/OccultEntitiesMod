package com.gm910.occentmod.empires;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.OccultEntities;
import com.google.common.collect.Sets;

import net.minecraft.util.ResourceLocation;

public class EmpireRace {

	public static final EmpireRace HUMAN = EmpireRace.create("human", true);
	public static final EmpireRace FAIRY = EmpireRace.create("fairy", true);
	public static final EmpireRace TROLL = EmpireRace.create("troll", false);
	public static final EmpireRace DRACONIAN = EmpireRace.create("draconian", false);

	public static final Set<EmpireRace> TYPES = new HashSet<>();

	private static int nextUseId = 1;

	public final ResourceLocation regName;

	public final int id;

	public final boolean canUseMagic;

	private EmpireRace(ResourceLocation regName, boolean canUseMagic) {
		this.regName = regName;
		this.canUseMagic = canUseMagic;
		this.id = nextUseId++;
		TYPES.add(this);
	}

	public ResourceLocation getRegName() {
		return regName;
	}

	public boolean canUseMagic() {
		return canUseMagic;
	}

	public int getId() {
		return id;
	}

	public static int getNextUseId() {
		return nextUseId;
	}

	public static String getRaceString(EmpireRace... races) {
		if (races.length == 0) {
			return "";
		}
		String r = races[0].id + "";
		for (EmpireRace r1 : races) {
			r = r + "-" + r1.getId();
		}
		return r;
	}

	public static EmpireRace[] getRaces(String racestring) {
		String[] s = racestring.split("-");
		return Sets.newHashSet(s).stream().map(EmpireRace::fromId).collect(Collectors.toSet())
				.toArray(new EmpireRace[0]);
	}

	public static EmpireRace create(ResourceLocation name, boolean canUseMagic) {
		return new EmpireRace(name, canUseMagic);
	}

	private static EmpireRace create(String name, boolean canUseMagic) {
		return new EmpireRace(new ResourceLocation(OccultEntities.MODID, name), canUseMagic);
	}

	public static EmpireRace fromName(ResourceLocation loc) {
		for (EmpireRace type : TYPES) {
			if (type.regName.equals(loc)) {
				return type;
			}
		}
		return null;
	}

	public static EmpireRace fromId(int loc) {
		for (EmpireRace type : TYPES) {
			if (type.id == loc) {
				return type;
			}
		}
		return null;
	}

	public static EmpireRace fromId(String loc) {
		return fromId(Integer.parseInt(loc));
	}

	@Override
	public boolean equals(Object o) {
		return regName.equals(((EmpireRace) o).regName);
	}

}
