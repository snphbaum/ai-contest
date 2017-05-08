package org.byteforce.ai;

/**
 * Interface representing an action that can be take from a certain state.
 *
 * @author Philipp Baumgaertel
 */
public interface Action {
    /**
     *
     * @return the numeric representation of the action type
     */
    int getType();
}