package org.byteforce.server.DTOs;

/**
 *
 * @author Philipp Baumgaertel
 */
public class Bomb
    extends GameObject
{
    private int remainingRounds; // How many moves does the player have left



    public int getRemainingRounds()
    {
        return remainingRounds;
    }



    public void setRemainingRounds(final int pRemainingRounds)
    {
        remainingRounds = pRemainingRounds;
    }
}