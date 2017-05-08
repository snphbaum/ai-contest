package org.byteforce.game.GridWorld;

import org.byteforce.ai.Action;
import org.byteforce.ai.ActionFactory;


/**
 * @author Philipp Baumgaertel
 */
public class GridWorldActionFactory
    implements ActionFactory
{

    @Override
    public Action get(final int i)
    {
        if (i == 0) {
            return GridWorldAction.UP;
        }
        else if (i == 1) {
            return GridWorldAction.DOWN;
        }
        else if (i == 2) {
            return GridWorldAction.LEFT;
        }
        else {
            return GridWorldAction.RIGHT;
        }
    }



    @Override
    public int getNumberOfActions()
    {
        return 4;
    }
}