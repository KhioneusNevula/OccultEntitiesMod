package com.gm910.occentmod.entities.citizen.mind_and_traits.relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;

public class Genealogy implements IDynamicSerializable {

	SapientIdentity of = null;
	private SapientIdentity firstParent = null;
	private SapientIdentity secondParent = null;
	private Set<SapientIdentity> children = new HashSet<>();
	private Set<SapientIdentity> siblings = new HashSet<>();

	public Genealogy(SapientIdentity of, SapientIdentity firstParent, SapientIdentity secondParent) {
		this(of, firstParent, secondParent, new HashSet<>(), new HashSet<>());
	}

	public Genealogy(SapientIdentity of, SapientIdentity fp, SapientIdentity sp, Set<SapientIdentity> chil,
			Set<SapientIdentity> sibs) {
		this.of = of;
		this.firstParent = fp;
		this.secondParent = sp;
		this.children = chil;
		this.siblings = sibs;
		this.siblings.remove(of);
	}

	public Genealogy(SapientIdentity of) {
		this.of = of;
	}

	public Genealogy(Dynamic<?> dyn) {
		this(new SapientIdentity(dyn.get("of").get().get()),
				dyn.get("parent1").get().isPresent() ? new SapientIdentity(dyn.get("parent1").get().get()) : null,
				dyn.get("parent2").get().isPresent() ? new SapientIdentity(dyn.get("parent2").get().get()) : null,
				dyn.get("children").asStream().map((d) -> new SapientIdentity(d)).collect(Collectors.toSet()),
				dyn.get("siblings").asStream().map((d) -> new SapientIdentity(d)).collect(Collectors.toSet()));
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

	public Set<SapientIdentity> getChildren() {
		return new HashSet<>(children);
	}

	public SapientIdentity getFirstParent() {
		return firstParent;
	}

	public SapientIdentity getCitizen() {
		return of;
	}

	public SapientIdentity getSecondParent() {
		return secondParent;
	}

	public Set<SapientIdentity> getSiblings() {
		return new HashSet<>(siblings);
	}

	private Genealogy setFirstParentMutable(SapientIdentity firstParent) {
		this.firstParent = firstParent;
		return this;
	}

	private Genealogy setSecondParentMutable(SapientIdentity secondParent) {
		this.secondParent = secondParent;
		return this;
	}

	private Genealogy setChildrenMutable(Set<SapientIdentity> children) {
		this.children = children;
		return this;
	}

	private Genealogy setSiblingsMutable(Set<SapientIdentity> siblings) {
		this.siblings = siblings;
		this.siblings.remove(of);
		return this;
	}

	private Genealogy setOfMutable(SapientIdentity s) {
		this.of = s;
		this.siblings.remove(of);
		return this;
	}

	public Genealogy setFirstParent(SapientIdentity par) {
		return new Genealogy(this).setFirstParentMutable(par);
	}

	public Genealogy setSecondParent(SapientIdentity secondParent) {
		return new Genealogy(this).setSecondParentMutable(secondParent);
	}

	public Genealogy setChildren(Set<SapientIdentity> children) {
		return new Genealogy(this).setChildrenMutable(children);
	}

	public Genealogy setSiblings(Set<SapientIdentity> siblings) {
		return new Genealogy(this).setSiblingsMutable(siblings);
	}

	public Genealogy setOwner(SapientIdentity owner) {
		return new Genealogy(this).setOfMutable(owner);
	}

	public Genealogy addSibling(SapientIdentity sibling) {
		Genealogy s = new Genealogy(this);
		Set<SapientIdentity> f = s.getSiblings();
		f.add(sibling);
		s.setSiblingsMutable(f);
		return s;
	}

	public Genealogy addChild(SapientIdentity child) {
		Genealogy s = new Genealogy(this);
		Set<SapientIdentity> f = s.getChildren();
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