package org.byteforce.ai;

import java.util.Random;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

//TODO implement adversarial games

/**
 * This class implements the learning algorithm
 *
 * Nd4j needs MKL ==> easiest installation
 * Add to Path D:\Software\IntelSWTools\compilers_and_libraries_2017.2.187\windows\redist\intel64_win\mkl\
 * JavaCpp best with clang (installer adds it to the path)
 * Anaconda has to be deinstalled (causes a clash with MKL)
 * Used BLAS Library can be checked via Nd4j API
 * CpuBlas.getBlasVendorId();
 * @author Philipp Baumgaertel
 */
public class DeepQLearning
{
    private ActionFactory actionFactory;

    private StateFactory stateFactory;

    private MultiLayerNetwork model;



    public DeepQLearning(ActionFactory pActionFactory, StateFactory pStateFactory)
    {
        actionFactory = pActionFactory;
        stateFactory = pStateFactory;
        initModel();
    }



    private void initModel()
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
                .nIn(stateFactory.getInputLength()) // depends on the number of possible states
                .nOut(164)
                .build())
            .layer(1, new DenseLayer.Builder() //create the second input layer
                .nIn(164)
                .nOut(150)
                .build())
            .layer(2, new DenseLayer.Builder() //create the second input layer
                .nIn(150)
                .nOut(150)
                .build())
            .layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.MSE) //create hidden layer
                .activation(Activation.IDENTITY)
                .nIn(150)
                .nOut(actionFactory.getNumberOfActions())
                .build())
            .pretrain(false).backprop(true) //use backpropagation to adjust weights
            .build();
        // @formatter:on

        model = new MultiLayerNetwork(conf);
        model.init();
    }



    public void learn(int pNumEpochs)
    {
        // TODO experiment with experience replay
        Random rand = new Random();
        double epsilon = 1; // 1 = exploration (random), 0 = exploitation (use model), gets decreased during learning
        double gamma = 0.9; // eagerness 0 = prioritize early rewards, 1 = late rewards
        double alpha = 0.1; // learning rate of the q learner

        // http://outlace.com/rlpart3.html
        // Learn
        for (int i = 0; i < pNumEpochs; i++) {
            State s = stateFactory.getState();
            while (!s.isFinal()) {
                INDArray qVal = model.output(s.getInputRepresentation());
                int a;
                if (Math.random() < epsilon) {
                    a = rand.nextInt(actionFactory.getNumberOfActions());
                }
                else {
                    a = Nd4j.argMax(qVal).getInt(0);
                }
                State new_s = s.move(actionFactory.get(a));

                INDArray newQVal = model.output(new_s.getInputRepresentation());
                double maxQ = newQVal.maxNumber().doubleValue();
                double reward = new_s.getReward();
                double q = qVal.getDouble(a);
                double update = (new_s.isFinal()) ? reward : q + alpha * (reward + (gamma * maxQ) - q);
                qVal.putScalar(a, update);
                model.fit(s.getInputRepresentation(), qVal);
                s = new_s;
                if (epsilon > 0.1)
                    epsilon -= (1.0 / pNumEpochs);
            }
            System.out.println(i * 100.0 / pNumEpochs + "%");
        }
    }



    public void play(int pNumEpochs, boolean showSteps)
    {
        //Replay
        int wins = 0;
        for (int i = 0; i < pNumEpochs; i++) {

            State s = stateFactory.getState();
            int c = 0;
            while (!s.isFinal()) {
                INDArray qVal = model.output(s.getInputRepresentation());
                int a = Nd4j.argMax(qVal).getInt(0);
                State new_s = s.move(actionFactory.get(a));

                double reward = new_s.getReward();
                if(showSteps) {
                    s.print();
                    System.out.println(qVal);
                    System.out.println(actionFactory.get(a));
                    System.out.println("Reward: " + reward);
                }
                s = new_s;
                c++;
                if (c > 10) {
                    if(showSteps) {
                        System.out.println("Too many moves");
                    }
                    break;
                }
            }
            if (s.won()) {
                wins++;
            }
        }
        System.out.println("Wins: " + wins*100.0/pNumEpochs + "%");
    }

}
