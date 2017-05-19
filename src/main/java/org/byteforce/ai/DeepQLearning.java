package org.byteforce.ai;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
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

    private GameServer gameServer;

    private int batchsize = 40;

    private int experienceBuffer = 100;

    private int percentEpochsWithoutReplay = 95;

    private double epsilon = 1;
        // 1 = exploration (random), 0 = exploitation (use model), gets decreased during learning

    private double gamma = 0.9; // eagerness 0 = prioritize early rewards, 1 = late rewards

    private double alpha = 0.1; // learning rate of the q learner

    private double endEpsilon = 0.1; // End value for the decrease of epsilon

    private int player;

    public DeepQLearning(ActionFactory pActionFactory, StateFactory pStateFactory, NeuralNetworkFactory pNetworkFactory,
        GameServer pGameServer, int pPlayer)
    {
        actionFactory = pActionFactory;
        stateFactory = pStateFactory;
        networkFactory = pNetworkFactory;
        gameServer = pGameServer;
        player = pPlayer;
        model = networkFactory.getNeuralNetwork();
    }



    /**
     * @param pBatchsize Defines how many memories get replayed during experience replay (Default: 40)
     * @param pExperienceBuffer Defines how many memories are stored as experience (Default: 100)
     * @param pPercentEpochsWithoutReplay Defines how many epochs (in percent) are played without replay. This is for
     * performance reasons as the replay is very slow (Default: 95)
     */
    public void configureExperienceReplay(int pBatchsize, int pExperienceBuffer, int pPercentEpochsWithoutReplay)
    {
        batchsize = pBatchsize;
        experienceBuffer = pExperienceBuffer;
        percentEpochsWithoutReplay = pPercentEpochsWithoutReplay;
    }



    /**
     * @param pStartEpsilon Start value for the exploration vs exploitation balance: 1 = exploration (random), 0 =
     * exploitation (use model), gets decreased during learning (Default: 1)
     * @param pEndEpsilon End value for the exploration vs exploitation balance. The balance gets decreased linearly.
     * (Default: 0.1)
     * @param pAlpha Learning rate of the q learner (Default: 0.1)
     * @param pGamma Eagerness: 0 = prioritize early rewards, 1 = late rewards (Default: 0.9)
     */
    public void configureLearning(double pStartEpsilon, double pEndEpsilon, double pAlpha, double pGamma)
    {
        epsilon = pStartEpsilon;
        endEpsilon = pEndEpsilon;
        alpha = pAlpha;
        gamma = pGamma;
    }



    private class Memory
    {
        Memory(State pState, Action pAction, double pReward, State pNewState)
        {
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



    public void learn(int pNumEpochs, boolean pMonitor)
    {
        if(pMonitor) {
            UIServer uiServer = UIServer.getInstance();

            //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.

            StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

            //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
            uiServer.attach(statsStorage);

            //Then add the StatsListener to collect this information from the network, as it trains
            model.setListeners(new StatsListener(statsStorage));
        }
        // TODO experiment with experience replay
        // TODO implement prioritized experience replay (with bad predicted value vs real value difference)
        Random rand = new Random();
        int numEpochsWithoutReplay = pNumEpochs * percentEpochsWithoutReplay / 100;
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
                State new_s = gameServer.doAction(action, s); // .move(action);

                INDArray newQVal = model.output(new_s.getInputRepresentation());
                double maxQ = newQVal.maxNumber().doubleValue();
                double reward = new_s.getReward();
                //Experience replay
                // FIrst we train the model without replay as that is very slow
                //TODO rewrite if cascade without duplicate code
                //TODO debug experience replay as this actually leads to worse results
                if (i < numEpochsWithoutReplay) {
                    double q = qVal.getDouble(a);
                    double update = (new_s.isFinal()) ? reward : q + alpha * (reward + (gamma * maxQ) - q);
                    qVal.putScalar(a, update);
                    model.fit(s.getInputRepresentation(), qVal);
                }
                else {
                    if (experience.size() < experienceBuffer) {
                        //Fill Buffer
                        experience.add(new Memory(s, action, reward, new_s));
                        double q = qVal.getDouble(a);
                        double update = (new_s.isFinal()) ? reward : q + alpha * (reward + (gamma * maxQ) - q);
                        qVal.putScalar(a, update);
                        model.fit(s.getInputRepresentation(), qVal);
                    }
                    else {
                        Collections.shuffle(experience);
                        experience.set(0, new Memory(s, action, reward, new_s));
                        INDArray inputList = Nd4j.zeros(batchsize, stateFactory.getInputLength());
                        INDArray outputList = Nd4j.zeros(batchsize, actionFactory.getNumberOfActions());
                        for (int j = 0; j < batchsize; j++) {

                            Memory memory = experience.get(j);
                            INDArray mQVal = model.output(memory.state.getInputRepresentation());
                            INDArray mNewQVal = model.output(memory.newState.getInputRepresentation());
                            double mMaxQ = newQVal.maxNumber().doubleValue();
                            double mReward = memory.newState.getReward();
                            double q = mQVal.getDouble(memory.action.getType());
                            double update =
                                (memory.newState.isFinal()) ? mReward : q + alpha * (mReward + (gamma * mMaxQ) - q);
                            mQVal.putScalar(memory.action.getType(), update);
                            inputList.putRow(j, memory.state.getInputRepresentation());
                            outputList.putRow(j, mQVal);
                        }
                        model.fit(inputList, outputList);
                    }
                }

                s = new_s;
                if (epsilon > endEpsilon) {
                    epsilon -= (1.0 / pNumEpochs);
                }
            }
            System.out.println(i * 100.0 / pNumEpochs + "%");
            if(i % 1000 == 0){
                System.out.println("Wins: " + play(1000, false)+ "%");
            }
        }

        try {
            ModelSerializer.writeModel(model, new File("MyMultiLayerNetwork.zip"), true);
        }
        catch (IOException pE) {
            // TODO handle java.io.IOException
        }

    }



    public double play(int pNumEpochs, boolean showSteps)
    {
        //Replay
        int wins = 0;
        for (int i = 0; i < pNumEpochs; i++) {

            State s = stateFactory.getState();
            int c = 0;
            while (!s.isFinal()) {
                INDArray qVal = model.output(s.getInputRepresentation());
                int a = Nd4j.argMax(qVal).getInt(0);
                //State new_s = s.move(actionFactory.get(a));
                State new_s = gameServer.doAction(actionFactory.get(a), s);
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

        return wins * 100.0 / pNumEpochs;
    }
}
