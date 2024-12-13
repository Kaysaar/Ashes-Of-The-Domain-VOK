package com.fs.starfarer.api.impl.campaign.aotd_entities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.PlutoMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.lib.aotd.campaign.api.*;
import org.shmo.lib.aotd.campaign.impl.BaseShmoStateScript;

import java.awt.*;

public class PlutoMiningStation extends BaseCustomEntityPlugin {
    private static final float LASER_CONTACT_PARTICLE_INTERVAL = 0.1f;
    private static final float LASER_CONTACT_PARTICLE_MAX_SPEED = 32f;
    private static final float LASER_CONTACT_PARTICLE_WINDUP = 0.25f;
    private static final float LASER_CONTACT_PARTICLE_LIFETIME = 1f;

    private static final float MAX_CONTACT_GLOW_CORE_SIZE = 80f;
    private static final float MAX_CONTACT_GLOW_FRINGE_SIZE = 128f;
    private static final float CONTACT_GLOW_INCREASE_RATE = 2f;
    private static final float CONTACT_GLOW_DECREASE_RATE = 1f;

    private static final float PLANET_HEAT_INCREASE_RATE = 0.5f;
    private static final float PLANET_HEAT_DECREASE_RATE = 0.1f;

    private static final float IN_TIME = 3f;
    private static final float OUT_TIME = 3f;

    private static final String CLAIMED_KEY = "$aotd_claimed";

    public enum State {
        INACTIVE,
        IN,
        ACTIVE,
        OUT
    }

    private transient ShmoCustomLaserSpec laserSpec = null;

    private ShmoCustomLaser laser = null;
    private ShmoStateMachine stateMachine = null;

    private float laserContactGlowFlickerAmount = 0;
    private float laserContactGlowAmount = 0;
    private float planetHeatAmount = 0;
    private float laserContactParticleTimer = 0;

    private float inTimer = 0f;
    private float outTimer = 0f;

    private boolean activatedForFirstTime = false;

    private ShmoCustomLaser getLaser() {
        if (this.laser == null) {
            this.laser = ShmoFactory.createCustomLaser(null);
        }
        if (this.laserSpec == null) {
            this.laserSpec = ShmoFactory.createCustomLaserSpec();
            this.laserSpec.setCoreSprite(Global.getSettings().getSpriteName("rendering", "aotd_beam2"));
            this.laserSpec.setFringeSprite(Global.getSettings().getSpriteName("rendering", "aotd_beam3"));
            this.laserSpec.setCoreColor(new Color(255, 221, 207));
            this.laserSpec.setCoreAlphaMult(1f);
            this.laserSpec.setCoreWidth(24f);
            this.laserSpec.setFringeColor(new Color(255, 25, 97));
            this.laserSpec.setFringeAlphaMult(0.25f);
            this.laserSpec.setFringeWidth(48f);
            this.laserSpec.setFadeInTime(0.5f);
            this.laserSpec.setFadeOutTime(0.5f);
            this.laser.setSpec(this.laserSpec);
        }
        return this.laser;
    }

    private ShmoStateMachine getStateMachine() {
        if (this.stateMachine == null) {
            this.stateMachine = ShmoFactory.createStateMachine();
            this.stateMachine.addState(State.INACTIVE, new BaseShmoStateScript() {
                @Override
                public void start() {
                    getLaser().deactivate();
                }
            });
            this.stateMachine.addState(State.IN, new BaseShmoStateScript() {
                @Override
                public Object advance(float deltaTime) {
                    inTimer += deltaTime / IN_TIME;
                    if (inTimer >= 1f) {
                        return State.ACTIVE;
                    }
                    return null;
                }
                @Override
                public void end() {
                    inTimer = 0f;
                }
            });
            this.stateMachine.addState(State.ACTIVE, new BaseShmoStateScript() {
                @Override
                public void start() {
                    getLaser().activate();
                }
            });
            this.stateMachine.addState(State.OUT, new BaseShmoStateScript() {
                @Override
                public Object advance(float deltaTime) {
                    outTimer += deltaTime / OUT_TIME;
                    if (outTimer >= 1f) {
                        return State.INACTIVE;
                    }
                    return null;
                }
                @Override
                public void end() {
                    outTimer = 0f;
                }
            });
            this.stateMachine.setState(State.INACTIVE);
        }
        return this.stateMachine;
    }

    public State getState() {
        return (State)getStateMachine().getState();
    }

    private void setState(State state) {
        getStateMachine().setState(state);
    }

    public boolean isClaimed() {
        return this.entity.getMemoryWithoutUpdate().getBoolean(CLAIMED_KEY);
    }

    public boolean isActive() {
        return getState().equals(State.IN) || getState().equals(State.ACTIVE);
    }

    public void activate() {
        if (isActive())
            return;
        setState(State.IN);
    }

