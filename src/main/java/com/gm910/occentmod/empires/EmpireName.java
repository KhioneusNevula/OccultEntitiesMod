package com.gm910.occentmod.empires;

import com.gm910.occentmod.api.language.NamePhonemicHelper.PhonemeWord;
import com.google.common.collect.Lists;

/**
 * NAME, ADJECTIVE, DEMONYM, DEMONYMPLURAL
 * 
 * @author borah
 *
 */
public class EmpireName {

	public static final EmpireName EMPTY = new EmpireName(new PhonemeWord(Lists.newArrayList()),
			new EmpireNameEnding(""));

	private final PhonemeWord namePrefix;

	private EmpireNameEnding ending;

	/**
	 * NAME, ADJECTIVE, DEMONYM, DEMONYMPLURAL
	 * 
	 * @author borah
	 *
	 */
	private EmpireName(PhonemeWord name, EmpireNameEnding ending) {
		this.namePrefix = name;
		this.ending = ending;
	}

	public static EmpireName of(PhonemeWord name, EmpireNameEnding ending) {
		EmpireName noma = new EmpireName(name, ending);
		if (noma.equals(EMPTY)) {
			return EMPTY;
		}
		return noma;
	}

	public static EmpireName of(PhonemeWord name, String loadedending) {
		EmpireNameEnding end = new EmpireNameEnding(loadedending.replace("$", ""));
		EmpireName noma = EmpireName.of(name, end);
		if (noma.equals(EMPTY)) {
			return EMPTY;
		}
		return noma;
	}

	public static EmpireName fromData(String dat) {
		return EmpireName.of(new PhonemeWord(dat.split("||")[0]), dat.split("||")[1]);
	}

	public PhonemeWord getRegularName() {
		return this.namePrefix.concat(ending.getEnding());
	}

	public PhonemeWord getNamePrefix() {
		return namePrefix;
	}

	public PhonemeWord getAdjective() {
		return this.namePrefix.concat(ending.getAdjective());
	}

	public PhonemeWord getDemonym() {
		return this.namePrefix.concat(ending.getDemonym());
	}

	public PhonemeWord getDemonymPlural() {
		return this.namePrefix.concat(ending.getDemonymPlural());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getRegularName().toString();
	}

	public String writeData() {
		return this.namePrefix + "||" + this.ending.makeSettings();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.namePrefix.equals(((EmpireName) obj).namePrefix)
				&& this.ending.equals(((EmpireName) obj).ending);
	}

}