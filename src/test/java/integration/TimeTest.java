package integration;

import com.cluttered.cryptocurrency.ann.Layer;
import com.cluttered.cryptocurrency.ann.NeuralNetwork;
import com.cluttered.cryptocurrency.ann.neuron.Neuron;
import com.cluttered.cryptocurrency.ann.neuron.NeuronBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author cluttered.code@gmail.com
 */
public class TimeTest {

    private static Logger LOG = LoggerFactory.getLogger(TimeTest.class);

    private static final int INPUT_SETS = 1000;
    private static final int INPUTS = 200;
    private static final int HIDDEN_NODES_1 = 100;
    private static final int HIDDEN_NODES_2 = 50;
    private static final int OUTPUTS = 2;

    public static NeuralNetwork build() {
        final List<Neuron> hiddenNeurons1 = IntStream.range(0, HIDDEN_NODES_1)
                .mapToObj(i -> Neuron.random(INPUTS))
                .collect(Collectors.toList());
        final Layer hiddenLayer1 = new Layer(hiddenNeurons1);

        final List<Neuron> hiddenNeurons2 = IntStream.range(0, HIDDEN_NODES_2)
                .mapToObj(i -> Neuron.random(HIDDEN_NODES_1))
                .collect(Collectors.toList());
        final Layer hiddenLayer2 = new Layer(hiddenNeurons2);

        final List<Neuron> outputNeurons = IntStream.range(0, OUTPUTS)
                .mapToObj(i -> Neuron.random(HIDDEN_NODES_2))
                .collect(Collectors.toList());
        final Layer outputLayer = new Layer(outputNeurons);

        return new NeuralNetwork(INPUTS, Arrays.asList(hiddenLayer1, hiddenLayer2, outputLayer));
    }

    public static void main(final String[] args) {
        LOG.info("building NeuralNetwork");
        final NeuralNetwork neuralNetwork = build();
        LOG.info("building Inputs");
        final List<List<Double>> inputSets = IntStream.range(0, INPUT_SETS)
                .parallel()
                .mapToObj(i -> IntStream.range(0, INPUTS)
                        .mapToDouble(j -> Math.random())
                        .boxed()
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        final Random random = new Random();
        final int oneYearOfFifteenMinuteIntervals = 4 * 24 * 7 * 52;
        final long startTimeMillis = System.currentTimeMillis();
        IntStream.range(0, oneYearOfFifteenMinuteIntervals)
                .forEach(i -> neuralNetwork.fire(inputSets.get(random.nextInt(INPUT_SETS))));
        LOG.error("Test Time: {}ms", System.currentTimeMillis() - startTimeMillis);
    }
}