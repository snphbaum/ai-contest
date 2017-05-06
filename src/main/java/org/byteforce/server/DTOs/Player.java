package org.byteforce.server.DTOs;

/**
 * @author Philipp Baumgaertel
 */
public class Player
    extends GameObject
{
    String Name;



    public String getName()
    {
        return Name;
    }



    public void setName(final String pName)
    {
        Name = pName;
    }
}

