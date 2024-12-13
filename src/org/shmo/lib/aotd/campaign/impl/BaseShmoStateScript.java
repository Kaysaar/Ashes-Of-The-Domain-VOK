package org.shmo.lib.aotd.campaign.impl;

import org.shmo.lib.aotd.campaign.api.ShmoStateScript;

public abstract class BaseShmoStateScript implements ShmoStateScript {
    @Override
    public Object advance(float deltaTime) { return null; }
    @Override
    public void start() {}
    @Override
    public void end() {}
}
