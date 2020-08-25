package com.gm910.occentmod.entities.citizen.mind_and_traits.skills;

import java.util.Map;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.mind_and_traits.InformationHolder;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ResourceLocation;

public class Skills extends InformationHolder {

	private Object2IntMap<SkillType> skills = new Object2IntOpenHashMap<>();

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T trait1 = ops.createMap(skills.object2IntEntrySet().stream().map((trait) -> {
			return Pair.of(ops.createString(trait.getKey().resource.toString()), ops.createInt(trait.getIntValue()));
		}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		return ops.createMap(ImmutableMap.of(ops.createString("skills"), trait1));
	}

	public Skills(Dynamic<?> dyn) {
		Map<SkillType, Integer> map = dyn.get("skills")
				.asMap((d) -> SkillType.get(new ResourceLocation(d.asString(""))), (d) -> d.asInt(0));
		skills.putAll(map);
	}

	public Skills() {
		for (SkillType skill : SkillType.getAll()) {
			skills.put(skill, 0);
		}
	}

	public static int clamp(int val, int min, int max) {
		return Math.max(min, Math.min(max, val));
	}

	public void setSkill(SkillType trait, int value) {
		this.skills.put(trait, clamp(value, trait.min, trait.max));
	}

	public int getSkill(SkillType trait) {
		return this.skills.getInt(trait);
	}

	@Override
	public long getTicksExisted() {
		return 0;
	}

}
