package com.gm910.occentmod.entities.citizen.mind_and_traits.personality;

import com.gm910.occentmod.entities.citizen.mind_and_traits.personality.Personality.ReactionType;

public class ReactionDeterminer<E> {
	E exceptionalLow;
	E low;
	E average;
	E high;
	E exceptional;

	public ReactionDeterminer(E initVal) {
		this(initVal, initVal, initVal);
	}

	public ReactionDeterminer(E exceptionalLow, E low, E average, E high, E exceptional) {
		this.exceptionalLow = exceptionalLow;
		this.low = low;
		this.high = high;
		this.average = average;
		this.exceptional = exceptional;
	}

	public ReactionDeterminer<E> withAverage(E average) {
		return new ReactionDeterminer<>(exceptionalLow, low, average, high, exceptional);
	}

	public ReactionDeterminer<E> withExceptional(E exceptional) {
		return new ReactionDeterminer<>(exceptionalLow, low, average, high, exceptional);
	}

	public ReactionDeterminer<E> withExceptionalLow(E exceptionalLow) {
		return new ReactionDeterminer<>(exceptionalLow, low, average, high, exceptional);
	}

	public ReactionDeterminer<E> withHigh(E high) {
		return new ReactionDeterminer<>(exceptionalLow, low, average, high, exceptional);
	}

	public ReactionDeterminer<E> withLow(E low) {
		return new ReactionDeterminer<>(exceptionalLow, low, average, high, exceptional);
	}

	public ReactionDeterminer<E> with(Personality.ReactionType key, E val) {
		E exceptionalLow = key == ReactionType.EXCEPTIONAL_LOW ? val : this.exceptionalLow;
		E exceptional = key == ReactionType.EXCEPTIONAL ? val : this.exceptional;
		E low = key == ReactionType.LOW ? val : this.low;
		E average = key == ReactionType.AVERAGE ? val : this.average;
		E high = key == ReactionType.HIGH ? val : this.high;
		return new ReactionDeterminer<>(exceptionalLow, low, average, high, exceptional);
	}

	public ReactionDeterminer(E low, E average, E high) {
		this.low = low;
		this.exceptionalLow = low;
		this.average = average;
		this.high = high;
		this.exceptional = high;
	}

	public E getExceptionalLow() {
		return exceptionalLow;
	}

	public E getLow() {
		return low;
	}

	public E getAverage() {
		return average;
	}

	public E getExceptional() {
		return exceptional;
	}

	public E getHigh() {
		return high;
	}

	public E get(Personality.ReactionType type) {
		switch (type) {
		case EXCEPTIONAL_LOW: {
			return exceptionalLow;
		}
		case LOW: {
			return low;
		}
		case AVERAGE: {
			return average;
		}
		case HIGH: {
			return high;
		}
		case EXCEPTIONAL: {
			return exceptional;
		}
		default: {
			return null;
		}
		}
	}
}