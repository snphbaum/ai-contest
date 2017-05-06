package org.byteforce.server;

import java.io.Serializable;


/**
 * @author Philipp Baumgaertel
 */
public class TestDTO
    implements Serializable
{
    private String name;

    private int number;

    private static final long serialVersionUID = 42L;

    public TestDTO(){
        name = "";
        number = 0;
    }

    public TestDTO(String pName, int pNumber ){
        name = pName;
        number = pNumber;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(final int pNumber)
    {
        number = pNumber;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String pName)
    {
        name = pName;
    }

    @Override
    public String toString(){
        return name + ":" + number;
    }
}
