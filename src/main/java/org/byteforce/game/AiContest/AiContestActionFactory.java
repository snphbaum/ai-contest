package org.byteforce.game.AiContest;

import org.byteforce.ai.Action;
import org.byteforce.ai.ActionFactory;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestActionFactory
    implements ActionFactory
{

    @Override
    public Action get(final int i)
    {
        if (i == 0) {
            return AiContestAction.UP;
        }
        else if (i == 1) {
            return AiContestAction.DOWN;
        }
        else if (i == 2) {
            return AiContestAction.LEFT;
        }
        else if (i == 3) {
            return AiContestAction.RIGHT;
        }
        else {
            return AiContestAction.DROP;
        }
    }

    @Override
    public int getNumberOfActions()
    {
        return 5;
    }
}