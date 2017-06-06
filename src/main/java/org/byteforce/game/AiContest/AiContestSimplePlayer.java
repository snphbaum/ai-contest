package org.byteforce.game.AiContest;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.byteforce.ai.Action;
import org.byteforce.ai.Player;
import org.byteforce.ai.State;
import org.byteforce.game.util.AStar;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestSimplePlayer
    implements Player
{
    int playerNum;

    AiContestActionFactory actionFactory;

    Random rand;

    int adversaryPlayerNum;

    int lastAdversaryX, lastAdversaryY;



    AiContestSimplePlayer(int pPlayerNum)
    {
        playerNum = pPlayerNum;
        actionFactory = new AiContestActionFactory();
        rand = new Random();
        adversaryPlayerNum = (playerNum + 1) % 2;
        lastAdversaryX = -1;
        lastAdversaryY = -1;
    }



    private void setBombsAsBlocked(AiContestState state, AStar pAStar)
    {
        for (int iPlayerNum = 0; iPlayerNum < 2; iPlayerNum++) {
            if (state.bomb[iPlayerNum] != null) {
                for (int i = 1; i < 3; i++) {
                    checkBombLineOfSightAndSetBlocked(state.bomb[iPlayerNum].y - i, state.bomb[iPlayerNum].x, state,
                        iPlayerNum, pAStar);
                    checkBombLineOfSightAndSetBlocked(state.bomb[iPlayerNum].y + i, state.bomb[iPlayerNum].x, state,
                        iPlayerNum, pAStar);
                    checkBombLineOfSightAndSetBlocked(state.bomb[iPlayerNum].y, state.bomb[iPlayerNum].x - i, state,
                        iPlayerNum, pAStar);
                    checkBombLineOfSightAndSetBlocked(state.bomb[iPlayerNum].y, state.bomb[iPlayerNum].x + i, state,
                        iPlayerNum, pAStar);
                }
            }
        }
    }



    private void checkBombLineOfSightAndSetBlocked(int y, int x, AiContestState newState, int playerNum, AStar pAStar)
    {
        if (y >= 0 && y < AiContestState.FIELD_HEIGHT && x >= 0 && x < AiContestState.FIELD_WIDTH
            && newState.grid.getInt(y, x, AiContestState.ObjectType.WALL.type) != 1) {
            // No wall in between
            if (newState.grid.getInt(newState.bomb[playerNum].y + (y - newState.bomb[playerNum].y) / 2,
                newState.bomb[playerNum].x + (x - newState.bomb[playerNum].x) / 2, AiContestState.ObjectType.WALL.type)
                != 1) {
                pAStar.setBlocked(y, x);
            }
        }
    }



    private Action runToPos(int pY, int pX, AiContestState state, boolean considerBombs)
    {
        AStar aStar = new AStar(AiContestState.FIELD_HEIGHT, AiContestState.FIELD_WIDTH, state.player[playerNum].y,
            state.player[playerNum].x, pY, pX);

        for (int y = 0; y < AiContestState.FIELD_HEIGHT; y++) {
            for (int x = 0; x < AiContestState.FIELD_WIDTH; x++) {
                if (x % 2 == 1 && y % 2 == 1) {
                    if (state.grid.getInt(y, x, AiContestState.ObjectType.WALL.type) == 1) {
                        aStar.setBlocked(y, x);
                    }
                    else if (state.grid.getInt(y, x, AiContestState.ObjectType.CRATE.type) == 1) {
                        aStar.setBlocked(y, x);
                    }
                }
            }
        }
        if (considerBombs) {
            setBombsAsBlocked(state, aStar);
        }

        Optional<List<Pair<Integer, Integer>>> path = aStar.getShortestPath();

        if (path.isPresent() && path.get().size() > 1) {
            if (path.get().get(1).getLeft() < state.player[playerNum].y) {
                return AiContestAction.UP;
            }
            else if (path.get().get(1).getLeft() > state.player[playerNum].y) {
                return AiContestAction.DOWN;
            }
            else if (path.get().get(1).getRight() < state.player[playerNum].x) {
                return AiContestAction.LEFT;
            }
            else if (path.get().get(1).getRight() > state.player[playerNum].x) {
                return AiContestAction.RIGHT;
            }
            else {
                throw new IllegalStateException("Next step in path is not valid");
            }
        }
        else {
            return actionFactory.get(
                rand.nextInt(actionFactory.getNumberOfActions())); //TODO allow void action in general
        }
    }



    private boolean isBombNear(AiContestState state, int y, int x)
    {
        for (int i = 0; i < 2; i++) {
            if (state.bomb[i] != null) {

                if (Math.abs(y - state.bomb[i].y) < 3 && Math.abs(x - state.bomb[i].x) < 3) {
                    // No wall in between
                    if (state.grid.getInt(state.bomb[i].y + (y - state.bomb[i].y) / 2,
                        state.bomb[i].x + (x - state.bomb[i].x) / 2, AiContestState.ObjectType.WALL.type) != 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    private Action runFromBomb(AiContestState state)
    {
        int closestX = 0;
        int closestY = 0;
        int distance = 1000;
        // Get closest bomb free spot
        for (int iy = -2; iy < 3; iy++) {
            for (int ix = -2; ix < 3; ix++) {
                if (state.player[playerNum].y + iy >= 0 && state.player[playerNum].y + iy < AiContestState.FIELD_HEIGHT
                    && state.player[playerNum].x + ix >= 0
                    && state.player[playerNum].x + ix < AiContestState.FIELD_WIDTH) {
                    if (!isBombNear(state, state.player[playerNum].y + iy, state.player[playerNum].x + ix)) {
                        if (Math.abs(ix) + Math.abs(iy) < distance) {
                            closestX = state.player[playerNum].x + ix;
                            closestY = state.player[playerNum].y + iy;
                        }
                    }
                }
            }
        }
        return runToPos(closestY, closestY, state, false);
    }



    private Pair<Integer, Integer> getTarget(AiContestState state)
    {
        //don't go, where the other player is, go where you expect him to be

        int targetX;
        int targetY;
        if (lastAdversaryX > 0) {
            int dirX = state.player[adversaryPlayerNum].x - lastAdversaryX;
            int dirY = state.player[adversaryPlayerNum].y - lastAdversaryY;
            targetX = Math.min(Math.max(state.player[adversaryPlayerNum].x + 3 * dirX, 0),
                AiContestState.FIELD_WIDTH - 1);
            targetY = Math.min(Math.max(state.player[adversaryPlayerNum].y + 3 * dirY, 0),
                AiContestState.FIELD_HEIGHT - 1);
            if (state.grid.getInt(targetY, targetX, AiContestState.ObjectType.WALL.type) == 1 || state.grid.getInt(
                targetY, targetX, AiContestState.ObjectType.CRATE.type) == 1) {
                //Target position is unreachable ==> go to player directly
                targetX = state.player[adversaryPlayerNum].x;
                targetY = state.player[adversaryPlayerNum].y;
            }
        }
        else {
            targetX = state.player[adversaryPlayerNum].x;
            targetY = state.player[adversaryPlayerNum].y;
        }
        return Pair.of(targetY, targetX);
    }



    private boolean isGoodPlaceToDropBomb(AiContestState state)
    {
        //Good is near the other player and on crossings
        return (Math.abs(state.player[adversaryPlayerNum].y - state.player[playerNum].y) + Math.abs(
            state.player[adversaryPlayerNum].x - state.player[playerNum].x) < 2) || (Math.abs(
            state.player[adversaryPlayerNum].y - state.player[playerNum].y) + Math.abs(
            state.player[adversaryPlayerNum].x - state.player[playerNum].x) < 3 && state.player[playerNum].y % 2 == 0
            && state.player[playerNum].x % 2 == 0);
    }



    @Override
    public Action getAction(final State pState)
    {
        AiContestState state = (AiContestState) pState;

        Pair<Integer, Integer> target = getTarget(state);

        lastAdversaryX = state.player[adversaryPlayerNum].x;
        lastAdversaryY = state.player[adversaryPlayerNum].y;

        if (isBombNear(state, state.player[playerNum].y, state.player[playerNum].x)) {
            return runFromBomb(state);
        }
        else if (isGoodPlaceToDropBomb(state)) {
            return AiContestAction.DROP;
        }
        else {
            return runToPos(target.getLeft(), target.getRight(), state, true);
        }
    }



    @Override
    public boolean isPlayerZero()
    {
        return playerNum == 0;
    }
}
