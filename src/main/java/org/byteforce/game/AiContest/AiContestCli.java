package org.byteforce.game.AiContest;

import java.util.Scanner;

import org.byteforce.ai.Action;
import org.byteforce.ai.AdversarialGameServer;
import org.byteforce.ai.AiPlayer;
import org.byteforce.ai.Player;
import org.byteforce.ai.State;


/**
 * @author Philipp Baumgaertel
 */
public class AiContestCli
{

    static class AiCliPlayer
        implements Player
    {

        @Override
        public Action getAction(final State pState)
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter action player 2 (wasdb)");
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
            return a;
        }
    }



    public static void main(String[] args)
    {
        //AdversarialGameServer g = new AdversarialGameServer(new AiCliPlayer(), false);
        AdversarialGameServer g = new AdversarialGameServer(new AiContestSimplePlayer(1), false);
        //AdversarialGameServer g = new AdversarialGameServer(new AiPlayer(new AiContestActionFactory(),"MyMultiLayerNetwork.zip"), true);
        State s = new AiContestState();
        while (!s.isFinal()) {
            s.print();
            System.out.println("Enter action (wasdb)");
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


            s = g.doAction(a, s);
        }
        s.print();
        if (s.won()) {
            System.out.println("You loose!"); //It's the other way round, because you are player 2 here
        }
        else {
            System.out.println("You win!");
        }
    }
}
