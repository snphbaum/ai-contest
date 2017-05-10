package org.byteforce.game.AiContest;

import java.util.concurrent.Exchanger;

import org.byteforce.ai.Action;


/**
 * @author Philipp Baumgaertel
 */
public class LocalGameServerImpl
    implements GameServer
{
    Exchanger<Action> exchanger = new Exchanger<Action>();

    public Action exchangeAction(Action pAction){
        try {
            return exchanger.exchange(pAction);
        }
        catch (InterruptedException pE) {
            // TODO handle java.lang.InterruptedException
            throw new RuntimeException();
        }
    }

}
