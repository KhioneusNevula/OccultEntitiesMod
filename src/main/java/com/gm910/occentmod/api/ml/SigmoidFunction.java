package com.gm910.occentmod.api.ml;

public enum SigmoidFunction implements ActivationFunction {
	INSTANCE;

	@Override
	public double calculateOutput(double summedInput) {
		return (1 / (1 + Math.pow(Math.E, (-1 * summedInput))));
	}

}
