package org.shmo.lib.aotd.campaign.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.lib.aotd.campaign.api.*;

import java.awt.*;

public class BaseShmoCustomLaser implements ShmoCustomLaser {
    public enum State {
        IN,
        ACTIVE,
        OUT,
        INACTIVE
    }

    private ShmoCustomLaserSpec spec = null;
    private float intensity = 0f;
    private float currentLengthFraction = 0f;
    private float animationT = 0f;
    private final ShmoStateMachine stateMachine;

    public BaseShmoCustomLaser() {
        this.stateMachine = ShmoFactory.createStateMachine();
        configureStateMachine();
        setState(State.INACTIVE);
    }

    @Override
    public void setSpec(ShmoCustomLaserSpec spec) {
        this.spec = spec;
    }

    @Override
    public ShmoCustomLaserSpec getSpec() {
        return this.spec;
    }

    @Override
    public void activate() {
        if (isActive())
            return;
        setState(State.IN);
    }

    @Override
    public void deactivate() {
        if (!isActive())
            return;
        setState(State.OUT);
    }

    @Override
    public boolean isActive() {
        if (this.stateMachine.getState() == null)
            return false;
        return this.stateMachine.getState().equals(State.ACTIVE) ||
                this.stateMachine.getState().equals(State.IN);
    }

    protected void setState(State state) {
        this.stateMachine.setState(state);
    }

    protected State getState() {
        return (State)this.stateMachine.getState();
    }

    @Override
    public float getIntensity() {
        return this.intensity;
    }

    @Override
    public void setAnimationT(float t) {
        while (t >= 1f) {
            t -= 1f;
        }
        while (t < 0f) {
            t += 1f;
        }
        this.animationT = t;
    }

    @Override
    public float getAnimationT() {
        return this.animationT;
    }

    @Override
    public void advance(float deltaTime) {
        if (this.spec == null)
            return;
        setAnimationT(getAnimationT() + deltaTime * this.spec.getAnimationSpeed());
        configureStateMachine();
        this.stateMachine.advance(deltaTime);
        if (this.intensity > 0) {
            this.currentLengthFraction = Math.max(this.currentLengthFraction, this.intensity);
        } else {
            this.currentLengthFraction = 0;
        }
    }

    @Override
    public void render(float x, float y, float angle, float length) {
        if (this.spec == null)
            return;
        if (this.intensity <= 0 || length <= 0)
            return;

        SpriteAPI coreSprite = null;
        SpriteAPI fringeSprite = null;

        if (this.spec.getCoreSprite() != null)
            coreSprite = Global.getSettings().getSprite(this.spec.getCoreSprite());
        if (this.spec.getFringeSprite() != null)
            fringeSprite = Global.getSettings().getSprite(this.spec.getFringeSprite());

        renderBeam(
                x,
                y,
                angle,
                length * this.currentLengthFraction,
                fringeSprite,
                this.spec.getFringeWidth(),
                this.spec.getFringeColor(),
                this.spec.getFringeAlphaMult(),
                this.intensity,
                this.animationT
        );

        renderBeam(
                x,
                y,
                angle,
                length * this.currentLengthFraction,
                coreSprite,
                this.spec.getCoreWidth(),
                this.spec.getCoreColor(),
                this.spec.getCoreAlphaMult(),
                this.intensity,
                this.animationT
        );
    }

    @Override
    public void render(Vector2f from, Vector2f to) {
        if (from == null || to == null)
            return;
        final float dx = to.x - from.x;
        final float dy = to.y - from.y;
        final Vector2f diff = new Vector2f(dx, dy);
        if (diff.lengthSquared() == 0f)
            return;
        final float angle = Misc.getAngleInDegrees(diff);
        final float length = diff.length();
        render(from.x, from.y, angle, length);
    }

    private static void renderBeam(
            float x,
            float y,
            float angle,
            float length,
            SpriteAPI sprite,
            float beamSize,
            Color color,
            float alphaMult,
            float intensity,
            float animationT
    ) {
        if (sprite == null)
            return;
        float spriteWidth = sprite.getWidth();
        float textureWidth = sprite.getTextureWidth();
        float textureHeight = sprite.getTextureHeight();
        angle += 180;
        alphaMult *= intensity;
        beamSize *= intensity;
        Vector2f location = new Vector2f(x, y);

        Vector2f direction = Misc.getUnitVectorAtDegreeAngle(angle);
        direction.scale(spriteWidth);

        Vector2f offset = Misc.getUnitVectorAtDegreeAngle(angle);
        offset.scale(-(spriteWidth / 2) + spriteWidth * animationT);

        location.x -= offset.x;
        location.y -= offset.y;

        sprite.setAdditiveBlend();
        sprite.setAngle(angle);
        sprite.setColor(new Color(color.getRed(), color.getGreen(), color.getGreen(), (int)(color.getAlpha() * alphaMult)));
        sprite.setSize(spriteWidth, beamSize);

        final int nSegments = (int)(length / spriteWidth);
        final float remainder = (length / spriteWidth) - nSegments;

        for (int i = 0; i < nSegments; i++) {
            location.x -= direction.x;
            location.y -= direction.y;
            sprite.renderRegionAtCenter(location.x, location.y, animationT * textureWidth, 0, textureWidth, textureHeight);
        }
        if (remainder > 0) {
            sprite.renderRegionAtCenter(location.x, location.y, animationT * textureWidth - remainder, 0, textureWidth * remainder, textureHeight);
        }
    }

    private void configureStateMachine() {
        final BaseShmoCustomLaser thisLaser = this;
        final ShmoStateMachine sm = this.stateMachine;
        if (!sm.hasState(State.IN)) {
            sm.addState(State.IN, new ShmoStateScript() {
                @Override
                public Object advance(float deltaTime) {
                    final float fadeInTime = thisLaser.getSpec().getFadeInTime();
                    if (fadeInTime <= 0) {
                        thisLaser.intensity = 1;
                        return State.ACTIVE;
                    }
                    thisLaser.intensity += deltaTime / fadeInTime;
                    if (thisLaser.intensity >= 1) {
                        thisLaser.intensity = 1;
                        return State.ACTIVE;
                    }
                    return null;
                }

                @Override
                public void start() {}

                @Override
                public void end() {}
            });
        }

        if (!sm.hasState(State.ACTIVE)) {
            sm.addState(State.ACTIVE, new ShmoStateScript() {
                @Override
                public Object advance(float deltaTime) {
                    return null;
                }

                @Override
                public void start() {}

                @Override
                public void end() {}
            });
        }

        if (!sm.hasState(State.OUT)) {
            sm.addState(State.OUT, new ShmoStateScript() {
                @Override
                public Object advance(float deltaTime) {
                    final float fadeOutTime = thisLaser.getSpec().getFadeOutTime();
                    if (fadeOutTime <= 0) {
                        thisLaser.intensity = 0;
                        return State.INACTIVE;
                    }
                    thisLaser.intensity -= deltaTime / fadeOutTime;
                    if (thisLaser.intensity <= 0) {
                        thisLaser.intensity = 0;
                        return State.INACTIVE;
                    }
                    return null;
                }

                @Override
                public void start() {}

                @Override
                public void end() {}
            });
        }

        if (!sm.hasState(State.INACTIVE)) {
            sm.addState(State.INACTIVE, new ShmoStateScript() {
                @Override
                public Object advance(float deltaTime) {
                    return null;
                }

                @Override
                public void start() {}

                @Override
                public void end() {}
            });
        }
    }
}
