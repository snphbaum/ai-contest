package org.byteforce.ai;

/**
 * @author Philipp Baumgaertel
 */
public class Memory
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
