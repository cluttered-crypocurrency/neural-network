package com.cluttered.cryptocurrency.ann;

import com.cluttered.cryptocurrency.ga.GeneticElement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author cluttered.code@gmail.com
 */
public class NeuralNetwork implements GeneticElement<NeuralNetwork> {

    private static final Logger LOG = LoggerFactory.getLogger(NeuralNetwork.class);
    private static final Gson GSON = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .create();

    private int inputSize;
    private List<Layer> layers;

    public NeuralNetwork() {
        // Morphia Constructor
    }

    NeuralNetwork(final int inputSize, final List<Layer> layers) {
        this.inputSize = inputSize;
        this.layers = layers;
    }

    public static NeuralNetwork fromJson(final String json) {
        return GSON.fromJson(json, NeuralNetwork.class);
    }

    public static Builder builder(final int inputSize, final Activation activation) {
        return Builder.create(inputSize, activation);
    }

    public int getInputSize() {
        return inputSize;
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

    @Override
    public NeuralNetwork mutate(final double mutationRate) {
        final List<Layer> mutatedLayers = layers.stream()
                .map(layer -> layer.mutate(mutationRate))
                .collect(Collectors.toList());
        return new NeuralNetwork(inputSize, mutatedLayers);
    }

    @Override
    public NeuralNetwork crossover(final NeuralNetwork mate) {
        final List<Layer> crossoverLayers = IntStream.range(0, layers.size())
                .mapToObj(i -> layers.get(i).crossover(mate.layers.get(i)))
                .collect(Collectors.toList());
        return new NeuralNetwork(inputSize, crossoverLayers);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        NeuralNetwork that = (NeuralNetwork) object;

        if (getInputSize() != that.getInputSize()) return false;
        return layers.equals(that.layers);
    }

    @Override
    public int hashCode() {
        int result = getInputSize();
        result = 31 * result + layers.hashCode();
        return result;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public String toString() {
        return toJson();
    }
}