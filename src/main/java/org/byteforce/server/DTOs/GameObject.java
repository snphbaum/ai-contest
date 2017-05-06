package org.byteforce.server.DTOs;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


/**
 * @author Philipp Baumgaertel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({@JsonSubTypes.Type(value = Player.class, name = "Player"),
    @JsonSubTypes.Type(value = DestructibleWall.class, name = "DestructibleWall"),
    @JsonSubTypes.Type(value = Wall.class, name = "Wall"), @JsonSubTypes.Type(value = Bomb.class, name = "Bomb")})
public class GameObject
{
    private int x;

    private int y;


    public int getX()
    {
        return x;
    }

    public void setX(final int pX)
    {
        x = pX;
    }
}

