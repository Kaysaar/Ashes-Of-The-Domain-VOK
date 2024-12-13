package org.shmo.lib.aotd.campaign.api;

import java.awt.*;

public interface ShmoCustomLaserSpec {
    void setId(String id);
    String getId();

    void setCoreSprite(String spriteName);
    String getCoreSprite();

    void setFringeSprite(String spriteName);
    String getFringeSprite();

    void setCoreWidth(float beamWidth);
    float getCoreWidth();

    void setFringeWidth(float beamWidth);
    float getFringeWidth();

    void setCoreColor(Color coreColor);
    Color getCoreColor();

    void setFringeColor(Color fringeColor);
    Color getFringeColor();

    void setCoreAlphaMult(float alphaMult);
    float getCoreAlphaMult();

    void setFringeAlphaMult(float alphaMult);
    float getFringeAlphaMult();

    void setAnimationSpeed(float animationSpeed);
    float getAnimationSpeed();

    void setFadeInTime(float fadeInTime);
    float getFadeInTime();

    void setFadeOutTime(float fadeOutTime);
    float getFadeOutTime();
}
