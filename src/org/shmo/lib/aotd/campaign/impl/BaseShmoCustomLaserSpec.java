package org.shmo.lib.aotd.campaign.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.shmo.lib.aotd.campaign.api.ShmoCustomLaserSpec;

import java.awt.*;

public class BaseShmoCustomLaserSpec implements ShmoCustomLaserSpec {
    private String id = null;
    private String coreSprite = null;
    private String fringeSprite = null;
    private Float beamWidth = null;
    private Float fringeWidth = null;
    private Color coreColor = null;
    private Color fringeColor = null;
    private Float coreAlphaMult = null;
    private Float fringeAlphaMult = null;
    private Float animationSpeed = null;
    private Float fadeInTime = null;
    private Float fadeOutTime = null;

    public BaseShmoCustomLaserSpec() {}

    public BaseShmoCustomLaserSpec(BaseShmoCustomLaserSpec other) {
        this.id = other.id;
        this.coreSprite = other.coreSprite;
        this.fringeSprite = other.fringeSprite;
        this.beamWidth = other.beamWidth;

        Color c = other.coreColor;
        if (c != null)
            this.coreColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        else
            this.coreColor = null;

        c = other.fringeColor;
        if (c != null)
            this.fringeColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        else
            this.fringeColor = null;

        this.coreAlphaMult = other.coreAlphaMult;
        this.fringeAlphaMult = other.fringeAlphaMult;
        this.animationSpeed = other.animationSpeed;
    }

    public BaseShmoCustomLaserSpec(ShmoCustomLaserSpec other) {
        setId(other.getId());
        setCoreSprite(other.getCoreSprite());
        setFringeSprite(other.getFringeSprite());
        setCoreWidth(other.getCoreWidth());
        setCoreColor(other.getCoreColor());
        setFringeColor(other.getFringeColor());
        setCoreAlphaMult(other.getCoreAlphaMult());
        setFringeAlphaMult(other.getFringeAlphaMult());
        setAnimationSpeed(other.getAnimationSpeed());
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setCoreSprite(String spriteName) {
        this.coreSprite = spriteName;
    }

    @Override
    public String getCoreSprite() {
        return this.coreSprite;
    }

    @Override
    public void setFringeSprite(String spriteName) {
        this.fringeSprite = spriteName;
    }

    @Override
    public String getFringeSprite() {
        return this.fringeSprite;
    }

    @Override
    public void setCoreWidth(float beamWidth) {
        if (beamWidth <= 0) {
            this.beamWidth = null;
            return;
        }
        this.beamWidth = beamWidth;
    }

    @Override
    public float getCoreWidth() {
        if (this.beamWidth == null) {
            String coreSpriteName = getCoreSprite();
            if (coreSpriteName != null) {
                SpriteAPI sprite = Global.getSettings().getSprite(coreSpriteName);
                if (sprite != null)
                    return sprite.getWidth();
            }
            return 16f;
        }
        return this.beamWidth;
    }

    @Override
    public void setFringeWidth(float beamWidth) {
        if (beamWidth <= 0) {
            this.fringeWidth = null;
            return;
        }
        this.fringeWidth = beamWidth;
    }

    @Override
    public float getFringeWidth() {
        if (this.fringeWidth == null) {
            String fringeSpriteName = getFringeSprite();
            if (fringeSpriteName != null) {
                SpriteAPI sprite = Global.getSettings().getSprite(fringeSpriteName);
                if (sprite != null)
                    return sprite.getWidth();
            }
            return 16f;
        }
        return this.fringeWidth;
    }

    @Override
    public void setCoreColor(Color coreColor) {
        if (coreColor == null) {
            this.coreColor = null;
            return;
        }

        this.coreColor = new Color(
                coreColor.getRed(),
                coreColor.getGreen(),
                coreColor.getBlue(),
                coreColor.getAlpha()
        );
    }

    @Override
    public Color getCoreColor() {
        if (this.coreColor == null)
            return new Color(255, 255, 255, 255);
        return new Color(
                this.coreColor.getRed(),
                this.coreColor.getGreen(),
                this.coreColor.getBlue(),
                this.coreColor.getAlpha()
        );
    }

    @Override
    public void setFringeColor(Color fringeColor) {
        if (fringeColor == null) {
            this.fringeColor = null;
            return;
        }

        this.fringeColor = new Color(
                fringeColor.getRed(),
                fringeColor.getGreen(),
                fringeColor.getBlue(),
                fringeColor.getAlpha()
        );
    }

    @Override
    public Color getFringeColor() {
        if (this.coreColor == null)
            return new Color(255, 255, 255, 255);
        return new Color(
                this.fringeColor.getRed(),
                this.fringeColor.getGreen(),
                this.fringeColor.getBlue(),
                this.fringeColor.getAlpha()
        );
    }

    @Override
    public void setCoreAlphaMult(float alphaMult) {
        if (alphaMult < 0) {
            this.coreAlphaMult = null;
            return;
        }
        this.coreAlphaMult = alphaMult;
    }

    @Override
    public float getCoreAlphaMult() {
        if (this.coreAlphaMult == null)
            return 1.0f;
        return this.coreAlphaMult;
    }

    @Override
    public void setFringeAlphaMult(float alphaMult) {
        if (alphaMult < 0) {
            this.fringeAlphaMult = null;
            return;
        }
        this.fringeAlphaMult = alphaMult;
    }

    @Override
    public float getFringeAlphaMult() {
        if (this.fringeAlphaMult == null)
            return 1.0f;
        return this.fringeAlphaMult;
    }

    @Override
    public void setAnimationSpeed(float animationSpeed) {
        if (animationSpeed < 0) {
            this.animationSpeed = null;
            return;
        }
        this.animationSpeed = animationSpeed;
    }

    @Override
    public float getAnimationSpeed() {
        if (this.animationSpeed == null)
            return 1.0f;
        return this.animationSpeed;
    }

    @Override
    public void setFadeInTime(float fadeInTime) {
        if (fadeInTime < 0) {
            this.fadeInTime = null;
            return;
        }
        this.fadeInTime = fadeInTime;
    }

    @Override
    public float getFadeInTime() {
        if (this.fadeInTime == null)
            return 1.0f;
        return this.fadeInTime;
    }

    @Override
    public void setFadeOutTime(float fadeOutTime) {
        if (fadeOutTime < 0) {
            this.fadeOutTime = null;
            return;
        }
        this.fadeOutTime = fadeOutTime;
    }

    @Override
    public float getFadeOutTime() {
        if (this.fadeOutTime == null)
            return 1.0f;
        return this.fadeOutTime;
    }
}
