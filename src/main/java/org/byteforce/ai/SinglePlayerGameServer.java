package org.byteforce.ai;

import org.byteforce.ai.Action;
import org.byteforce.ai.GameServer;
import org.byteforce.ai.State;


/**
 * @author Philipp Baumgaertel
 */
public class SinglePlayerGameServer
    implements GameServer
{
    @Override
    public State doAction(final Action pAction, final State pOldState)
    {
        return pOldState.move(pAction);
    }
}
