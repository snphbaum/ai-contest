package org.byteforce.ai;

import java.util.Random;

import org.byteforce.game.GridWorld;
import org.nd4j.linalg.api.ndarray.INDArray;


/**
 * Representing a state for reinforcement learning.
 * <p/>
 * This class is immutable.
 *
 * @author Philipp Baumgaertel
 */
public interface State {

    /**
     * @return Copy of the current state
     */
    State copy();



    /**
     * @return the reward of the current state.
     */
    double getReward();



    /**
     *
     * @return true if this is a final state
     */
    boolean isFinal();



    /**
     * @return true if the game has been won
     */
    boolean won();



    /**
     * The state is not altered by the action, but rather a new state is returned.
     *
     * @param action The action to take
     * @return The new state
     */
    State move(Action action);



    /**
     * Prints a visual representation of the state.
     */
    void print() ;



    /**
     * @returna linear representation of the state
     */
    INDArray getInputRepresentation();


}

