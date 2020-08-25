package com.gm910.occentmod.empires;
/*
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.EnglishNumberToWords;
import com.google.common.collect.Sets;

public class EmpireName {

	public static final EmpireName EMPTY = new EmpireName("", "", "", "");

	private final String[] names;
	private final String[] demonyms;
	private final String[] demonymPlurals;
	private final String[] adjectives;

	private EmpireName(String name, String adjective, String demonym, String demonymPlural) {
		this(new String[] { name }, new String[] { adjective }, new String[] { demonym },
				new String[] { demonymPlural });
	}

	private EmpireName(String[] name, String[] adjective, String[] demonym, String[] demonymPlural) {
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
				.withDemonymPlural(Sets.newHashSet(this.demonymPlurals).stream().map(func).collect(Collectors.toSet())
						.toArray(new String[0]));
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.toString().equals(obj.toString());
	}

}*/