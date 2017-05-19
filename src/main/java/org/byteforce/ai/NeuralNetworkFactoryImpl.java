package org.byteforce.ai;

import java.util.List;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;


/**
 * @author Philipp Baumgaertel
 */
public class NeuralNetworkFactoryImpl
    implements NeuralNetworkFactory
{

    private int inputLength;

    private int outputLength;

    private List<Integer> layer;

    private double rate;

    public NeuralNetworkFactoryImpl(int pInputLength, int pOutputLength, List<Integer> pLayer, double pLearningRate)
    {
        inputLength = pInputLength;
        outputLength = pOutputLength;
        layer = pLayer;
        rate = pLearningRate;
    }



    public MultiLayerNetwork getNeuralNetwork()
    {

        int rngSeed = 123; // random number seed for reproducibility
        int i = 0;
        // @formatter:off
        NeuralNetConfiguration.ListBuilder lb = new NeuralNetConfiguration.Builder()
            .seed(rngSeed) //include a random seed for reproducibility
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT) // use stochastic gradient descent as an optimization algorithm
            .iterations(1)
            .activation(Activation.LEAKYRELU)
            .weightInit(WeightInit.RELU_UNIFORM)
            .learningRate(rate) //specify the learning rate
            .updater(Updater.RMSPROP).momentum(0.98) //specify the rate of change of the learning rate.
            .regularization(true).l2(rate * 0.005) // regularize learning model
            .list();

        lb = lb.layer(0, new DenseLayer.Builder() //create the first input layer.
            .nIn(inputLength) // depends on the number of possible states
            .nOut(layer.get(0))
            .build());

        for(i = 1; i < layer.size(); i++) {
            lb = lb.layer(i, new DenseLayer.Builder() //create the input layers.
            .nIn(layer.get(i-1))
            .nOut(layer.get(i))
            .build());
        }

         MultiLayerConfiguration conf = lb
             .layer(i, new OutputLayer.Builder(LossFunctions.LossFunction.MSE) //create output layer
                .activation(Activation.IDENTITY)
                .nIn(layer.get(i-1))
                .nOut(outputLength)
                .build())
            .pretrain(false).backprop(true) //use backpropagation to adjust weights
            .build();
        // @formatter:on

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        return model;
    }
}
