package com.cluttered.cryptocurrency.ann;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.cluttered.cryptocurrency.ann.GsonConstant.GSON;

/**
 * @author cluttered.code@gmail.com
 */
public class NeuralNetwork {

    private static final Logger LOG = LoggerFactory.getLogger(NeuralNetwork.class);

    private final Integer inputSize;
    private final List<Layer> layers;

    NeuralNetwork(final Integer inputSize, final List<Layer> layers) {
        this.inputSize = inputSize;
        this.layers = layers;
    }

    public static NeuralNetwork fromJson(final String json) {
        return GSON.fromJson(json, NeuralNetwork.class);
    }

    public static NeuralNetworkBuilder builder(final int inputSize) {
        return NeuralNetworkBuilder.create(inputSize);
    }

    public List<Double> fire(final List<Double> inputs) {
        final long startTimeNanos = System.nanoTime();
        LOG.info("########## Fire NeuralNetwork ##########");
        LOG.info("Inputs: {}", inputs);

        if (inputs.size() != inputSize)
            throw new IllegalArgumentException("NeuralNetwork accepts " + inputSize + " inputs but received " + inputs.size());

        List<Double> layerResults = inputs;
        for (final Layer layer : layers) {
            layerResults = layer.fire(layerResults);
        }

        LOG.info("Outputs: {}", layerResults);
        LOG.info("NeuralNetwork Time: {}nanos", System.nanoTime() - startTimeNanos);
        return layerResults;
    }

    public String toJson() {
        return GSON.toJson(this);
    }
}