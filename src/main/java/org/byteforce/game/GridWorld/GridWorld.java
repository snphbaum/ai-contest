package org.byteforce.game.GridWorld;

import org.byteforce.ai.ActionFactory;
import org.byteforce.ai.DeepQLearning;
import org.byteforce.ai.GameServer;
import org.byteforce.ai.SinglePlayerGameServer;
import org.byteforce.ai.NeuralNetworkFactory;
import org.byteforce.ai.NeuralNetworkFactoryImpl;
import org.byteforce.ai.StateFactory;


/**
 * @author Philipp Baumgaertel
 */
public class GridWorld
{

    public static void main(String[] args){

        // Inputs for the function
       // System.out.println((new CpuBlas()).getBlasVendor());
        ActionFactory actionFactory = new GridWorldActionFactory();
        StateFactory stateFactory = new GridWorldRandomStateFactory();
        NeuralNetworkFactory networkFactory = new NeuralNetworkFactoryImpl();
        GameServer gameServer = new SinglePlayerGameServer();
        DeepQLearning dql = new DeepQLearning(actionFactory, stateFactory, networkFactory, gameServer ,0);
        dql.configureExperienceReplay(40,100,100);
        dql.learn(50000);
        dql.play(50000, false);

    }
}