    public void deactivate() {
        if (!isActive())
            return;
        setState(State.OUT);
    }

    @Override
    public void advance(float amount) {
        PlutoMegastructure megastructure = (PlutoMegastructure) entity.getMemory().get(GPBaseMegastructure.memKey);
        if(megastructure!=null){
            if(megastructure.getLaserSection().isFiringLaser()&&!isActive()){
                activate();
            }
            if(!megastructure.getLaserSection().isFiringLaser()&&isActive()){
                deactivate();
            }
            updateStateMachine(amount);
            updateLaser(amount);
            updateLaserContactGlow(amount);
            updateLaserContactParticles(amount);
        }

    }

    private void updateStateMachine(float amount) {
        getStateMachine().advance(amount);
    }

    private void updateLaser(float amount) {
        getLaser().advance(amount);
    }

    private void updateLaserContactGlow(float amount) {
        final float glowFlickerDelta = (Misc.random.nextFloat() * 2) - 1f;
        this.laserContactGlowFlickerAmount += glowFlickerDelta * amount;
        if (this.laserContactGlowFlickerAmount < 0 || this.laserContactGlowFlickerAmount > 1) {
            this.laserContactGlowFlickerAmount -= (glowFlickerDelta * amount) * 2;
        }
        this.laserContactGlowFlickerAmount = Math.max(0f, Math.min(1f, this.laserContactGlowFlickerAmount));

        if (getLaser().getIntensity() >= 1) {
            this.laserContactGlowAmount += amount * CONTACT_GLOW_INCREASE_RATE;
            this.planetHeatAmount += amount * PLANET_HEAT_INCREASE_RATE;
        } else {
            this.laserContactGlowAmount -= amount * CONTACT_GLOW_DECREASE_RATE;
            this.planetHeatAmount -= amount * PLANET_HEAT_DECREASE_RATE;
        }
        this.laserContactGlowAmount = Math.max(0f, Math.min(this.laserContactGlowAmount, 1f));
        this.planetHeatAmount = Math.max(0f, Math.min(this.planetHeatAmount, 1f));
    }

    private void updateLaserContactParticles(float amount) {
        final SectorEntityToken orbitFocus = entity.getOrbitFocus();
        if (orbitFocus == null)
            return;
        final ShmoCustomLaser laser = getLaser();
        if (laser.getIntensity() < 1f)
            return;

        this.laserContactParticleTimer += amount;
        while (this.laserContactParticleTimer > LASER_CONTACT_PARTICLE_INTERVAL) {
            this.laserContactParticleTimer -= LASER_CONTACT_PARTICLE_INTERVAL;
            final Vector2f glowDir = Misc.getUnitVectorAtDegreeAngle(this.entity.getCircularOrbitAngle());
            glowDir.scale(orbitFocus.getRadius());
            final float glow1X = orbitFocus.getLocation().x + glowDir.x;
            final float glow1Y = orbitFocus.getLocation().y + glowDir.y;
            final Vector2f vel = Misc.getPointWithinRadius(new Vector2f(), LASER_CONTACT_PARTICLE_MAX_SPEED);
            final Color color = Misc.random.nextFloat() > 0.8f ?
                    laser.getSpec().getFringeColor() : laser.getSpec().getCoreColor();
            Misc.addGlowyParticle(
                    this.entity.getContainingLocation(),
                    new Vector2f(glow1X, glow1Y),
                    vel,
                    (Misc.random.nextFloat() + 1.0f) * 8f,
                    LASER_CONTACT_PARTICLE_WINDUP,
                    LASER_CONTACT_PARTICLE_LIFETIME,
                    color
            );
        }
    }

    @Override
    public void render(CampaignEngineLayers layer, ViewportAPI viewport) {
        if (layer.equals(CampaignEngineLayers.STATIONS)) {
            final SpriteAPI plutoGeneralGlow = Global.getSettings().getSprite("rendering", "aotd_pluto_general_glow");
            final SpriteAPI plutoLaserGlow = Global.getSettings().getSprite("rendering", "aotd_pluto_laser_glow");
            final SpriteAPI plutoLaserFlareGlow = Global.getSettings().getSprite("rendering", "aotd_pluto_laserflare_glow");

            switch (getState()) {
                case IN: {
                    final float t = ShmoEase.inBounce(this.inTimer);
                    plutoLaserGlow.setAlphaMult(t);
                    plutoLaserFlareGlow.setAlphaMult(t);
                    break;
                }
                case OUT: {
                    final float t = ShmoEase.outBounce(1f - this.outTimer);
                    plutoLaserGlow.setAlphaMult(t);
                    plutoLaserFlareGlow.setAlphaMult(t);
                    break;
                }
                case INACTIVE:
                    PlutoMegastructure megastructure = (PlutoMegastructure) entity.getMemory().get(GPBaseMegastructure.memKey);
                    if(megastructure!=null&&megastructure.isClaimed()){
                        plutoGeneralGlow.setAlphaMult(1f);
                    }

                    renderAtCenter(plutoGeneralGlow);
                    return;
            }
            PlutoMegastructure megastructure = (PlutoMegastructure) entity.getMemory().get(GPBaseMegastructure.memKey);
            if(megastructure!=null&&megastructure.isClaimed()){
                plutoGeneralGlow.setAlphaMult(1f);
            }

            renderAtCenter(plutoGeneralGlow);
            renderAtCenter(plutoLaserGlow);
            renderAtCenter(plutoLaserFlareGlow);
        }

        if (layer.equals(CampaignEngineLayers.BELOW_STATIONS)) {
            renderLaser();
            renderLaserContactGlow();
        }
    }

