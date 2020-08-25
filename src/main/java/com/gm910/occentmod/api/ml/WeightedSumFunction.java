package com.gm910.occentmod.api.ml;

import java.util.List;

public enum WeightedSumFunction implements InputSummingFunction {
	INSTANCE;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double collectOutput(List<Axon> inputConnections) {
		double weightedSum = 0d;
		for (Axon connection : inputConnections) {
			weightedSum += connection.getWeightedInput();
		}
		return weightedSum;
	}
}