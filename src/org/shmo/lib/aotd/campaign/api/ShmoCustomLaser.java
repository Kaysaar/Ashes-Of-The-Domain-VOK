package org.shmo.lib.aotd.campaign.api;

import org.lwjgl.util.vector.Vector2f;

public interface ShmoCustomLaser {
    void setSpec(ShmoCustomLaserSpec spec);
    ShmoCustomLaserSpec getSpec();

    void activate();
    void deactivate();
    boolean isActive();

    float getIntensity();

    void setAnimationT(float t);
    float getAnimationT();

    void advance(float deltaTime);
    void render(float x, float y, float angle, float length);
    void render(Vector2f from, Vector2f to);
}
