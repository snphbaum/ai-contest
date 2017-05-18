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

    Random rand;
    ActionFactory actionFactory;

    public RandomPlayer(ActionFactory pActionFactory) {
        actionFactory = pActionFactory;
        rand = new Random();

    }

    @Override
    public Action getAction(final State pState)
    {
        return actionFactory.get(rand.nextInt(actionFactory.getNumberOfActions()));
    }
}
