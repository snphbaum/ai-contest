package org.byteforce.ai;

/**
 * Factory for creating actions from their numeric representation during the learning or replay phase.
 *
 * @author Philipp Baumgaertel
 */
public interface ActionFactory
{
    /**
     *
     * @param i The numeric representation of the action
     * @return An actual Action object representing this action. (Possibly from an enum)
     */
    Action get(int i);



    /**
     *
     * @return The number of possible actions
     */
    int getNumberOfActions();
}
