package com.cluttered.cryptocurrency.ann;

import com.cluttered.cryptocurrency.ga.GeneticElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.cluttered.cryptocurrency.RandomGenerator.*;
import static java.lang.Math.exp;

/**
 * @author cluttered.code@gmail.com
 */
public class Neuron implements GeneticElement<Neuron> {

    private static final Logger LOG = LoggerFactory.getLogger(Neuron.class);

    private final double bias;
    private final double leakage;
    private final List<Double> weights;

    private Neuron(final double bias, final double leakage, final List<Double> weights) {
        this.bias = bias;
        this.leakage = leakage;
        this.weights = weights;
    }

    public static Neuron generate(final int inputSize) {
        return new Neuron(randomBias(), randomLeakage(), randomWeights(inputSize));
    }

    public double fire(final List<Double> inputs) {
        final long startTimeNanos = System.nanoTime();
        LOG.debug("Fire Neuron");
        final double biasDotProduct = dotProductWithWeights(inputs) + bias;
        final double activation = exponentialLinearUnitActivation(biasDotProduct);
        LOG.trace("Neuron Time: {}nanos", System.nanoTime() - startTimeNanos);
        return activation;
    }

    private double exponentialLinearUnitActivation(final double biasDotProduct) {
        return leakage * (exp(biasDotProduct) - 1);
    }

    private double dotProductWithWeights(final List<Double> inputs) {
        if (inputs.size() != weights.size())
            throw new IllegalArgumentException("inputs (" + inputs.size() + ") and weights (" + weights.size() + ") must have the same number of elements");

        return IntStream.range(0, inputs.size())
                .mapToDouble(i -> inputs.get(i) * weights.get(i))
                .sum();
    }

    @Override
    public Neuron mutate(final double mutationRate) {
        final double mutatedBias = random() < mutationRate ? randomBias() : bias;
        final double mutatedLeakage = random() < mutationRate ? randomLeakage() : leakage;
        final List<Double> mutatedWeights = weights.stream()
                .map(weight -> random() < mutationRate ? randomWeight() : weight)
                .collect(Collectors.toList());
        return new Neuron(mutatedBias, mutatedLeakage, mutatedWeights);
    }

    @Override
    public Neuron crossover(final Neuron mate) {
        final double crossoverBias = coinFlip() ? mate.bias : bias;
        final double crossoverLeakage = coinFlip() ? mate.leakage : leakage;
        final List<Double> crossoverWeights = IntStream.range(0, weights.size())
                .mapToDouble(i -> coinFlip() ? mate.weights.get(i) : weights.get(i))
                .boxed()
                .collect(Collectors.toList());
        return new Neuron(crossoverBias, crossoverLeakage, crossoverWeights);
    }
}