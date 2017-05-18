package org.byteforce.game.AiContest;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.byteforce.ai.Action;
import org.byteforce.ai.AdversarialGameServer;
import org.byteforce.ai.State;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestState
    implements State
{

    static final int FIELD_WIDTH = 7;

    static final int FIELD_HEIGHT = 7;

    static final int MAX_TICKS = 3;

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



        static int getLayerForPlayer(int playerNum)
        {
            if (playerNum == 0) {
                return PLAYER1.type;
            }
            else if (playerNum == 1) {
                return PLAYER2.type;
            }
            else {
                throw new RuntimeException("Player type " + playerNum + " not implemented");
            }
        }



        static int getLayerForBomb(int playerNum, int ticksElapsed)
        {
            if (ticksElapsed > 3) {
                throw new RuntimeException("getLayerForBomb: ticksElapsed too large");
            }
            if (playerNum == 0) {
                return BOMB1_3.type + ticksElapsed;
            }
            else if (playerNum == 1) {
                return BOMB2_3.type + ticksElapsed;
            }
            else {
                throw new RuntimeException("Player type " + playerNum + " not implemented");
            }
        }



        static ObjectType get(int i)
        {
            return lookup.get(i);
        }
    }



    //For convenience and performance, some information is stored redundantly
    //In a grid for the AI and in special classes for evaluating the state
    INDArray grid;

    Player[] player;

    Bomb[] bomb;





    public AiContestState()
    {
        player = new Player[2];
        bomb = new Bomb[2];


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
        player[0] = new Player(0, 0);
        player[1] = new Player(6, 6);
    }



    AiContestState(INDArray pGrid, Player pPlayer1, Player pPlayer2, Bomb pBomb1, Bomb pBomb2)
    {
        player = new Player[2];
        bomb = new Bomb[2];
        grid = pGrid;
        player[0] = pPlayer1;
        player[1] = pPlayer2;
        bomb[0] = pBomb1;
        bomb[1] = pBomb2;
    }



    public State copy()
    {
        return new AiContestState(grid.dup(), player[0].copy(), player[1].copy(), bomb[0].copy(), bomb[1].copy());
    }



    public double getReward()
    {
        //-10 for loosing when you are too close to an exploding bomb
        if (grid.getInt(player[0].y, player[0].x, ObjectType.BOMB1_0.type) == 1 || grid.getInt(player[0].y, player[0].x,
            ObjectType.BOMB2_0.type) == 1) {
            return -10;
        }
        //10 for winning when the opponent is too close to an exploding bomb
        else if ((grid.getInt(player[1].y, player[1].x, ObjectType.BOMB1_0.type) == 1 || grid.getInt(player[1].y,
            player[1].x, ObjectType.BOMB2_0.type) == 1)) {
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



    private void handlePos(int y, int x, AiContestState newState, int playerNum)
    {
        if (y >= 0 && y < FIELD_HEIGHT && x >= 0 && x < FIELD_WIDTH && newState.grid.getInt(y, x, ObjectType.WALL.type)
            != 1) {
            // No wall in between
            if (newState.grid.getInt(newState.bomb[playerNum].y + (y - newState.bomb[playerNum].y) / 2,
                newState.bomb[playerNum].x + (x - newState.bomb[playerNum].x) / 2, ObjectType.WALL.type) != 1) {
                newState.grid.putScalar(new int[]{y, x, ObjectType.getLayerForBomb(playerNum, MAX_TICKS)}, 1);
                newState.grid.putScalar(new int[]{y, x, ObjectType.CRATE.type}, 0);
            }
        }
    }



    private void cleanPos(int y, int x, AiContestState newState, int playerNum)
    {
        if (y >= 0 && y < FIELD_HEIGHT && x >= 0 && x < FIELD_WIDTH) {
            newState.grid.putScalar(new int[]{y, x, ObjectType.getLayerForBomb(playerNum, MAX_TICKS)}, 0);
        }
    }



    private void handleBombForPlayer(AiContestState newState, int playerNum)
    {
        if (newState.bomb[playerNum] != null) {
            if (newState.bomb[playerNum].isExploded()) {
                //Erase explosion
                newState.grid.putScalar(
                    new int[]{newState.bomb[playerNum].y, newState.bomb[playerNum].x, ObjectType.getLayerForBomb(playerNum, MAX_TICKS)},
                    0);
                for (int i = 1; i < 3; i++) {
                    cleanPos(newState.bomb[playerNum].y - i, newState.bomb[playerNum].x, newState, playerNum);
                    cleanPos(newState.bomb[playerNum].y + i, newState.bomb[playerNum].x, newState, playerNum);
                    cleanPos(newState.bomb[playerNum].y, newState.bomb[playerNum].x - i, newState, playerNum);
                    cleanPos(newState.bomb[playerNum].y, newState.bomb[playerNum].x + i, newState, playerNum);
                }
                newState.bomb[playerNum] = null;
            }
            else {
                newState.bomb[playerNum].tick();
                newState.grid.putScalar(
                    new int[]{newState.bomb[playerNum].y, newState.bomb[playerNum].x, ObjectType.getLayerForBomb(playerNum, 2)},
                    newState.grid
                        .getInt(newState.bomb[playerNum].y, newState.bomb[playerNum].x, ObjectType.getLayerForBomb(playerNum, 1)));
                newState.grid.putScalar(
                    new int[]{newState.bomb[playerNum].y, newState.bomb[playerNum].x, ObjectType.getLayerForBomb(playerNum, 1)},
                    newState.grid
                        .getInt(newState.bomb[playerNum].y, newState.bomb[playerNum].x, ObjectType.getLayerForBomb(playerNum, 0)));
                newState.grid.putScalar(
                    new int[]{newState.bomb[playerNum].y, newState.bomb[playerNum].x, ObjectType.getLayerForBomb(playerNum, 0)}, 0);
                if (newState.bomb[playerNum].isExploded()) {
                    //calculate explosion
                    newState.grid.putScalar(new int[]{newState.bomb[playerNum].y, newState.bomb[playerNum].x, ObjectType.getLayerForBomb(playerNum, MAX_TICKS)},
                        1);
                    for (int i = 1; i < 3; i++) {
                        handlePos(newState.bomb[playerNum].y - i, newState.bomb[playerNum].x, newState, playerNum);
                        handlePos(newState.bomb[playerNum].y + i, newState.bomb[playerNum].x, newState, playerNum);
                        handlePos(newState.bomb[playerNum].y, newState.bomb[playerNum].x - i, newState, playerNum);
                        handlePos(newState.bomb[playerNum].y, newState.bomb[playerNum].x + i, newState, playerNum);
                    }
                }
            }
        }
    }



    private void handleActionForPlayer(AiContestState newState, Action action, int playerNum)
    {
        if (action.getType() == AiContestAction.UP.getType() && player[playerNum].y > 0 && grid.getInt(
            player[playerNum].y - 1, player[playerNum].x, ObjectType.WALL.type) < 1 && grid.getInt(
            player[playerNum].y - 1, player[playerNum].x, ObjectType.CRATE.type) < 1) {
            newState.grid.putScalar(new int[]{player[playerNum].y, player[playerNum].x, ObjectType.getLayerForPlayer(playerNum)}, 0);
            newState.grid.putScalar(new int[]{player[playerNum].y - 1, player[playerNum].x, ObjectType.getLayerForPlayer(playerNum)},
                1);
            newState.player[playerNum].y--;
        }
        else if (action.getType() == AiContestAction.DOWN.getType() && player[playerNum].y < FIELD_HEIGHT - 1
            && grid.getInt(player[playerNum].y + 1, player[playerNum].x, ObjectType.WALL.type) < 1 && grid.getInt(
            player[playerNum].y + 1, player[playerNum].x, ObjectType.CRATE.type) < 1) {
            newState.grid.putScalar(new int[]{player[playerNum].y, player[playerNum].x, ObjectType.getLayerForPlayer(playerNum)}, 0);
            newState.grid.putScalar(new int[]{player[playerNum].y + 1, player[playerNum].x, ObjectType.getLayerForPlayer(playerNum)},
                1);
            newState.player[playerNum].y++;
        }
        else if (action.getType() == AiContestAction.LEFT.getType() && player[playerNum].x > 0 && grid.getInt(
            player[playerNum].y, player[playerNum].x - 1, ObjectType.WALL.type) < 1 && grid.getInt(player[playerNum].y,
            player[playerNum].x - 1, ObjectType.CRATE.type) < 1) {
            newState.grid.putScalar(new int[]{player[playerNum].y, player[playerNum].x, ObjectType.getLayerForPlayer(playerNum)}, 0);
            newState.grid.putScalar(new int[]{player[playerNum].y, player[playerNum].x - 1, ObjectType.getLayerForPlayer(playerNum)},
                1);
            newState.player[playerNum].x--;
        }
        else if (action.getType() == AiContestAction.RIGHT.getType() && player[playerNum].x < FIELD_WIDTH - 1
            && grid.getInt(player[playerNum].y, player[playerNum].x + 1, ObjectType.WALL.type) < 1 && grid.getInt(
            player[playerNum].y, player[playerNum].x + 1, ObjectType.CRATE.type) < 1) {
            newState.grid.putScalar(new int[]{player[playerNum].y, player[playerNum].x, ObjectType.getLayerForPlayer(playerNum)}, 0);
            newState.grid.putScalar(new int[]{player[playerNum].y, player[playerNum].x + 1, ObjectType.getLayerForPlayer(playerNum)},
                1);
            newState.player[playerNum].x++;
        }
        else if (action.getType() == AiContestAction.DROP.getType() && newState.bomb[playerNum] == null) {
            newState.bomb[playerNum] = new Bomb(player[playerNum].y, player[playerNum].x);
            newState.grid.putScalar(
                new int[]{newState.bomb[playerNum].y, newState.bomb[playerNum].x, ObjectType.getLayerForBomb(playerNum, 0)}, 1);
        }
    }



    public State move(Action action)
    {
        throw new NotImplementedException();
    }

    public State move(Action actionPlayer0, Action actionPlayer1)
    {
        AiContestState newState = new AiContestState(grid.dup(), player[0].copy(), player[1].copy(),
            (bomb[0] != null) ? bomb[0].copy() : null, (bomb[1] != null) ? bomb[1].copy() : null);

        handleBombForPlayer(newState, 0);
        handleBombForPlayer(newState, 1);
        handleActionForPlayer(newState, actionPlayer0, 0);
        handleActionForPlayer(newState, actionPlayer1, 1);

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