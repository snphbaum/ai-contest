package org.byteforce.ai;

import org.byteforce.ai.Action;


/**
 * @author Philipp Baumgaertel
 */
public interface GameServer
{
    //public Action exchangeAction(Action pAction);

    public State doAction(Action pAction, State pOldState);
}
