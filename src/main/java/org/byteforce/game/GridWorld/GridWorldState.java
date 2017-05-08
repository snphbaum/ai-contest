package org.byteforce.game.GridWorld;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.byteforce.ai.Action;
import org.byteforce.ai.State;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * @author Philipp Baumgaertel
 */
public class GridWorldState implements State
{

    static class Player {
        int x;
        int y;
        Player(int pY, int pX){
            x = pX;
            y = pY;
        }
        Player copy() {
            return new Player(y,x);
        }
    }
    enum ObjectType {
        PLAYER(0),
        WALL(1),
        PIT(2),
        GOAL(3);
        int type;

        ObjectType(int pType){
            type = pType;
        }
        static ObjectType get(int i){
            if(i==0)
                return PLAYER;
            else if (i == 1)
                return WALL;
            else if (i == 2)
                return PIT;
            else
                return GOAL;
        }
    }

    INDArray grid;
    Player player;

    public GridWorldState(){
        this(false);
    }

    public GridWorldState(boolean createRandom){

        if(createRandom) {
            grid = Nd4j.zeros(4,4,4);
            Random r = new Random();
            boolean finished = false;
            while(!finished) {

                Set<ImmutablePair<Integer,Integer>> objSet = new HashSet<ImmutablePair<Integer,Integer>>();
                for(int i = 0;i < 4; i++) {
                    int x = r.nextInt(4);
                    int y = r.nextInt(4);
                    objSet.add(new ImmutablePair<>(y,x));
                }
                if(objSet.size() == 4) {
                    finished = true;
                    int i = 0;
                    for(ImmutablePair<Integer,Integer> pos : objSet){
                        if(i == 0){
                            player = new Player(pos.getLeft(), pos.getRight());
                        }
                        grid.putScalar(new int[]{pos.getLeft(), pos.getRight(), ObjectType.get(i).type},1);
                        i++;
                    }
                }
            }

        } else {
            grid = Nd4j.zeros(4,4,4);
            player = new Player(0, 1);
            grid.putScalar(new int[]{0, 1, ObjectType.PLAYER.type},1); //Player
            grid.putScalar(new int[]{2, 2, ObjectType.WALL.type},1); //Wall
            grid.putScalar(new int[]{1, 1, ObjectType.PIT.type},1); //Pit
            grid.putScalar(new int[]{3, 3, ObjectType.GOAL.type},1); //Goal
        }

    }

    GridWorldState(INDArray pGrid, Player pPlayer){
        grid = pGrid;
        player = pPlayer;
    }

    public State copy(){
        return new GridWorldState(grid.dup(),player.copy());
    }
    public double getReward(){
        if(grid.getInt(player.y,player.x, ObjectType.PIT.type) == 1){
            return -10;
        }else if (grid.getInt(player.y,player.x, ObjectType.GOAL.type) == 1){
            return 10;
        } else {
            return -1;
        }
    }
    public boolean isFinal(){
        if(grid.getInt(player.y,player.x, ObjectType.PIT.type) == 1){
            return true;
        }else if (grid.getInt(player.y,player.x, ObjectType.GOAL.type) == 1){
            return true;
        } else {
            return false;
        }
    }
    public boolean won(){
        if (grid.getInt(player.y,player.x, ObjectType.GOAL.type) == 1){
            return true;
        } else {
            return false;
        }
    }
    public State move(Action action){
        GridWorldState newGridworldState = new GridWorldState(grid.dup(),player.copy());

        if(action.getType() == GridWorldAction.UP.getType() &&  player.y > 0 && grid.getInt(player.y-1,player.x, ObjectType.WALL.type) < 1){
            newGridworldState.grid.putScalar(new int[]{player.y,player.x, ObjectType.PLAYER.type},0);
            newGridworldState.grid.putScalar(new int[]{player.y-1,player.x, ObjectType.PLAYER.type},1);
            newGridworldState.player.y--;
        } else if(action.getType() == GridWorldAction.DOWN.getType() &&  player.y < 3 && grid.getInt(player.y+1,player.x, ObjectType.WALL.type) < 1){
            newGridworldState.grid.putScalar(new int[]{player.y,player.x, ObjectType.PLAYER.type},0);
            newGridworldState.grid.putScalar(new int[]{player.y+1,player.x, ObjectType.PLAYER.type},1);
            newGridworldState.player.y++;
        } else if(action.getType() == GridWorldAction.LEFT.getType() &&  player.x > 0 && grid.getInt(player.y,player.x-1, ObjectType.WALL.type) < 1){
            newGridworldState.grid.putScalar(new int[]{player.y,player.x, ObjectType.PLAYER.type},0);
            newGridworldState.grid.putScalar(new int[]{player.y,player.x-1, ObjectType.PLAYER.type},1);
            newGridworldState.player.x--;
        } else if(action.getType() == GridWorldAction.RIGHT.getType() &&  player.x < 3 && grid.getInt(player.y,player.x+1, ObjectType.WALL.type) < 1){
            newGridworldState.grid.putScalar(new int[]{player.y,player.x, ObjectType.PLAYER.type},0);
            newGridworldState.grid.putScalar(new int[]{player.y,player.x+1, ObjectType.PLAYER.type},1);
            newGridworldState.player.x++;
        }

        return newGridworldState;
    }

    public void print() {
        for(int y = 0; y < 4; y++){
            for(int x = 0; x < 4; x++){
                char p = (grid.getInt(y,x,ObjectType.PLAYER.type) == 1 )? 'P' : ' ';
                p = (grid.getInt(y,x,ObjectType.WALL.type) == 1 )? 'W' : p;
                p = (grid.getInt(y,x,ObjectType.PIT.type) == 1 )? '-' : p;
                p = (grid.getInt(y,x,ObjectType.GOAL.type) == 1 )? '+' : p;
                System.out.print(" " + p + " ");
            }
            System.out.println();
        }
    }
    public INDArray getInputRepresentation(){
        return grid.reshape(1,64);
    }

}