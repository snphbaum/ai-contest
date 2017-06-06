package org.byteforce.game.AiContest;

import java.util.Arrays;

import org.byteforce.ai.ActionFactory;
import org.byteforce.ai.AdversarialGameServer;
import org.byteforce.ai.DeepQLearning;
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
            Arrays.asList(300, 150,100), 0.0001);
        //TODO experiment with different Networkarchitectures
        AdversarialGameServer gameServer = new AdversarialGameServer(new RandomPlayer(actionFactory, false));
        //TODO not working as the Players don't kill each other ==> Simple player is to defensive
        //AdversarialGameServer gameServer = new AdversarialGameServer(new AiContestSimplePlayer(1));

        DeepQLearning dql = new DeepQLearning(actionFactory, stateFactory, networkFactory, gameServer);
        dql.learnFromExperience(() -> gameServer.playAgainst(new AiContestSimplePlayer(0), stateFactory),1000);
        dql.configureExperienceReplay(40, 100, 100);
        dql.learn(100000, false);
        System.out.println("Wins: " + dql.play(50000, false)+ "%");
    }
}