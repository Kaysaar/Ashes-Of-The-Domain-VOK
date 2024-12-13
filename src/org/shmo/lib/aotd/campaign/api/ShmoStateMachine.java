package org.shmo.lib.aotd.campaign.api;

public interface ShmoStateMachine {

    /** Register a state with this state machine.
     * @param id ID of the state.
     * @param script Code to run when the state is executed.
     */
    void addState(Object id, ShmoStateScript script);

    /** Transition to a given state.
     * @param id ID of the state to transition to, or `null` to clear state.
     */
    void setState(Object id);

    /** Gets the current state.
     * @return The current state, or `null` if no state.
     */
    Object getState();

    /** Update the current state, or do nothing if there is no current state.
     * @param deltaTime Amount to advance state by.
     */
    void advance(float deltaTime);

    /**
     * @param id ID of the state to check for.
     * @return 'true' if the state machine has a state with the given ID.
     */
    boolean hasState(Object id);
}
