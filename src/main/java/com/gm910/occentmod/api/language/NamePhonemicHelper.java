package com.gm910.occentmod.api.language;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.GMHelper;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;

public class NamePhonemicHelper {

	public static final Phoneme Y = new Phoneme("y", false) {
		public boolean isConsonant() {
			return true;
		};

		public boolean isVowel() {
			return true;
		};

	};

	public static final Phoneme DASH = new Phoneme("-", false) {
		public boolean isConsonant() {
			return false;
		};

		public boolean isVowel() {
			return false;
		};
	};

	public static final Phoneme SPACE = new Phoneme(" ", false) {
		public boolean isConsonant() {
			return false;
		};

		public boolean isVowel() {
			return false;
		};
	};

	public static final Set<Consonant> CONSONANTS = GMHelper.create(new HashSet<>(), (set) -> {
		set.addAll(Consonant.from(true, true, true, "b", "c", "d", "f", "g", "k", "l", "m", "n", "p", "r", "s", "t",
				"v", "z"));
		set.addAll(Consonant.from(true, true, false, "h", "j", "q", "w", "x", "sh", "ch", "th", "rh", "bh", "st", "sp",
				"ph", "gh", "sc", "sk", "sp", "sm"));
		set.addAll(Consonant.from(true, false, false, "qu", "bl", "cl", "fl", "gl", "kl", "pl", "sl", "br", "cr", "dr",
				"fr", "gr", "kr", "pr", "tr", "bw", "cw", "dw", "fw", "gw", "jw", "kw", "mw", "pw", "qw", "sw", "tw"));
		set.addAll(Consonant.from(false, true, false, "ct", "ck", "cs", "ds", "fs", "ft", "fz", "gs", "gz", "hb", "hc",
				"hd", "hf", "hg", "hj", "hk", "hl", "hm", "hn", "hp", "hq", "hs", "ht", "kt", "ks", "lb", "lc", "ld",
				"lf", "lg", "lh", "lj", "lk", "lm", "ln", "lp", "lq", "ls", "lt", "lv", "lx", "lz", "mb", "mp", "ms",
				"nd", "ng", "nj", "nk", "np", "nq", "ns", "nt", "nv", "nx", "nz", "ps", "pt", "rb", "rc", "rd", "rf",
				"rg", "rj", "rk", "rl", "rm", "rn", "rp", "rs", "rt", "rv", "rx", "rz", "ts", "vs", "wd", "wf", "wm",
				"wn", "wl", "wt", "ws"));
		set.addAll(Consonant.from(false, false, false, "'"));
		set.add(new Consonant("y", true, true, false) {
			public boolean isConsonant() {
				return true;
			}

			public boolean isVowel() {
				return true;
			}
		});
	});
	public static final Set<Vowel> VOWELS = GMHelper.create(new HashSet<>(), (set) -> {
		set.addAll(Vowel.from(true, "a", "e", "i", "o", "u"));
		set.add(new Vowel("y", false) {
			public boolean isConsonant() {
				return true;
			}

			public boolean isVowel() {
				return true;
			}
		});
	});

	public static final Set<Phoneme> ALL = GMHelper.create(new HashSet<>(), (set) -> {
		set.addAll(CONSONANTS);
		set.addAll(VOWELS);
		set.add(Y);
		set.add(DASH);
		set.add(SPACE);
	});

	public static Set<Phoneme> getPhonemes() {
		Set<Phoneme> ph = new HashSet<>(ALL);
		return ph;
	}

	public static Set<Phoneme> getPhonemesByType(PhonemeType type) {
		return getPhonemes().stream().filter(type::matches).collect(Collectors.toSet());
	}

	public static Set<Consonant> getConsonants(boolean beginning, boolean end) {
		return CONSONANTS.stream().filter((con) -> con.canGoAtBeginning == beginning && con.canGoAtEnd == end)
				.collect(Collectors.toSet());
	}

	public static Set<Consonant> getBeginnerConsonants() {
		return CONSONANTS.stream().filter((con) -> con.canGoAtBeginning).collect(Collectors.toSet());
	}

