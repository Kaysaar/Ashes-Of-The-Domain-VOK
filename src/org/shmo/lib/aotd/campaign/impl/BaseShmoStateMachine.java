package org.shmo.lib.aotd.campaign.impl;

import org.shmo.lib.aotd.campaign.api.ShmoStateMachine;
import org.shmo.lib.aotd.campaign.api.ShmoStateScript;

import java.util.HashMap;
import java.util.Map;

public class BaseShmoStateMachine implements ShmoStateMachine {

    private Object currentState = null;
    private final Map<Object, ShmoStateScript> states = new HashMap<>();

    @Override
    public void addState(Object id, ShmoStateScript script) {
        if (id == null)
            return;
        if (script == null) {
            this.states.remove(id);
            return;
        }
        this.states.put(id, script);
    }

    @Override
    public void setState(Object id) {
        if (id != null && !this.states.containsKey(id)) {
            setState(null);
            return;
        }
        if (this.currentState != null) {
            ShmoStateScript script = this.states.get(this.currentState);
            if (script != null)
                script.end();
            script = states.get(id);
            if (script != null)
                script.start();
        }
        this.currentState = id;
    }

    @Override
    public Object getState() {
        return this.currentState;
    }

    @Override
    public void advance(float deltaTime) {
        if (this.currentState == null)
            return;

        final ShmoStateScript script = this.states.get(this.currentState);
        if (script == null)
            return;

        Object result = script.advance(deltaTime);
        if (result != null) {
            setState(result);
        }
    }

    @Override
    public boolean hasState(Object id) {
        return this.states.containsKey(id);
    }
}
