package com.gm910.occentmod.empires;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.NonNullMap;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;

public class Government implements IDynamicSerializable {

	private Map<LeaderType, Set<SapientIdentity>> highLeaders = new NonNullMap<>(() -> new HashSet<>());

	private GovernType governType = GovernType.values()[0];

	private LineageType lineageType = LineageType.values()[0];

	private RuleMethod ruleMethod = RuleMethod.values()[0];

	public Government() {
	}

	public Government addJob(SapientIdentity identity, LeaderType job) {
		if (this.governType == GovernType.MONARCHY && job == LeaderType.RULER && !this.highLeaders.get(job).isEmpty()) {
			throw new IllegalArgumentException("Cannot add " + identity + " as a Ruler; " + this + " is a monarchy");
		}
		this.highLeaders.get(job).add(identity);
		return this;
	}

	public Set<SapientIdentity> getSetFor(LeaderType job) {
		return this.highLeaders.get(job);
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Map<T, T> map = new HashMap<>();
		map.put(ops.createString("governType"), ops.createInt(governType.ordinal()));
		map.put(ops.createString("ruleMethod"), ops.createInt(ruleMethod.ordinal()));
		map.put(ops.createString("lineageType"), ops.createInt(lineageType.ordinal()));
		map.put(ops.createString("leaders"),
				ops.createMap(this.highLeaders.entrySet().stream()
						.map((e) -> Pair.of(ops.createInt(e.getKey().ordinal()),
								ops.createList(e.getValue().stream().map((m) -> m.serialize(ops)))))
						.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
		return ops.createMap(map);
	}

	public <T> void deserialize(Dynamic<T> dyn) {
		this.governType = GovernType.values()[dyn.get("governType").asInt(0)];
		this.ruleMethod = RuleMethod.values()[dyn.get("ruleMethod").asInt(0)];
		this.lineageType = LineageType.values()[dyn.get("lineageType").asInt(0)];
		this.highLeaders.putAll(dyn.get("leaders").asMap((d) -> LeaderType.values()[d.asInt(0)],
				(d) -> d.asStream().map((e) -> new SapientIdentity(e)).collect(Collectors.toSet())));
	}

	public LineageType getLineageType() {
		return lineageType;
	}

	public GovernType getGovernType() {
		return governType;
	}

	public RuleMethod getRuleMethod() {
		return ruleMethod;
	}

	public void setGovernType(GovernType governType) {
		this.governType = governType;
	}

	public void setLineageType(LineageType lineageType) {
		this.lineageType = lineageType;
	}

	public void setRuleMethod(RuleMethod ruleMethod) {
		this.ruleMethod = ruleMethod;
	}

	public Government copy() {
		Government other = new Government();
		other.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, this.serialize(NBTDynamicOps.INSTANCE)));
		return other;
	}

	public static enum GovernType {
		/**
		 * One leader
		 */
		MONARCHY,
		/**
		 * One leader, one spouse -> two leaders
		 */
		DUUMVIRATE,
		/**
		 * Any number of leaders
		 */
		OLIGARCHY
	}

	public static enum LineageType {
		/**
		 * Leaders are elected by citizens
		 */
		DEMOCRATIC,
		/**
		 * Leaders are elected by the senate
		 */
		REPUBLIC,
		/**
		 * Deity ordains leaders
		 */
		DIVINE,
		/**
		 * Leaders are selected by next of kin
		 */
		BLOODLINE
	}

	public static enum RuleMethod {
		/**
		 * Leader is a priest who listens to their archbishop, who makes claims about
		 * the deity
		 */
		THEOCRACY,
		/**
		 * Leader makes executive decisions, but senate creates laws; senator jobs are
		 * designated as the eldest members of all provinces
		 */
		SENATE,
		/**
		 * Leader makes executive decisions, council comes up with laws; councilors are
		 * elected by the people
		 */
		COUNCIL,
		/**
		 * Leader makes all decisions; council can only influence them
		 */
		DICTATORSHIP
	}

	public static enum LeaderType {
		RULER, SENATOR, ARCHMAGE, ARCHBISHOP, PENDRAGON
	}

}
