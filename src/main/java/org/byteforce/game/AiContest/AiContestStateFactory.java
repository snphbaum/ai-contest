package org.byteforce.game.AiContest;

import org.byteforce.ai.State;
import org.byteforce.ai.StateFactory;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestStateFactory
    implements StateFactory
{

    LocalGameServerImpl gameServer;

    public AiContestStateFactory(LocalGameServerImpl pGameServer){
        gameServer = pGameServer;
    }

    @Override
    public State getState()
    {
        return new AiContestState(gameServer);
    }



    @Override
    public int getInputLength()
    {
        return AiContestState.FIELD_HEIGHT * AiContestState.FIELD_WIDTH * AiContestState.OBJECT_TYPES;
    }
}