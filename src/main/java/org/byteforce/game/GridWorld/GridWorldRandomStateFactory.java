package org.byteforce.game.GridWorld;

import org.byteforce.ai.State;
import org.byteforce.ai.StateFactory;


/**
 * @author Philipp Baumgaertel
 */
public class GridWorldRandomStateFactory
    implements StateFactory
{

    @Override
    public State getState()
    {
        return new GridWorldState(true);
    }



    @Override
    public int getInputLength()
    {
        return 64;
    }
}