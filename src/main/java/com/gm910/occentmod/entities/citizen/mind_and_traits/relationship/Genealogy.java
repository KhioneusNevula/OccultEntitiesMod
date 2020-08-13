package com.gm910.occentmod.entities.citizen.mind_and_traits.relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;

public class Genealogy implements IDynamicSerializable {

	CitizenIdentity of = null;
	private CitizenIdentity firstParent = null;
	private CitizenIdentity secondParent = null;
	private Set<CitizenIdentity> children = new HashSet<>();
	private Set<CitizenIdentity> siblings = new HashSet<>();

	public Genealogy(CitizenIdentity of, CitizenIdentity firstParent, CitizenIdentity secondParent) {
		this(of, firstParent, secondParent, new HashSet<>(), new HashSet<>());
	}

	public Genealogy(CitizenIdentity of, CitizenIdentity fp, CitizenIdentity sp, Set<CitizenIdentity> chil,
			Set<CitizenIdentity> sibs) {
		this.of = of;
		this.firstParent = fp;
		this.secondParent = sp;
		this.children = chil;
		this.siblings = sibs;
		this.siblings.remove(of);
	}

	public Genealogy(CitizenIdentity of) {
		this.of = of;
	}

	public Genealogy(Dynamic<?> dyn) {
		this(new CitizenIdentity(dyn.get("of").get().get()),
				dyn.get("parent1").get().isPresent() ? new CitizenIdentity(dyn.get("parent1").get().get()) : null,
				dyn.get("parent2").get().isPresent() ? new CitizenIdentity(dyn.get("parent2").get().get()) : null,
				dyn.get("children").asStream().map((d) -> new CitizenIdentity(d)).collect(Collectors.toSet()),
				dyn.get("siblings").asStream().map((d) -> new CitizenIdentity(d)).collect(Collectors.toSet()));
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T oft = of.serialize(ops);
		T par1 = firstParent.serialize(ops);
		T par2 = secondParent.serialize(ops);
		T chil = ops.createList(this.children.stream().map((f) -> f.serialize(ops)));
		T sibs = ops.createList(this.siblings.stream().map((f) -> f.serialize(ops)));

		return ops.createMap(ImmutableMap.of(ops.createString("of"), oft, ops.createString("parent1"), par1,
				ops.createString("parent2"), par2, ops.createString("children"), chil, ops.createString("siblings"),
				sibs));
	}

	public Genealogy(Genealogy other) {
		this(other.of, other.firstParent, other.secondParent, other.children, other.siblings);
	}

	public Set<CitizenIdentity> getChildren() {
		return new HashSet<>(children);
	}

	public CitizenIdentity getFirstParent() {
		return firstParent;
	}

	public CitizenIdentity getCitizen() {
		return of;
	}

	public CitizenIdentity getSecondParent() {
		return secondParent;
	}

	public Set<CitizenIdentity> getSiblings() {
		return new HashSet<>(siblings);
	}

	private Genealogy setFirstParentMutable(CitizenIdentity firstParent) {
		this.firstParent = firstParent;
		return this;
	}

	private Genealogy setSecondParentMutable(CitizenIdentity secondParent) {
		this.secondParent = secondParent;
		return this;
	}

	private Genealogy setChildrenMutable(Set<CitizenIdentity> children) {
		this.children = children;
		return this;
	}

	private Genealogy setSiblingsMutable(Set<CitizenIdentity> siblings) {
		this.siblings = siblings;
		this.siblings.remove(of);
		return this;
	}

	private Genealogy setOfMutable(CitizenIdentity s) {
		this.of = s;
		this.siblings.remove(of);
		return this;
	}

	public Genealogy setFirstParent(CitizenIdentity par) {
		return new Genealogy(this).setFirstParentMutable(par);
	}

	public Genealogy setSecondParent(CitizenIdentity secondParent) {
		return new Genealogy(this).setSecondParentMutable(secondParent);
	}

	public Genealogy setChildren(Set<CitizenIdentity> children) {
		return new Genealogy(this).setChildrenMutable(children);
	}

	public Genealogy setSiblings(Set<CitizenIdentity> siblings) {
		return new Genealogy(this).setSiblingsMutable(siblings);
	}

	public Genealogy setOwner(CitizenIdentity owner) {
		return new Genealogy(this).setOfMutable(owner);
	}

	public Genealogy addSibling(CitizenIdentity sibling) {
		Genealogy s = new Genealogy(this);
		Set<CitizenIdentity> f = s.getSiblings();
		f.add(sibling);
		s.setSiblingsMutable(f);
		return s;
	}

	public Genealogy addChild(CitizenIdentity child) {
		Genealogy s = new Genealogy(this);
		Set<CitizenIdentity> f = s.getChildren();
		f.add(child);
		s.setChildrenMutable(f);
		return s;
	}

	@Override
	public boolean equals(Object obj) {
		Genealogy other = (Genealogy) obj;
		return this.secondParent != null && other.secondParent != null && this.secondParent.equals(other.secondParent)
				&& this.firstParent != null && other.firstParent != null && this.firstParent.equals(other.firstParent)
				&& this.of != null && other.of != null && this.of.equals(other.of)
				&& this.children.equals(other.children) && this.siblings.equals(other.siblings);
	}

}