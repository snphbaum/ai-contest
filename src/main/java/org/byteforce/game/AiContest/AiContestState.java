package org.byteforce.game.AiContest;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.byteforce.ai.Action;
import org.byteforce.ai.State;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestState
    implements State
{

    static final int FIELD_WIDTH = 7;

    static final int FIELD_HEIGHT = 7;

    static final int OBJECT_TYPES = 12; //(ObjectTypes + 3 extra layers per bomb for ticking)



    static class Player
    {
        int x;

        int y;



        Player(int pY, int pX)
        {
            x = pX;
            y = pY;
        }



        Player copy()
        {
            return new Player(y, x);
        }
    }



    static class Bomb
    {
        int x;

        int y;

        int countDown;



        Bomb(int pY, int pX)
        {
            x = pX;
            y = pY;
            countDown = 3;
        }



        private Bomb(int pY, int pX, int pCountDown)
        {
            x = pX;
            y = pY;
            countDown = pCountDown;
        }



        Bomb copy()
        {
            return new Bomb(y, x, countDown);
        }



        public void tick()
        {
            countDown--;
        }



        public boolean isExploded()
        {
            return countDown <= 0;
        }
    }



    enum ObjectType
    {
        PLAYER1(0),
        PLAYER2(1),
        WALL(2),
        CRATE(3),
        BOMB1_3(4),
        BOMB1_2(5),
        BOMB1_1(6),
        BOMB1_0(7),
        BOMB2_3(8),
        BOMB2_2(9),
        BOMB2_1(10),
        BOMB2_0(11);

        int type;

        private static final Map<Integer, ObjectType> lookup = new HashMap<Integer, ObjectType>();

        static {
            for (ObjectType s : EnumSet.allOf(ObjectType.class)) {
                lookup.put(s.type, s);
            }
        }

        ObjectType(int pType)
        {
            type = pType;
        }



        static ObjectType get(int i)
        {
            return lookup.get(i);
        }
    }



    //For convenience and performance, some information is stored redundantly
    //In a grid for the AI and in special classes for evaluating the state
    INDArray grid;

    Player player1;

    Player player2;

    Bomb bomb1;

    Bomb bomb2;



    public AiContestState()
    {
        //TODO optionally create random crates
        grid = Nd4j.zeros(FIELD_HEIGHT, FIELD_WIDTH,
            OBJECT_TYPES);  //X x Y x (ObjectTypes + 3 extra layers per bomb for ticking)
        /* Putting the Walls in the following structure
         *  ---------------
         *  | | | | | | | |
         *  | |X| |X| |X| |
         *  | | | | | | | |
         *  | |X| |X| |X| |
         *  | | | | | | | |
         *  | |X| |X| |X| |
         *  | | | | | | | |
         *  ---------------
         */

        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                if (x % 2 == 1 && y % 2 == 1) {
                    grid.putScalar(new int[]{y, x, ObjectType.WALL.type}, 1);
                }
            }
        }
        // Putting the players in opposite corners
        grid.putScalar(new int[]{0, 0, ObjectType.PLAYER1.type}, 1);
        grid.putScalar(new int[]{6, 6, ObjectType.PLAYER2.type}, 1);
        player1 = new Player(0, 0);
        player2 = new Player(6, 6);
    }



    AiContestState(INDArray pGrid, Player pPlayer1, Player pPlayer2, Bomb pBomb1, Bomb pBomb2)
    {
        grid = pGrid;
        player1 = pPlayer1;
        player2 = pPlayer2;
        bomb1 = pBomb1;
        bomb2 = pBomb2;
    }



    public State copy()
    {
        return new AiContestState(grid.dup(), player1.copy(), player2.copy(), bomb1.copy(), bomb2.copy());
    }



    public double getReward()
    {
        //-10 for loosing when you are too close to an exploding bomb
        if (grid.getInt(player1.y, player1.x, ObjectType.BOMB1_0.type) == 1 || grid.getInt(player1.y, player1.x,
            ObjectType.BOMB2_0.type) == 1) {
            return -10;
        }
        //10 for winning when the opponent is too close to an exploding bomb
        else if ((grid.getInt(player2.y, player2.x, ObjectType.BOMB1_0.type) == 1 || grid.getInt(player2.y, player2.x,
            ObjectType.BOMB2_0.type) == 1)) {
            return 10;
        }
        else {
            return -1;
        }
    }



    public boolean isFinal()
    {
        if (getReward() != -1) {
            return true;
        }
        else {
            return false;
        }
    }



    public boolean won()
    {
        if (getReward() == 10) {
            return true;
        }
        else {
            return false;
        }
    }



    private void handlePos(int y, int x, AiContestState newState, Bomb bomb)
    {
        if (y >= 0 && y < FIELD_HEIGHT && x >= 0 && x < FIELD_WIDTH && newState.grid.getInt(y, x, ObjectType.WALL.type)
            != 1) {
            // No wall in between
            if (newState.grid.getInt(bomb.y + (y - bomb.y) / 2, bomb.x + (x - bomb.x) / 2, ObjectType.WALL.type) != 1) {
                newState.grid.putScalar(new int[]{y, x, ObjectType.BOMB1_0.type}, 1);
                newState.grid.putScalar(new int[]{y, x, ObjectType.CRATE.type}, 0);
            }
        }
    }



    private void cleanPos(int y, int x, AiContestState newState)
    {
        if (y >= 0 && y < FIELD_HEIGHT && x >= 0 && y < FIELD_WIDTH) {
            newState.grid.putScalar(new int[]{y, x, ObjectType.BOMB1_0.type}, 0);
        }
    }



    public State move(Action action)
    {
        AiContestState newState = new AiContestState(grid.dup(), player1.copy(), player2.copy(), bomb1, bomb2);

        if (bomb1 != null) {
            if (bomb1.isExploded()) {
                //Erase explosion
                newState.grid.putScalar(new int[]{bomb1.y, bomb1.x, ObjectType.BOMB1_0.type}, 0);
                for (int i = 1; i < 3; i++) {
                    cleanPos(bomb1.y - i, bomb1.x, newState);
                    cleanPos(bomb1.y + i, bomb1.x, newState);
                    cleanPos(bomb1.y, bomb1.x - i, newState);
                    cleanPos(bomb1.y, bomb1.x + i, newState);
                }
                bomb1 = null;
            }
            else {
                bomb1.tick();
                newState.grid.putScalar(new int[]{bomb1.y, bomb1.x, ObjectType.BOMB1_1.type},
                    newState.grid.getInt(bomb1.y, bomb1.x, ObjectType.BOMB1_2.type));
                newState.grid.putScalar(new int[]{bomb1.y, bomb1.x, ObjectType.BOMB1_2.type},
                    newState.grid.getInt(bomb1.y, bomb1.x, ObjectType.BOMB1_3.type));
                newState.grid.putScalar(new int[]{bomb1.y, bomb1.x, ObjectType.BOMB1_3.type}, 0);
                if (bomb1.isExploded()) {
                    //calculate explosion
                    newState.grid.putScalar(new int[]{bomb1.y, bomb1.x, ObjectType.BOMB1_0.type}, 1);
                    for (int i = 1; i < 3; i++) {
                        handlePos(bomb1.y - i, bomb1.x, newState, bomb1);
                        handlePos(bomb1.y + i, bomb1.x, newState, bomb1);
                        handlePos(bomb1.y, bomb1.x - i, newState, bomb1);
                        handlePos(bomb1.y, bomb1.x + i, newState, bomb1);
                    }
                }
            }
        }

        if (action.getType() == AiContestAction.UP.getType() && player1.y > 0 && grid.getInt(player1.y - 1, player1.x,
            ObjectType.WALL.type) < 1 && grid.getInt(player1.y - 1, player1.x, ObjectType.CRATE.type) < 1) {
            newState.grid.putScalar(new int[]{player1.y, player1.x, ObjectType.PLAYER1.type}, 0);
            newState.grid.putScalar(new int[]{player1.y - 1, player1.x, ObjectType.PLAYER1.type}, 1);
            newState.player1.y--;
        }
        else if (action.getType() == AiContestAction.DOWN.getType() && player1.y < FIELD_HEIGHT - 1 && grid.getInt(
            player1.y + 1, player1.x, ObjectType.WALL.type) < 1 && grid.getInt(player1.y + 1, player1.x,
            ObjectType.CRATE.type) < 1) {
            newState.grid.putScalar(new int[]{player1.y, player1.x, ObjectType.PLAYER1.type}, 0);
            newState.grid.putScalar(new int[]{player1.y + 1, player1.x, ObjectType.PLAYER1.type}, 1);
            newState.player1.y++;
        }
        else if (action.getType() == AiContestAction.LEFT.getType() && player1.x > 0 && grid.getInt(player1.y,
            player1.x - 1, ObjectType.WALL.type) < 1 && grid.getInt(player1.y, player1.x - 1, ObjectType.CRATE.type)
            < 1) {
            newState.grid.putScalar(new int[]{player1.y, player1.x, ObjectType.PLAYER1.type}, 0);
            newState.grid.putScalar(new int[]{player1.y, player1.x - 1, ObjectType.PLAYER1.type}, 1);
            newState.player1.x--;
        }
        else if (action.getType() == AiContestAction.RIGHT.getType() && player1.x < FIELD_WIDTH - 1 && grid.getInt(
            player1.y, player1.x + 1, ObjectType.WALL.type) < 1 && grid.getInt(player1.y, player1.x + 1,
            ObjectType.CRATE.type) < 1) {
            newState.grid.putScalar(new int[]{player1.y, player1.x, ObjectType.PLAYER1.type}, 0);
            newState.grid.putScalar(new int[]{player1.y, player1.x + 1, ObjectType.PLAYER1.type}, 1);
            newState.player1.x++;
        }
        else if (action.getType() == AiContestAction.DROP.getType() && bomb1 == null) {
            bomb1 = new Bomb(player1.y, player1.x);
            newState.bomb1 = bomb1;
            newState.grid.putScalar(new int[]{bomb1.y, bomb1.x, ObjectType.BOMB1_3.type}, 1);
        }
        // TODO: the enemy also needs to make a move
        return newState;
    }



    public void print()
    {
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                char p = (grid.getInt(y, x, ObjectType.PLAYER1.type) == 1) ? '1' : ' ';
                p = (grid.getInt(y, x, ObjectType.PLAYER2.type) == 1) ? '2' : p;
                p = (grid.getInt(y, x, ObjectType.WALL.type) == 1) ? 'W' : p;
                p = (grid.getInt(y, x, ObjectType.CRATE.type) == 1) ? 'C' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB1_3.type) == 1) ? 'B' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB1_2.type) == 1) ? 'B' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB1_1.type) == 1) ? 'B' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB1_0.type) == 1) ? 'X' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB2_3.type) == 1) ? 'B' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB2_2.type) == 1) ? 'B' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB2_1.type) == 1) ? 'B' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB2_0.type) == 1) ? 'X' : p;
                if (x == 0) {
                    System.out.print("|");
                }
                System.out.print(p + "|");
            }
            System.out.println();
        }
    }



    public INDArray getInputRepresentation()
    {
        return grid.reshape(1, FIELD_HEIGHT * FIELD_WIDTH * OBJECT_TYPES);
    }
}