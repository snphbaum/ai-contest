package org.byteforce.game.GridWorld;

import org.byteforce.ai.Action;


/**
 * @author Philipp Baumgaertel
 */
public enum GridWorldAction
    implements Action
{
    UP(0),
    DOWN(1),
    LEFT(2),
    RIGHT(3);
    int type;
    GridWorldAction(int pType){
        type = pType;
    }
    public int getType(){
        return type;
    }
}