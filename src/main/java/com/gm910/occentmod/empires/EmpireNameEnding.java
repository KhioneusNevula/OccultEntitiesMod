package com.gm910.occentmod.empires;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gm910.occentmod.api.language.NamePhonemicHelper;
import com.gm910.occentmod.api.language.NamePhonemicHelper.Phoneme;
import com.gm910.occentmod.api.language.NamePhonemicHelper.PhonemeType;
import com.gm910.occentmod.api.language.NamePhonemicHelper.PhonemeWord;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EmpireNameEnding {
	Set<Phoneme> matches;

	private PhonemeWord ending;
	private PhonemeWord adjective;
	private PhonemeWord demonym;
	private PhonemeWord demonymPlural;

	private String original;

	public EmpireNameEnding(String end) {
		original = end;
		matches = new HashSet<>();
		if (end.isEmpty()) {
			ending = new PhonemeWord(Lists.newArrayList());
			adjective = new PhonemeWord(Lists.newArrayList());
			demonym = new PhonemeWord(Lists.newArrayList());
			demonymPlural = new PhonemeWord(Lists.newArrayList());

		}
		String settings = end.split("{", -1)[1].replace("}", "");
		String _ends = end.split("{", -1)[0];
		String[] ends = _ends.split(",", -1);
		if (ends.length < 4) {
			throw new IllegalArgumentException("cannot be made with the endstring " + _ends + ": input was " + end);
		}
		this.ending = new PhonemeWord(Sets.newHashSet(ends[0].split("/", -1)).stream().findAny().get());
		this.adjective = new PhonemeWord(Sets.newHashSet(ends[1].split("/", -1)).stream().findAny().get());
		this.demonym = new PhonemeWord(Sets.newHashSet(ends[2].split("/", -1)).stream().findAny().get());
		this.demonymPlural = new PhonemeWord(Sets.newHashSet(ends[3].split("/", -1)).stream().findAny().get());

		String[] types = settings.split(",");
		Set<Phoneme> toRemove = new HashSet<>();
		Set<Phoneme> toAdd = new HashSet<>();
		for (String type1 : types) {
			boolean not = type1.startsWith("!");
			String type = type1.replace("!", "");
			Set<Phoneme> adda = new HashSet<>();

			if (type.isEmpty()) {
				adda.addAll(NamePhonemicHelper.ALL);
			} else if (type.toLowerCase().contains("consonant")) {
				adda.addAll(NamePhonemicHelper.getConsonants());
			} else if (type.toLowerCase().contains("vowel")) {
				adda.addAll(NamePhonemicHelper.getVowels());
			} else if (type.toLowerCase().contains("phoneme")) {
				adda.addAll(NamePhonemicHelper.getPhonemesByType(PhonemeType.BOTH));
				adda.addAll(NamePhonemicHelper.getPhonemesByType(PhonemeType.CONSONANT));
				adda.addAll(NamePhonemicHelper.getPhonemesByType(PhonemeType.VOWEL));
			} else if (type.toLowerCase().contains("symbol")) {
				adda.addAll(NamePhonemicHelper.getPhonemesByType(PhonemeType.NEITHER));
			} else if (type.toLowerCase().contains("any")) {
				adda.addAll(NamePhonemicHelper.getPhonemes());
			} else if (type.toLowerCase().contains("none")) {
				adda.addAll(NamePhonemicHelper.ALL);
				not = true;
			} else {
				adda.add(NamePhonemicHelper.getFrom(type));
			}
			if (not)
				toRemove.addAll(adda);
			else
				toAdd.addAll(adda);
		}
		if (toAdd.isEmpty()) {
			toAdd.addAll(NamePhonemicHelper.ALL);
		}
		matches.addAll(toAdd);
		matches.removeAll(toRemove);

	}

	public boolean matchesEnding(Phoneme phon) {
		return this.matches.contains(phon);
	}

	public PhonemeWord getAdjective() {
		return adjective;
	}

	public PhonemeWord getDemonym() {
		return demonym;
	}

	public PhonemeWord getDemonymPlural() {
		return demonymPlural;
	}

	public PhonemeWord getEnding() {
		return ending;
	}

	public String makeSettings() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.ending.toString().toLowerCase() + ",").append(this.adjective.toString().toLowerCase() + ",")
				.append(this.demonym.toString().toLowerCase() + ",").append(this.demonymPlural.toString().toLowerCase())
				.append("{");
		if (!this.matches.isEmpty()) {
			List<Phoneme> mat = Lists.newArrayList(matches);
			for (int i = 0; i < mat.size(); i++) {
				builder.append(mat.get(i).toString());
				if (i < mat.size() - 1) {
					builder.append(",");
				}
			}
		} else {
			builder.append("none");
		}
		return builder.toString();
	}

	public String getOriginal() {
		return original;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.ending + "/" + this.adjective + "/" + this.demonym + "/" + this.demonymPlural;
	}

	@Override
	public boolean equals(Object obj) {
		EmpireNameEnding end = (EmpireNameEnding) obj;
		return end.ending.equals(this.ending) && end.adjective.equals(this.adjective)
				&& end.demonym.equals(this.demonym) && end.demonymPlural.equals(this.demonymPlural);
	}
}