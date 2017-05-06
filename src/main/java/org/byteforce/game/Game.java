package org.byteforce.game;

import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;


/**
 * Bomberman clone
 * <p/>
 *
 *
 * @author Philipp Baumgaertel
 */
public class Game
{
    class Field {
        Map<Pair<Integer,Integer>,Element> elements;
        int width;
        int height;

        public Field(int pWidth, int pHeight) {
            width = pWidth;
            height = pHeight;
            createObstacles();
        }

        public void createObstacles(){
            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    if(i%2 == 1 && j%2 == 1){
                        elements.put(new ImmutablePair<>(i,j),new Wall(i,j));
                    }
                }
            }
        }

    }

    abstract class Element {
        int xPosition;
        int yPosition;
        public Element(int pXPosition, int pYPosition){
            xPosition = pXPosition;
            yPosition = pYPosition;
        }
    }

    class Player extends Element {
        String name;
        public Player(int pXPosition, int pYPosition){
            super(pXPosition,pYPosition);
        }
    }

    class Bomb extends Element {

        public Bomb(final int pXPosition, final int pYPosition) {
            super(pXPosition, pYPosition);
        }
    }

    class Wall extends Element {

        public Wall(final int pXPosition, final int pYPosition) {
            super(pXPosition, pYPosition);
        }
    }
    Field field;
}
