package org.byteforce.ai;

/**
 * A factory to create states during the learning or replay phase
 *
 * @author Philipp Baumgaertel
 */
public interface StateFactory
{

    /**
     * If the intial state is random, is determined by the Factory Implementation
     *
     * @return A new initial state
     */
    State getState();

    /**
     *
     * @return The length of the linear input representation
     */
    int getInputLength();
}
