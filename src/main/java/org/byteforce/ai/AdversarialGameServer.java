package org.byteforce.ai;

import java.util.concurrent.Exchanger;

import org.byteforce.ai.Action;
import org.byteforce.ai.GameServer;
import org.byteforce.ai.Player;
import org.byteforce.ai.State;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * @author Philipp Baumgaertel
 */
public class AdversarialGameServer
    implements GameServer
{
    // TODO use in threaded game server
    // Exchanger<Action> exchanger = new Exchanger<Action>();
    //
    //
    //
    // private Action exchangeAction(Action pAction)
    // {
    //     try {
    //         return exchanger.exchange(pAction);
    //     }
    //     catch (InterruptedException pE) {
    //         throw new RuntimeException();
    //     }
    // }



    Player player;
    boolean asPlayerOne;


    public AdversarialGameServer(Player pPlayer, boolean pAsPlayerOne)
    {
        player = pPlayer;
        asPlayerOne = pAsPlayerOne;
    }



    @Override
    public State doAction(final Action pAction, final State pOldState)
    {
        Action adversaryAction = player.getAction(pOldState);
        if(asPlayerOne){
            return pOldState.move(adversaryAction, pAction);
        }
        else {
            return pOldState.move(pAction, adversaryAction);
        }

    }
}