	public static Set<Consonant> getEnderConsonants() {
		return CONSONANTS.stream().filter((con) -> con.canGoAtEnd).collect(Collectors.toSet());
	}

	public static Set<Vowel> getVowels() {
		return Sets.newHashSet(VOWELS);
	}

	public static Set<Consonant> getConsonants() {
		return Sets.newHashSet(CONSONANTS);
	}

	public static Phoneme getFrom(String name) {
		Set<Phoneme> ph = new HashSet<>(ALL);
		Optional<Phoneme> phon = ph.stream().filter((e) -> e.letters.equals(name)).findAny();
		if (!phon.isPresent()) {
			return null;
		}
		return phon.get();
	}

	public static boolean isConsonant(String s) {
		Phoneme ph = getFrom(s);
		if (ph == null)
			return false;
		if (ph.isConsonant())
			return true;
		return false;
	}

	public static boolean isVowel(String s) {
		Phoneme ph = getFrom(s);
		if (ph == null)
			return false;
		if (ph.isVowel())
			return true;
		return false;
	}

	public static boolean is(String s, PhonemeType type) {
		Phoneme ph = getFrom(s);
		if (ph == null)
			return false;
		return type.matches(ph);
	}

	public static boolean sameType(Phoneme o, Phoneme t) {
		return PhonemeType.get(o) == PhonemeType.get(t);
	}

	public static boolean sameType(String o, String t) {
		return sameType(getFrom(o), getFrom(t));
	}

	public static boolean exists(String s) {
		Phoneme ph = getFrom(s);
		if (ph == null)
			return false;
		return true;
	}

	public static PhonemeWord generateName(Random rand) {
		return generateName(rand, 2 + rand.nextInt(19));
	}

	public static PhonemeWord generateName(Random rand, int phonemicLength) {
		List<Phoneme> name = new ArrayList<>(phonemicLength);

		boolean con = rand.nextBoolean();
		int vowelCount = 0;
		for (int i = 0; i < phonemicLength; i++) {
			if (i != 0) {
				con = !con;
			}
			if (vowelCount < 3) {
				con = rand.nextBoolean();
				if (con) {
					vowelCount = 0;
				}
			}
			if (!con) {
				vowelCount++;
			}
			Set<Phoneme> cons = new HashSet<>();
			if (i == 0) {
				cons.addAll(con ? getBeginnerConsonants() : getVowels());
				name.add(cons.stream().findAny().get());
				continue;
			} else if (i >= phonemicLength) {
				cons.addAll(con ? getEnderConsonants() : getVowels());
			} else {
				cons.addAll(con ? getConsonants() : getVowels());
			}
			Phoneme next = cons.stream().findAny().get();
			name.add(next);
			if (next.canDouble && rand.nextInt(5) < 1) {
				name.add(next);
			}
		}

		return new PhonemeWord(name);
	}

	public static class Phoneme {
		private String letters;
		private boolean canDouble;

		public Phoneme(String letters, boolean canDouble) {
			this.letters = letters;
			this.canDouble = canDouble;
		}

		public boolean canDouble() {
			return canDouble;
		}

		public String getLetters() {
			return letters;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return getLetters();
		}

		public boolean isConsonant() {
			return this instanceof Consonant;
		}

		public boolean isVowel() {
			return this instanceof Vowel;
		}

		public boolean equal(Object o) {
			if (o instanceof Phoneme) {
				return this.letters.equals(((Phoneme) o).getLetters());
			}
			return false;
		}
	}

	public static class Consonant extends Phoneme {
		private boolean canGoAtBeginning;
		private boolean canGoAtEnd;

		public Consonant(String letters, boolean canGoAtBeginning, boolean canGoAtEnd, boolean canDouble) {
			super(letters, canDouble);
			this.canGoAtBeginning = canGoAtBeginning;
			this.canGoAtEnd = canGoAtEnd;
		}

