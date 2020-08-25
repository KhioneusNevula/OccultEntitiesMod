package com.gm910.occentmod.api.ml;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetLayer {
	/**
	 * Layer's identifier
	 */
	private String id;
	/**
	 * Collection of neurons in this layer
	 */
	protected List<Neuron> neurons;

	/**
	 * Creates an empty layer with an id.
	 * 
	 * @param id layer's identifier
	 */
	public NeuralNetLayer(String id) {
		this.id = id;
		neurons = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public List<Neuron> getNeurons() {
		return neurons;
	}

	/**
	 * Creates a layer with a list of neurons and an id.
	 *
	 * @param id      layer's identifier
	 * @param neurons list of neurons to be added to the layer
	 */
	public NeuralNetLayer(String id, List<Neuron> neurons) {
		this.id = id;
		this.neurons = neurons;
	}
}