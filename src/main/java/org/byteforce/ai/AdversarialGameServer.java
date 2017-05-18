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
    //         // TODO handle java.lang.InterruptedException
    //         throw new RuntimeException();
    //     }
    // }



    Player player;



    public AdversarialGameServer(Player pPlayer)
    {
        player = pPlayer;
    }



    @Override
    public State doAction(final Action pAction, final State pOldState)
    {
        Action adversaryAction = player.getAction(pOldState);

        return pOldState.move(pAction, adversaryAction);

    }
}
