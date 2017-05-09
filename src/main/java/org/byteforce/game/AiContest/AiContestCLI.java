package org.byteforce.game.AiContest;

import java.util.Scanner;

import org.byteforce.ai.Action;
import org.byteforce.ai.State;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestCLI
{

    public static void main(String[] args) {
        State s = new AiContestState();
        while(!s.isFinal()){
            s.print();
            System.out.print("Enter action (up,down,left,right,drop)");
            Scanner scanner = new Scanner(System.in);
            String action = scanner.nextLine();
            Action a;
            if(action.equalsIgnoreCase("w")){
                a = AiContestAction.UP;
            } else if (action.equalsIgnoreCase("s")){
                a = AiContestAction.DOWN;
            } else if (action.equalsIgnoreCase("a")){
                a = AiContestAction.LEFT;
            } else if (action.equalsIgnoreCase("d")){
                a = AiContestAction.RIGHT;
            }  else if (action.equalsIgnoreCase("b")){
                a = AiContestAction.DROP;
            } else {
                continue;
            }
            s = s.move(a);
        }
        s.print();
        if(s.won()){
            System.out.println("You win!");
        }else {
            System.out.println("You loose!");
        }

    }
}
