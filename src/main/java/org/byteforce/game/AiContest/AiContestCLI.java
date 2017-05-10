package org.byteforce.game.AiContest;

import java.util.Scanner;

import org.byteforce.ai.Action;
import org.byteforce.ai.State;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestCLI
{

    static class Opponent
        implements Runnable
    {

        LocalGameServerImpl gameServer;



        public Opponent(LocalGameServerImpl pGameServer)
        {
            gameServer = pGameServer;
        }



        @Override
        public void run()
        {
            while(true) {
                Scanner scanner = new Scanner(System.in);
                String action = scanner.nextLine();
                Action a;
                //Loop to handle wrong input
                while (true) {
                    if (action.equalsIgnoreCase("w")) {
                        a = AiContestAction.UP;
                        break;
                    }
                    else if (action.equalsIgnoreCase("s")) {
                        a = AiContestAction.DOWN;
                        break;
                    }
                    else if (action.equalsIgnoreCase("a")) {
                        a = AiContestAction.LEFT;
                        break;
                    }
                    else if (action.equalsIgnoreCase("d")) {
                        a = AiContestAction.RIGHT;
                        break;
                    }
                    else if (action.equalsIgnoreCase("b")) {
                        a = AiContestAction.DROP;
                        break;
                    }
                    else {
                        continue;
                    }
                }
                gameServer.exchangeAction(a);
            }
        }
    }



    public static void main(String[] args)
    {
        LocalGameServerImpl g = new LocalGameServerImpl();
        Thread opponent = null;

        State s = new AiContestState(g);
        while (!s.isFinal()) {
            s.print();
            System.out.print("Enter action (up,down,left,right,drop)");
            Scanner scanner = new Scanner(System.in);
            String action = scanner.nextLine();
            Action a;
            if (action.equalsIgnoreCase("w")) {
                a = AiContestAction.UP;
            }
            else if (action.equalsIgnoreCase("s")) {
                a = AiContestAction.DOWN;
            }
            else if (action.equalsIgnoreCase("a")) {
                a = AiContestAction.LEFT;
            }
            else if (action.equalsIgnoreCase("d")) {
                a = AiContestAction.RIGHT;
            }
            else if (action.equalsIgnoreCase("b")) {
                a = AiContestAction.DROP;
            }
            else {
                continue;
            }
            if (opponent == null) {
                opponent = new Thread(new Opponent(g));
                opponent.start();
            }
            s = s.move(a);
        }
        s.print();
        if (s.won()) {
            System.out.println("You win!");
        }
        else {
            System.out.println("You loose!");
        }
    }
}
