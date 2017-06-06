package org.byteforce.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


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


    public AdversarialGameServer(Player pPlayer)
    {
        player = pPlayer;
    }

    public List<Memory> playAgainst(Player pPlayer, StateFactory pStateFactory){
        List<Memory> result = new ArrayList<>();
        State s = pStateFactory.getState();
        while (!s.isFinal()) {
            Action a = pPlayer.getAction(s);
            State new_s = doAction(a, s);
            result.add(new Memory(s,a,s.getReward(),new_s));
            s = new_s;
        }
        return result;
    }

    @Override
    public State doAction(final Action pAction, final State pOldState)
    {
        Action adversaryAction = player.getAction(pOldState);
        if(player.isPlayerZero()){
            return pOldState.move(adversaryAction, pAction);
        }
        else {
            return pOldState.move(pAction, adversaryAction);
        }

    }
}
