package org.byteforce.game.AiContest;

import java.util.Optional;
import java.util.Random;

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



        ObjectType(int pType)
        {
            type = pType;
        }



        static ObjectType get(int i)
        {
            if (i == 0) {
                return PLAYER1;
            }
            else if (i == 1) {
                return PLAYER2;
            }
            else if (i == 2) {
                return WALL;
            }
            else if (i == 3) {
                return CRATE;
            }
            else if (i == 4) {
                return BOMB1_3; // Bomb explodes on layer 7
            }
            else if (i == 5) {
                return BOMB1_2; // Bomb explodes on layer 7
            }
            else if (i == 6) {
                return BOMB1_1; // Bomb explodes on layer 7
            }
            else if (i == 7) {
                return BOMB1_0; // Bomb explodes on layer 7
            }
            else if (i == 8) {
                return BOMB2_3; // Bomb explodes on layer 11
            }
            else if (i == 9) {
                return BOMB2_2; // Bomb explodes on layer 11
            }
            else if (i == 10) {
                return BOMB2_1; // Bomb explodes on layer 11
            }
            else{
                return BOMB2_0; // Bomb explodes on layer 11
            }
        }
    }



    INDArray grid;

    Player player;

    Bomb bomb1;
    Bomb bomb2;

    public AiContestState()
    {

        grid = Nd4j.zeros(FIELD_HEIGHT, FIELD_WIDTH, OBJECT_TYPES);  //X x Y x (ObjectTypes + 3 extra layers per bomb for ticking)
        Random r = new Random();

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
    }



    AiContestState(INDArray pGrid, Player pPlayer, Bomb pBomb1, Bomb pBomb2)
    {
        grid = pGrid;
        player = pPlayer;
        bomb1 = pBomb1;
        bomb2 = pBomb2;
    }



    public State copy()
    {
        return new AiContestState(grid.dup(), player.copy(), bomb1.copy(), bomb2.copy());
    }


    public double getReward()
    {
        // TODO
        // //-10 for loosing when you are too close to an exploding bomb
        // if (grid.getInt(player.y, player.x, ObjectType.PIT.type) == 1) {
        //     return -10;
        // }
        // //10 for winning when the opponent is too close to an exploding bomb
        // else if (grid.getInt(player.y, player.x, ObjectType.GOAL.type) == 1) {
        //     return 10;
        // }
        // else {
        //     return -1;
        // }
        return -1;
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



    public State move(Action action)
    {
        AiContestState newState = new AiContestState(grid.dup(), player.copy(), bomb1, bomb2);
        // TODO
        // if (action.getType() == AiContestAction.UP.getType() && player.y > 0 && grid.getInt(player.y - 1, player.x,
        //     ObjectType.WALL.type) < 1) {
        //     newState.grid.putScalar(new int[]{player.y, player.x, ObjectType.PLAYER.type}, 0);
        //     newState.grid.putScalar(new int[]{player.y - 1, player.x, ObjectType.PLAYER.type}, 1);
        //     newState.player.y--;
        // }
        // else if (action.getType() == AiContestAction.DOWN.getType() && player.y < 3 && grid.getInt(player.y + 1,
        //     player.x, ObjectType.WALL.type) < 1) {
        //     newState.grid.putScalar(new int[]{player.y, player.x, ObjectType.PLAYER.type}, 0);
        //     newState.grid.putScalar(new int[]{player.y + 1, player.x, ObjectType.PLAYER.type}, 1);
        //     newState.player.y++;
        // }
        // else if (action.getType() == AiContestAction.LEFT.getType() && player.x > 0 && grid.getInt(player.y,
        //     player.x - 1, ObjectType.WALL.type) < 1) {
        //     newState.grid.putScalar(new int[]{player.y, player.x, ObjectType.PLAYER.type}, 0);
        //     newState.grid.putScalar(new int[]{player.y, player.x - 1, ObjectType.PLAYER.type}, 1);
        //     newState.player.x--;
        // }
        // else if (action.getType() == AiContestAction.RIGHT.getType() && player.x < 3 && grid.getInt(player.y,
        //     player.x + 1, ObjectType.WALL.type) < 1) {
        //     newState.grid.putScalar(new int[]{player.y, player.x, ObjectType.PLAYER.type}, 0);
        //     newState.grid.putScalar(new int[]{player.y, player.x + 1, ObjectType.PLAYER.type}, 1);
        //     newState.player.x++;
        // }

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
                p = (grid.getInt(y, x, ObjectType.BOMB1_0.type) == 1) ? 'B' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB2_3.type) == 1) ? 'B' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB2_2.type) == 1) ? 'B' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB2_1.type) == 1) ? 'B' : p;
                p = (grid.getInt(y, x, ObjectType.BOMB2_0.type) == 1) ? 'B' : p;

                System.out.print("|" + p + "|");
            }
            System.out.println();
        }
    }



    public INDArray getInputRepresentation()
    {
        return grid.reshape(1, FIELD_HEIGHT * FIELD_WIDTH * OBJECT_TYPES);
    }
}