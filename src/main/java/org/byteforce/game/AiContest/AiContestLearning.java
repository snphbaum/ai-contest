package org.byteforce.game.AiContest;

import java.util.Arrays;

import org.byteforce.ai.ActionFactory;
import org.byteforce.ai.AdversarialGameServer;
import org.byteforce.ai.DeepQLearning;
import org.byteforce.ai.GameServer;
import org.byteforce.ai.NeuralNetworkFactory;
import org.byteforce.ai.NeuralNetworkFactoryImpl;
import org.byteforce.ai.RandomPlayer;
import org.byteforce.ai.StateFactory;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestLearning
{
    public static void main(String[] args)
    {

        // Inputs for the function
        // System.out.println((new CpuBlas()).getBlasVendor());
        ActionFactory actionFactory = new AiContestActionFactory();
        StateFactory stateFactory = new AiContestStateFactory();
        NeuralNetworkFactory networkFactory = new NeuralNetworkFactoryImpl(stateFactory.getInputLength(), actionFactory.getNumberOfActions(),
            Arrays.asList(500, 300), 0.15);
        //TODO experiment with different Architectures
        GameServer gameServer = new AdversarialGameServer(new RandomPlayer(actionFactory), false);
        DeepQLearning dql = new DeepQLearning(actionFactory, stateFactory, networkFactory, gameServer, 0);
        dql.configureExperienceReplay(40, 100, 100);
        dql.learn(50000, false);
        System.out.println("Wins: " + dql.play(50000, false)+ "%");
    }
}