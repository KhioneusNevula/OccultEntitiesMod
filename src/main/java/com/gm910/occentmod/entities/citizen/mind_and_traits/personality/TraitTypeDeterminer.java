package com.gm910.occentmod.entities.citizen.mind_and_traits.personality;

public class TraitTypeDeterminer<E> {
	E exceptionalLow;
	E low;
	E lowAverage;
	E highAverage;
	E high;
	E exceptionalHigh;

	public TraitTypeDeterminer(E initVal) {
		this(initVal, initVal);
	}

	public TraitTypeDeterminer(E exceptionalLow, E low, E lowAverage, E highAverage, E high, E exceptional) {
		this.exceptionalLow = exceptionalLow;
		this.low = low;
		this.high = high;
		this.lowAverage = lowAverage;
		this.highAverage = highAverage;
		this.exceptionalHigh = exceptional;
	}

	public TraitTypeDeterminer(E low, E average, E high) {
		this.low = low;
		this.exceptionalLow = low;
		this.lowAverage = average;
		this.highAverage = average;
		this.high = high;
		this.exceptionalHigh = high;
	}

	public TraitTypeDeterminer(E low, E high) {
		this.low = low;
		this.exceptionalLow = low;
		this.lowAverage = low;
		this.highAverage = high;
		this.high = high;
		this.exceptionalHigh = high;
	}

	public TraitTypeDeterminer<E> withLowAverage(E average) {
		return new TraitTypeDeterminer<>(exceptionalLow, low, average, highAverage, high, exceptionalHigh);
	}

	public TraitTypeDeterminer<E> withHighAverage(E average) {
		return new TraitTypeDeterminer<>(exceptionalLow, low, lowAverage, average, high, exceptionalHigh);
	}

	public TraitTypeDeterminer<E> withExceptional(E exceptional) {
		return new TraitTypeDeterminer<>(exceptionalLow, low, lowAverage, highAverage, high, exceptional);
	}

	public TraitTypeDeterminer<E> withExceptionalLow(E exceptionalLow) {
		return new TraitTypeDeterminer<>(exceptionalLow, low, lowAverage, highAverage, high, exceptionalHigh);
	}

	public TraitTypeDeterminer<E> withHigh(E high) {
		return new TraitTypeDeterminer<>(exceptionalLow, low, lowAverage, highAverage, high, exceptionalHigh);
	}

	public TraitTypeDeterminer<E> withLow(E low) {
		return new TraitTypeDeterminer<>(exceptionalLow, low, lowAverage, highAverage, high, exceptionalHigh);
	}

	public TraitTypeDeterminer<E> with(PersonalityTrait.TraitLevel key, E val) {
		E exceptionalLow = key == PersonalityTrait.TraitLevel.EXCEPTIONAL_LOW ? val : this.exceptionalLow;
		E exceptional = key == PersonalityTrait.TraitLevel.EXCEPTIONAL_HIGH ? val : this.exceptionalHigh;
		E low = key == PersonalityTrait.TraitLevel.LOW ? val : this.low;
		E average = key == PersonalityTrait.TraitLevel.LOW_AVERAGE ? val : this.lowAverage;
		E highAverage = key == PersonalityTrait.TraitLevel.HIGH_AVERAGE ? val : this.highAverage;
		E high = key == PersonalityTrait.TraitLevel.HIGH ? val : this.high;

		return new TraitTypeDeterminer<>(exceptionalLow, low, average, highAverage, high, exceptional);
	}

	public E getExceptionalLow() {
		return exceptionalLow;
	}

	public E getLow() {
		return low;
	}

	public E getLowAverage() {
		return lowAverage;
	}

	public E getHighAverage() {
		return highAverage;
	}

	public E getExceptional() {
		return exceptionalHigh;
	}

	public E getHigh() {
		return high;
	}

	public E get(PersonalityTrait.TraitLevel type) {
		switch (type) {
		case EXCEPTIONAL_LOW: {
			return exceptionalLow;
		}
		case LOW: {
			return low;
		}
		case LOW_AVERAGE: {
			return lowAverage;
		}
		case HIGH_AVERAGE: {
			return highAverage;
		}
		case HIGH: {
			return high;
		}
		case EXCEPTIONAL_HIGH: {
			return exceptionalHigh;
		}
		default: {
			return null;
		}
		}
	}
}