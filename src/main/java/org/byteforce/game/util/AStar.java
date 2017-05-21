package org.byteforce.game.util;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;


public class AStar
{
    public static final int V_H_COST = 10;

    static class Cell
    {
        int heuristicCost = 0; //Heuristic cost

        int finalCost = 0; //G+H

        int i, j;

        Cell parent;

        Cell(int i, int j)
        {
            this.i = i;
            this.j = j;
        }
    }

    //Blocked cells are just null Cell values in grid
    private Cell[][] grid;

    private PriorityQueue<Cell> open;

    private boolean closed[][];

    private int width, height;

    private int startI, startJ, endI, endJ;

    public AStar(int pHeight, int pWidth, int pStartI, int pStartJ, int pEndI, int pEndJ){
        width = pWidth;
        height = pHeight;
        grid = new Cell[height][width];
        startI = pStartI;
        startJ = pStartJ;
        endI = pEndI;
        endJ = pEndJ;

        for(int i=0;i<grid.length;++i){
            for(int j=0;j<grid[0].length;++j){
                grid[i][j] = new Cell(i, j);
                grid[i][j].heuristicCost = Math.abs(i-endI)+Math.abs(j-endJ);
            }
        }
        grid[startI][startJ].finalCost = 0;

    }

    public void setBlocked(int i, int j)
    {
        grid[i][j] = null;
    }


    private void checkAndUpdateCost(Cell current, Cell t, int cost)
    {
        if (t == null || closed[t.i][t.j]) {
            return;
        }
        int t_final_cost = t.heuristicCost + cost;

        boolean inOpen = open.contains(t);
        if (!inOpen || t_final_cost < t.finalCost) {
            t.finalCost = t_final_cost;
            t.parent = current;
            if (!inOpen) {
                open.add(t);
            }
        }
    }

    public Optional<List<Pair<Integer,Integer>>> getShortestPath()
    {
        closed = new boolean[height][width]; //Defaults to false
        open = new PriorityQueue<>((c1,c2) -> c1.finalCost<c2.finalCost?-1:
            c1.finalCost>c2.finalCost?1:0);


        //add the start location to open list.
        open.add(grid[startI][startJ]);

        Cell current;

        while (true) {
            current = open.poll();
            if (current == null) {
                break;
            }
            closed[current.i][current.j] = true;

            if (current.equals(grid[endI][endJ])) {
                break;
            }

            Cell t;
            if (current.i - 1 >= 0) {
                t = grid[current.i - 1][current.j];
                checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
            }

            if (current.j - 1 >= 0) {
                t = grid[current.i][current.j - 1];
                checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
            }

            if (current.j + 1 < grid[0].length) {
                t = grid[current.i][current.j + 1];
                checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
            }

            if (current.i + 1 < grid.length) {
                t = grid[current.i + 1][current.j];
                checkAndUpdateCost(current, t, current.finalCost + V_H_COST);

            }
        }

        //get the path
        if(closed[endI][endJ]){
            List<Pair<Integer,Integer>> result = new ArrayList<>();
            //Trace back the path
            current = grid[endI][endJ];
            result.add(Pair.of(current.i, current.j));
            while(current.parent!=null){
                current = current.parent;
                result.add(Pair.of(current.i, current.j));
            }
            Collections.reverse(result);
            return Optional.of(result);
        }else {
            return Optional.empty();
        }
    }
}