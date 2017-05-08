package org.byteforce.game.AiContest;

import org.byteforce.ai.Action;


/**
 * @author Philipp Baumgaertel
 */
public enum AiContestAction
    implements Action
{
    UP(0),
    DOWN(1),
    LEFT(2),
    RIGHT(3),
    DROP(4);
    int type;
    AiContestAction(int pType){
        type = pType;
    }
    public int getType(){
        return type;
    }
}