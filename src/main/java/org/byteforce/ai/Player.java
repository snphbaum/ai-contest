package org.byteforce.ai;

/**
 * @author Philipp Baumgaertel
 */
public interface Player
{
    Action getAction(State pState);
    boolean isPlayerZero();
}
