package org.byteforce.game.AiContest;

import org.byteforce.ai.Action;


/**
 * @author Philipp Baumgaertel
 */
public interface GameServer
{
    public Action exchangeAction(Action pAction);
}
