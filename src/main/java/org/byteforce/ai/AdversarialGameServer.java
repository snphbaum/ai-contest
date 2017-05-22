package org.byteforce.ai;

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
    boolean asPlayerZero;


    public AdversarialGameServer(Player pPlayer, boolean pAsPlayerZero)
    {
        player = pPlayer;
        asPlayerZero = pAsPlayerZero;
    }



    @Override
    public State doAction(final Action pAction, final State pOldState)
    {
        Action adversaryAction = player.getAction(pOldState);
        if(asPlayerZero){
            return pOldState.move(adversaryAction, pAction);
        }
        else {
            return pOldState.move(pAction, adversaryAction);
        }

    }
}
