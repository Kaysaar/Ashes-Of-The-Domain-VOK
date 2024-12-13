package org.shmo.lib.aotd.campaign.api;

public interface ShmoStateScript {
    /** Executes the state's code, returning the ID of a state to transition to, or `null` to stay in the same state.
     * @param deltaTime Amount to advance the state by.
     * @return ID of state to transition to, or 'null`.
     */
    Object advance(float deltaTime);

    /**
     * Executes setup code for a state when transitioning to it.
     */
    void start();


    /**
     * Executes cleanup code for a state when transitioning from it.
     */
    void end();
}
