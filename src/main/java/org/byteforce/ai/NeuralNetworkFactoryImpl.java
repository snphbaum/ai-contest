package org.byteforce.ai;

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
 *
 * @author Philipp Baumgaertel
 */
public class NeuralNetworkFactoryImpl
    implements NeuralNetworkFactory
{

    public MultiLayerNetwork getNeuralNetwork(int pInputLength, int pOutputLength)
    {

        //TODO make number of layers and number of neurons configurable
        //perhaps even a convolutional network?
        int rngSeed = 123; // random number seed for reproducibility
        double rate = 0.0015; // learning rate of the net

        // @formatter:off
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(rngSeed) //include a random seed for reproducibility
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT) // use stochastic gradient descent as an optimization algorithm
            .iterations(1)
            .activation(Activation.LEAKYRELU)
            .weightInit(WeightInit.RELU_UNIFORM)
            .learningRate(rate) //specify the learning rate
            .updater(Updater.RMSPROP).momentum(0.98) //specify the rate of change of the learning rate.
            .regularization(true).l2(rate * 0.005) // regularize learning model
            .list()
            .layer(0, new DenseLayer.Builder() //create the first input layer.
                .nIn(pInputLength) // depends on the number of possible states
                .nOut(164)
                .build())
            .layer(1, new DenseLayer.Builder() //create the second input layer
                .nIn(164)
                .nOut(150)
                .build())
            // .layer(2, new DenseLayer.Builder() //create the second input layer
            //     .nIn(150)
            //     .nOut(150)
            //     .build())
            .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE) //create hidden layer
                .activation(Activation.IDENTITY)
                .nIn(150)
                .nOut(pOutputLength)
                .build())
            .pretrain(false).backprop(true) //use backpropagation to adjust weights
            .build();
        // @formatter:on

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        return model;
    }

}