    private void renderLaser() {
        final SectorEntityToken orbitFocus = this.entity.getOrbitFocus();
        if (orbitFocus == null)
            return;
        getLaser().render(
                this.entity.getLocation().x,
                this.entity.getLocation().y,
                this.entity.getCircularOrbitAngle() + 180,
                this.entity.getCircularOrbitRadius() - orbitFocus.getRadius()
        );
    }

    private void renderLaserContactGlow() {
        final SectorEntityToken orbitFocus = this.entity.getOrbitFocus();
        if (orbitFocus == null)
            return;
        final ShmoCustomLaser laser = getLaser();
        final float intensity = laser.getIntensity();
        final float flicker = (1f - (1f - (this.laserContactGlowFlickerAmount * 0.5f))) + 0.5f;
        final float intensitySquared = intensity * intensity;

        final SpriteAPI planetGlow0 = Global.getSettings().getSprite("rendering", "aotd_planet_glow0");
        final SpriteAPI planetGlow1 = Global.getSettings().getSprite("rendering", "aotd_planet_glow1");
        final SpriteAPI planetHeat = Global.getSettings().getSprite("rendering", "aotd_planet_glow2");

        planetHeat.setSize(orbitFocus.getRadius() * 2, orbitFocus.getRadius() * 2);
        planetHeat.setAngle(this.entity.getCircularOrbitAngle());
        planetHeat.setColor(Color.RED);
        planetHeat.setAlphaMult(this.planetHeatAmount);
        planetHeat.setAdditiveBlend();
        planetHeat.renderAtCenter(orbitFocus.getLocation().x, orbitFocus.getLocation().y);
        planetHeat.setColor(Color.WHITE);
        planetHeat.setAlphaMult((this.planetHeatAmount * this.planetHeatAmount * this.planetHeatAmount) * 0.6666f);
        planetHeat.renderAtCenter(orbitFocus.getLocation().x, orbitFocus.getLocation().y);

        planetGlow0.setSize(orbitFocus.getRadius() * 2, orbitFocus.getRadius() * 2);
        planetGlow0.setAngle(this.entity.getCircularOrbitAngle());
        planetGlow0.setColor(laser.getSpec().getFringeColor());
        planetGlow0.setAlphaMult(intensitySquared * flicker * 0.5f);
        planetGlow0.setAdditiveBlend();
        planetGlow0.renderAtCenter(orbitFocus.getLocation().x, orbitFocus.getLocation().y);

        final Vector2f glowDir = Misc.getUnitVectorAtDegreeAngle(this.entity.getCircularOrbitAngle());
        glowDir.scale(orbitFocus.getRadius());
        final float glow1X = orbitFocus.getLocation().x + glowDir.x;
        final float glow1Y = orbitFocus.getLocation().y + glowDir.y;
        final float glow1Size = (this.laserContactGlowAmount * flicker) * MAX_CONTACT_GLOW_CORE_SIZE;
        final float glow1FringeSize = (this.laserContactGlowAmount * flicker) * MAX_CONTACT_GLOW_FRINGE_SIZE;

        planetGlow1.setSize(glow1FringeSize, glow1FringeSize);
        planetGlow1.setColor(laser.getSpec().getFringeColor());
        planetGlow1.setAlphaMult(this.laserContactGlowAmount * 0.5f);
        planetGlow1.setAdditiveBlend();
        planetGlow1.renderAtCenter(glow1X, glow1Y);

        planetGlow1.setSize(glow1Size, glow1Size);
        planetGlow1.setColor(laser.getSpec().getCoreColor());
        planetGlow1.setAlphaMult(this.laserContactGlowAmount);
        planetGlow1.renderAtCenter(glow1X, glow1Y);
    }

    public void renderAtCenter(SpriteAPI sprite){
        sprite.setAngle(this.entity.getCircularOrbitAngle() - 90);
        sprite.renderAtCenter(this.entity.getLocation().x, this.entity.getLocation().y);
    }

}
