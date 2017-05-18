package org.byteforce.game.AiContest;

import org.byteforce.ai.AdversarialGameServer;
import org.byteforce.ai.State;
import org.byteforce.ai.StateFactory;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestStateFactory
    implements StateFactory
{



    @Override
    public State getState()
    {
        return new AiContestState();
    }



    @Override
    public int getInputLength()
    {
        return AiContestState.FIELD_HEIGHT * AiContestState.FIELD_WIDTH * AiContestState.OBJECT_TYPES;
    }
}