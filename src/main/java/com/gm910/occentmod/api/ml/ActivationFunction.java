package com.gm910.occentmod.api.ml;

import java.util.function.Function;

/**
 * Neural networks activation function interface.
 */
public interface ActivationFunction extends Function<Double, Double> {
	/**
	 * Performs calculation based on the sum of input neurons output.
	 * 
	 * @param summedInput neuron's sum of outputs respectively inputs for the
	 *                    connected neuron
	 * 
	 * @return Output's calculation based on the sum of inputs
	 */
	double calculateOutput(double summedInput);

	@Override
	default Double apply(Double t) {
		return calculateOutput(t);
	}
}