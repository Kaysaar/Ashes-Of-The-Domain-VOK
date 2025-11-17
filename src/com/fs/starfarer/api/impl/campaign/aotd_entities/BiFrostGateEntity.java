package com.fs.starfarer.api.impl.campaign.aotd_entities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.CustomEntitySpecAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin;
import com.fs.starfarer.api.util.*;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastructure;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class BiFrostGateEntity extends BaseCustomEntityPlugin {

    transient protected SpriteAPI baseSprite;
    transient protected SpriteAPI scannedGlow;

    transient protected SpriteAPI activeGlow;
    transient protected SpriteAPI whirl1;
    transient protected SpriteAPI whirl2;
    transient protected SpriteAPI starfield;
    transient protected SpriteAPI rays;
    transient protected SpriteAPI concentric;
    transient protected WarpingSpriteRendererUtil warp;

    protected FaderUtil beingUsedFader = new FaderUtil(0f, 1f, 1f, false, true);
    protected FaderUtil glowFader = new FaderUtil(0f, 1f, 1f, true, true);
    protected boolean madeActive = false;
    protected boolean addedIntel = false;
    protected float showBeingUsedDur = 0f;
    protected float accumulatedTransitDistLY = 0f;
    protected float inUseAngle = 0f;
    protected Color jitterColor = null;
    protected JitterUtil jitter;
    protected FaderUtil jitterFader = null;
    transient protected boolean scaledSprites = false;
    protected IntervalUtil moteSpawn = null;
    public boolean isDeActivated;

    public void setDeActivated(boolean deActivated) {
        isDeActivated = deActivated;
    }

    public boolean isDeActivated() {
        return isDeActivated;
    }


    public void render(CampaignEngineLayers layer, ViewportAPI viewport) {
        if(scannedGlow==null){
            scannedGlow = Global.getSettings().getSprite("gates", "glow_scanned");
            activeGlow = Global.getSettings().getSprite("gates", "glow_ring_active");
            concentric = Global.getSettings().getSprite("gates", "glow_concentric");
            rays = Global.getSettings().getSprite("gates", "glow_rays");
            whirl1 = Global.getSettings().getSprite("gates", "glow_whirl1");
            whirl2 = Global.getSettings().getSprite("gates", "glow_whirl2");
            starfield = Global.getSettings().getSprite("gates", "starfield");
            int height = 90;
            scannedGlow.setSize(height, height);
            activeGlow.setSize(height, height);
            concentric.setSize(height, height);
            rays.setSize(height, height);
            whirl1.setSize(height, height);
            whirl2.setSize(height, height);
            starfield.setSize(height, height);


        }
        if (layer == CampaignEngineLayers.BELOW_STATIONS) {
            boolean beingUsed = !beingUsedFader.isFadedOut();
            if (beingUsed) {
                float alphaMult = viewport.getAlphaMult();
                alphaMult *= entity.getSensorFaderBrightness();
                alphaMult *= entity.getSensorContactFaderBrightness();
                if (alphaMult <= 0f) return;

                if (warp == null) {
                    int cells = 6;
                    float cs = starfield.getWidth() / 10f;
                    warp = new WarpingSpriteRendererUtil(cells, cells, cs * 0.2f, cs * 0.2f, 2f);
                }

                Vector2f loc = entity.getLocation();

                float glowAlpha = 1f;

                glowAlpha *= beingUsedFader.getBrightness();

                starfield.setAlphaMult(alphaMult * glowAlpha);
                starfield.setColor(new Color(141, 102, 255));
                starfield.setAdditiveBlend();
                //starfield.renderAtCenter(loc.x + 1.5f, loc.y);

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                warp.renderNoBlendOrRotate(starfield, loc.x + 1.5f - starfield.getWidth() / 2f,
                        loc.y - starfield.getHeight() / 2f, false);
            }
        }
        if (layer == CampaignEngineLayers.STATIONS) {
            float alphaMult = viewport.getAlphaMult();
            alphaMult *= entity.getSensorFaderBrightness();
            alphaMult *= entity.getSensorContactFaderBrightness();
            if (alphaMult <= 0f) return;

            CustomEntitySpecAPI spec = entity.getCustomEntitySpec();
            if (spec == null) return;

            float w = spec.getSpriteWidth();
            float h = spec.getSpriteHeight();

            float scale = spec.getSpriteWidth() / Global.getSettings().getSprite(spec.getSpriteName()).getWidth();

            Vector2f loc = entity.getLocation();


            Color scannedGlowColor = new Color(255,200,0,255);
            Color activeGlowColor = new Color(200,50,255,255);

            scannedGlowColor = Color.white;
            activeGlowColor = Color.white;

            float glowAlpha = 1f;


            float glowMod1 = 0.5f + 0.5f * glowFader.getBrightness();
            float glowMod2 = 0.75f + 0.25f * glowFader.getBrightness();

            boolean beingUsed = !beingUsedFader.isFadedOut();


            if (jitterFader != null && jitter != null) {
                Color c = jitterColor;
                if (c == null) c = new Color(255,255,255,255);
                baseSprite.setColor(c);
                baseSprite.setAlphaMult(alphaMult * jitterFader.getBrightness());
                baseSprite.setAdditiveBlend();
                jitter.render(baseSprite, loc.x, loc.y, 30f * jitterFader.getBrightness(), 10);
                baseSprite.renderAtCenter(loc.x, loc.y);
            }
                activeGlow.setColor(activeGlowColor);
                //activeGlow.setSize(w * scale, h * scale);
                activeGlow.setAlphaMult(alphaMult * glowAlpha * glowMod2);
                activeGlow.setAdditiveBlend();
                activeGlow.renderAtCenter(loc.x, loc.y);


//			beingUsed = true;
//			showBeingUsedDur = 10f;
            if (beingUsed) {
                    activeGlow.setColor(activeGlowColor);
                    //activeGlow.setSize(w + 20, h + 20);
                    activeGlow.setAlphaMult(alphaMult * glowAlpha * beingUsedFader.getBrightness() * glowMod2);
                    activeGlow.setAdditiveBlend();
                    activeGlow.renderAtCenter(loc.x, loc.y);

                glowAlpha *= beingUsedFader.getBrightness();
                float angle;

                rays.setAlphaMult(alphaMult * glowAlpha);
                rays.setAdditiveBlend();
                rays.renderAtCenter(loc.x + 1.5f, loc.y);

                concentric.setAlphaMult(alphaMult * glowAlpha * 1f);
                concentric.setAdditiveBlend();
                concentric.renderAtCenter(loc.x + 1.5f, loc.y);

                angle = -inUseAngle * 0.25f;
                angle = Misc.normalizeAngle(angle);
                whirl1.setAngle(angle);
                whirl1.setAlphaMult(alphaMult * glowAlpha);
                whirl1.setAdditiveBlend();
                whirl1.renderAtCenter(loc.x + 1.5f, loc.y);

                angle = -inUseAngle * 0.33f;
                angle = Misc.normalizeAngle(angle);
                whirl2.setAngle(angle);
                whirl2.setAlphaMult(alphaMult * glowAlpha * 0.5f);
                whirl2.setAdditiveBlend();
                whirl2.renderAtCenter(loc.x + 1.5f, loc.y);
            }
        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (this.entity.getMemory().is("$used", true)) {
            float value = this.entity.getMemory().getFloat("$cooldown");
            value -= Global.getSector().getClock().convertToDays(amount);
            this.entity.getMemory().set("$cooldown", value);
            if (value <= 0) {
                this.entity.getMemory().set("$cooldown", 0);
                this.entity.getMemory().set("$used", false);
            }
        }
        if(HypershuntMegastructure.isWithinReceiverSystem(this.entity)){
            this.entity.getMemory().set("$cooldown", 0);
            this.entity.getMemory().set("$used", false);
            this.entity.getMemory().set("$connected",true);
        }
        else{
            this.entity.getMemory().set("$connected",false);
        }
        if (showBeingUsedDur > 0 || !beingUsedFader.isIdle()) {
            showBeingUsedDur -= amount;
            if (showBeingUsedDur > 0) {
                beingUsedFader.fadeIn();
            } else {
                showBeingUsedDur = 0f;
            }
            inUseAngle += amount * 60f;
            if (warp != null) {
                warp.advance(amount);
            }
        }
        glowFader.advance(amount);

//		if (entity.isInCurrentLocation()) {
//			System.out.println("BRIGHTNESS: " + beingUsedFader.getBrightness());
//		}

        if (jitterFader != null) {
            jitterFader.advance(amount);
            if (jitterFader.isFadedOut()) {
                jitterFader = null;
            }
        }

        beingUsedFader.advance(amount);
    }

    public void showBeingUsed(float transitDistLY) {
        showBeingUsed(10f, transitDistLY);
    }

    public void showBeingUsed(float dur, float transitDistLY) {
        beingUsedFader.fadeIn();
        showBeingUsedDur = dur;

        accumulatedTransitDistLY += transitDistLY;

//		if (withSound && entity.isInCurrentLocation()) {
//			Global.getSoundPlayer().playSound("gate_being_used", 1, 1, entity.getLocation(), entity.getVelocity());
//		}
    }
}