		public static Set<Consonant> from(boolean canGoAtBeginning, boolean canGoAtEnd, boolean canDouble,
				String... cons) {
			Set<Consonant> set = Sets.newHashSet(cons).stream()
					.map((e) -> new Consonant(e, canGoAtBeginning, canGoAtEnd, canDouble)).collect(Collectors.toSet());
			return set;
		}

		public boolean canGoAtBeginning() {
			return canGoAtBeginning;
		}

		public boolean canGoAtEnd() {
			return canGoAtEnd;
		}
	}

	public static class Vowel extends Phoneme {

		public Vowel(String letters, boolean canDouble) {
			super(letters, canDouble);
		}

		public static Set<Vowel> from(boolean canDouble, String... cons) {
			Set<Vowel> set = Sets.newHashSet(cons).stream().map((e) -> new Vowel(e, canDouble))
					.collect(Collectors.toSet());
			return set;
		}

	}

	public static class PhonemeWord implements IDynamicSerializable {
		private List<Phoneme> name;

		public PhonemeWord(List<Phoneme> name) {
			this.name = new ArrayList<>(name);
		}

		public PhonemeWord(String unified) {
			List<Phoneme> n = new ArrayList<>();

			int i = 0;
			while (i < unified.length()) {

				boolean areSame = false;

				Phoneme gotten = null;

				char c = unified.charAt(i);
				if (i + 1 < unified.length()) {
					areSame = sameType(c + "", unified.charAt(i + 1) + "");
				}

				if (areSame) {
					gotten = getFrom("" + c + unified.charAt(i + 1));
				} else {
					gotten = getFrom("" + c);

				}
				if (gotten == null) {
					throw new IllegalArgumentException(
							"Unrecognized phoneme : " + c + (areSame ? unified.charAt(i + 1) : ""));
				}
				n.add(gotten);
			}
			this.name = n;
		}

		public PhonemeWord(Dynamic<?> dyn) {
			this.name = dyn.asList((e) -> NamePhonemicHelper.getFrom(e.asString("a")));
		}

		public List<Phoneme> getName() {
			return new ArrayList<>(name);
		}

		public Phoneme phonemeAt(int ind) {
			return name.get(ind);
		}

		public Phoneme getEnding() {
			return name.get(name.size() - 1);
		}

		@Override
		public String toString() {

			String n = String.join("",
					name.stream().map((e) -> e.letters).collect(Collectors.toList()).toArray(new String[0]));
			n = Character.toUpperCase(n.charAt(0)) + n.substring(1);
			return n;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PhonemeWord) {
				return this.name.equals(((PhonemeWord) obj).getName());
			}
			return false;
		}

		@Override
		public <T> T serialize(DynamicOps<T> ops) {
			return ops.createList(name.stream().map((e) -> ops.createString(e.toString())));
		}

		public PhonemeWord concat(PhonemeWord theOther) {
			List<Phoneme> new_ = new ArrayList<>();
			new_.addAll(this.name);
			new_.addAll(theOther.name);
			return new PhonemeWord(new_);
		}

		public PhonemeWord add(Phoneme phon) {

			List<Phoneme> new_ = this.getName();
			new_.add(phon);
			return new PhonemeWord(new_);
		}

		public PhonemeWord copy() {
			return new PhonemeWord(this.name);
		}
	}

	public static enum PhonemeType {
		VOWEL(VOWELS), CONSONANT(CONSONANTS),
		BOTH(getPhonemes().stream().filter((e) -> e.isConsonant() || e.isVowel()).collect(Collectors.toSet())),
		NEITHER(getPhonemes().stream().filter((e) -> !e.isConsonant() && !e.isVowel()).collect(Collectors.toSet()));

		private Set<? extends Phoneme> accepted;

		private PhonemeType(Set<? extends Phoneme> accepted) {
			this.accepted = accepted;
		}

		public boolean matches(Phoneme phon) {
			return accepted.contains(phon);
		}

		public static PhonemeType get(Phoneme p) {
			if (p.isConsonant() && p.isVowel())
				return BOTH;
			if (p.isConsonant())
				return CONSONANT;
			if (p.isVowel())
				return VOWEL;
			return NEITHER;
		}
	}

}
