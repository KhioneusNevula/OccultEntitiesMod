package com.gm910.occentmod.entities.citizen.mind_and_traits.skills;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.entities.citizen.CitizenEntity;
import com.google.common.collect.Sets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class SkillType {

	private static final Map<ResourceLocation, SkillType> TYPES = new HashMap<>();

	ResourceLocation resource;

	private final Applicability applicability;

	int max;

	int min;

	public SkillType(ResourceLocation rl, Applicability applicability) {
		this.resource = rl;
		this.applicability = applicability;
		TYPES.put(rl, this);
		max = 5;
		min = 0;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public ResourceLocation getResource() {
		return resource;
	}

	public boolean isApplicable(LivingEntity e) {
		if (e instanceof PlayerEntity) {
			return this.applicability == Applicability.PLAYER_ONLY || this.applicability == Applicability.BOTH;
		} else if (e instanceof CitizenEntity) {
			return this.applicability == Applicability.CITIZEN_ONLY || this.applicability == Applicability.BOTH;
		}
		return false;
	}

	public static SkillType get(ResourceLocation rl) {
		return TYPES.get(rl);
	}

	public static Collection<SkillType> getAll() {
		return TYPES.values();
	}

	public static Set<SkillType> getByApplicability(Applicability... app) {
		return TYPES.values().stream().filter((e) -> Sets.newHashSet(app).contains(e.applicability))
				.collect(Collectors.toSet());
	}

	public static Set<SkillType> getByApplicability(LivingEntity e) {
		return TYPES.values().stream().filter((m) -> m.isApplicable(e)).collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName() + " " + this.resource;
	}

	public static enum Applicability {
		PLAYER_ONLY, CITIZEN_ONLY, BOTH
	}

}
