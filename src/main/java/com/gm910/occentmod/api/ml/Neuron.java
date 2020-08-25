package com.gm910.occentmod.api.ml;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class Neuron {
	/**
	 * Neuron's identifier
	 */
	private String id;
	/**
	 * Collection of neuron's input connections (connections to this neuron)
	 */
	protected List<Axon> inputConnections;
	/**
	 * Collection of neuron's output connections (connections from this to other
	 * neurons)
	 */
	protected List<Axon> outputConnections;
	/**
	 * Input summing function for this neuron
	 */
	protected InputSummingFunction inputSummingFunction;
	/**
	 * Activation function for this neuron
	 */
	protected ActivationFunction activationFunction;

	/**
	 * Default constructor
	 */
	public Neuron(String id) {
		this.id = id;
		this.inputConnections = new ArrayList<>();
		this.outputConnections = new ArrayList<>();
		inputSummingFunction = WeightedSumFunction.INSTANCE;
		activationFunction = SigmoidFunction.INSTANCE;
	}

	public Neuron(String id, InputSummingFunction sumFunc, ActivationFunction actFunc) {
		this(id);
		this.inputSummingFunction = sumFunc;
		this.activationFunction = actFunc;
	}

	public String getId() {
		return id;
	}

	public Neuron addInputConnection(Axon... ax) {
		this.inputConnections.addAll(Lists.newArrayList(ax));
		return this;
	}

	public Neuron setSummingFunction(InputSummingFunction sumFunc) {
		this.inputSummingFunction = sumFunc;
		return this;
	}

	public Neuron setActivationFunction(ActivationFunction actFunc) {
		this.activationFunction = actFunc;
		return this;
	}

	public List<Axon> getInputConnections() {
		return inputConnections;
	}

	public List<Axon> getOutputConnections() {
		return outputConnections;
	}

	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	public InputSummingFunction getInputSummingFunction() {
		return inputSummingFunction;
	}

	public Neuron addOutputConnection(Axon... ax) {
		this.outputConnections.addAll(Lists.newArrayList(ax));
		return this;
	}

	/**
	 * Calculates the neuron's output
	 */
	public double calculateOutput() {
		double totalInput = inputSummingFunction.collectOutput(inputConnections);
		return activationFunction.calculateOutput(totalInput);
	}
}