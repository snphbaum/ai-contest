package org.byteforce.server.DTOs;

import java.io.Serializable;
import java.util.List;


/**
 * @author Philipp Baumgaertel
 */
public class GameState
{
    private int gameId;

    private int width;

    private int height;

    private int sequenceNumber;

    private String nextPlayer;

    private Player player1;

    private Player player2;

    private List<GameObject> objects;



    public int getGameId()
    {
        return gameId;
    }



    public void setGameId(final int pGameId)
    {
        gameId = pGameId;
    }



    public int getSequenceNumber()
    {
        return sequenceNumber;
    }



    public void setSequenceNumber(final int pSequenceNumber)
    {
        sequenceNumber = pSequenceNumber;
    }



    public int getWidth()
    {
        return width;
    }



    public void setWidth(final int pWidth)
    {
        width = pWidth;
    }



    public int getHeight()
    {
        return height;
    }



    public void setHeight(final int pHeight)
    {
        height = pHeight;
    }



    public String getNextPlayer()
    {
        return nextPlayer;
    }



    public void setNextPlayer(final String pNextPlayer)
    {
        nextPlayer = pNextPlayer;
    }



    public List<GameObject> getObjects()
    {
        return objects;
    }



    public void setObjects(final List<GameObject> pObjects)
    {
        objects = pObjects;
    }



    public Player getPlayer1()
    {
        return player1;
    }



    public void setPlayer1(final Player pPlayer1)
    {
        player1 = pPlayer1;
    }



    public Player getPlayer2()
    {
        return player2;
    }



    public void setPlayer2(final Player pPlayer2)
    {
        player2 = pPlayer2;
    }
}
