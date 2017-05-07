package org.byteforce.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

//TODO implement adversarial games



/**
 * This class implements the learning algorithm
 * Nd4j needs MKL ==> easiest installation
 * Add to Path D:\Software\IntelSWTools\compilers_and_libraries_2017.2.187\windows\redist\intel64_win\mkl\
 * JavaCpp best with clang (installer adds it to the path)
 * Anaconda has to be deinstalled (causes a clash with MKL)
 * Used BLAS Library can be checked via Nd4j API
 * CpuBlas.getBlasVendorId();
 *
 * @author Philipp Baumgaertel
 */
public class DeepQLearning
{
    private ActionFactory actionFactory;

    private StateFactory stateFactory;

    private MultiLayerNetwork model;

    private NeuralNetworkFactory networkFactory;



    public DeepQLearning(ActionFactory pActionFactory, StateFactory pStateFactory, NeuralNetworkFactory pNetworkFactory)
    {
        actionFactory = pActionFactory;
        stateFactory = pStateFactory;
        networkFactory = pNetworkFactory;
        model = networkFactory.getNeuralNetwork(stateFactory.getInputLength(), actionFactory.getNumberOfActions());
    }



    private class Memory
    {
        Memory(State pState, Action pAction, double pReward, State pNewState){
            state = pState;
            action = pAction;
            reward = pReward;
            newState = pNewState;
        }
        State state;
        Action action;
        double reward;
        State newState;
    }



    public void learn(int pNumEpochs)
    {
        // TODO experiment with experience replay
        Random rand = new Random();
        double epsilon = 1; // 1 = exploration (random), 0 = exploitation (use model), gets decreased during learning
        double gamma = 0.9; // eagerness 0 = prioritize early rewards, 1 = late rewards
        double alpha = 0.1; // learning rate of the q learner
        int batchsize = 40;
        int experienceBuffer = 100;
        List<Memory> experience = new ArrayList<>(batchsize);
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
                Action action = actionFactory.get(a);
                State new_s = s.move(action);

                INDArray newQVal = model.output(new_s.getInputRepresentation());
                double maxQ = newQVal.maxNumber().doubleValue();
                double reward = new_s.getReward();
                //Experience replay
                if(experience.size() < experienceBuffer){
                    //Fill Buffer
                    experience.add(new Memory(s,action, reward ,new_s));
                    double q = qVal.getDouble(a);
                    double update = (new_s.isFinal()) ? reward : q + alpha * (reward + (gamma * maxQ) - q);
                    qVal.putScalar(a, update);
                    model.fit(s.getInputRepresentation(), qVal);

                }else {
                    Collections.shuffle(experience);
                    experience.set(0,new Memory(s,action, reward ,new_s));
                    INDArray inputList = Nd4j.zeros(batchsize, stateFactory.getInputLength());
                    INDArray outputList = Nd4j.zeros(batchsize, actionFactory.getNumberOfActions());
                    for (int j = 0; j < batchsize; j++) {

                        Memory memory = experience.get(j);
                        INDArray mQVal = model.output(memory.state.getInputRepresentation());
                        INDArray mNewQVal = model.output(memory.newState.getInputRepresentation());
                        double mMaxQ = newQVal.maxNumber().doubleValue();
                        double mReward = memory.newState.getReward();
                        double q = mQVal.getDouble(memory.action.getType());
                        double update = (memory.newState.isFinal()) ? mReward : q + alpha * (mReward + (gamma * mMaxQ) - q);
                        mQVal.putScalar(a, update);
                        inputList.putRow(j,memory.state.getInputRepresentation());
                        outputList.putRow(j,mQVal);
                        model.fit(inputList,outputList);
                    }
                }

                s = new_s;
                if (epsilon > 0.1) {
                    epsilon -= (1.0 / pNumEpochs);
                }
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
                if (showSteps) {
                    s.print();
                    System.out.println(qVal);
                    System.out.println(actionFactory.get(a));
                    System.out.println("Reward: " + reward);
                }
                s = new_s;
                c++;
                if (c > 10) {
                    if (showSteps) {
                        System.out.println("Too many moves");
                    }
                    break;
                }
            }
            if (s.won()) {
                wins++;
            }
        }
        System.out.println("Wins: " + wins * 100.0 / pNumEpochs + "%");
    }
}
