package org.byteforce.game.AiContest;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.byteforce.ai.Action;
import org.byteforce.ai.ActionFactory;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestActionFactory
    implements ActionFactory
{


    private static final Map<Integer,AiContestAction> lookup
        = new HashMap<Integer,AiContestAction>();

    static {
        for(AiContestAction s : EnumSet.allOf(AiContestAction.class)) {
            lookup.put(s.getType(), s);
        }
    }

    @Override
    public Action get(final int i)
    {
        return lookup.get(i);
    }

    @Override
    public int getNumberOfActions()
    {
        return 5;
    }
}