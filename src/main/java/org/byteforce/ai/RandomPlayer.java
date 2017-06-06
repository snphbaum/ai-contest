package org.byteforce.ai;

import java.util.Random;

import org.byteforce.ai.Action;
import org.byteforce.ai.ActionFactory;
import org.byteforce.ai.Player;
import org.byteforce.ai.State;


/**
 * @author Philipp Baumgaertel
 */
public class RandomPlayer
    implements Player
{

    private Random rand;
    private ActionFactory actionFactory;
    private boolean isPlayerZero;

    public RandomPlayer(ActionFactory pActionFactory, boolean pIsPlayerZero) {
        actionFactory = pActionFactory;
        rand = new Random();
        isPlayerZero = pIsPlayerZero;
    }

    @Override
    public Action getAction(final State pState)
    {
        return actionFactory.get(rand.nextInt(actionFactory.getNumberOfActions()));
    }



    @Override
    public boolean isPlayerZero()
    {
        return isPlayerZero;
    }
}
