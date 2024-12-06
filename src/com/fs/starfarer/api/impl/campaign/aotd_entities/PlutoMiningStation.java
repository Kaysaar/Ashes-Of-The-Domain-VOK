package com.fs.starfarer.api.impl.campaign.aotd_entities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.PlutoMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections.OpticCommandNexus;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;

public class PlutoMiningStation extends BaseCustomEntityPlugin {
    private static final float GROWTH_STEP = 0.002f;
    protected SpriteAPI aotd_pluto_general_glow = Global.getSettings().getSprite("rendering", "aotd_pluto_general_glow");
    protected SpriteAPI aotd_pluto_laser_glow_white = Global.getSettings().getSprite("rendering", "aotd_pluto_laser_glow_white");
    protected SpriteAPI aotd_pluto_laser_glow = Global.getSettings().getSprite("rendering", "aotd_pluto_laser_glow");
    protected SpriteAPI aotd_pluto_laserfire_glow = Global.getSettings().getSprite("rendering", "aotd_pluto_laserfire_glow");
    protected SpriteAPI aotd_pluto_laserflare_glow = Global.getSettings().getSprite("rendering", "aotd_pluto_laserflare_glow");
    protected SpriteAPI aotd_beam0 = Global.getSettings().getSprite("rendering", "aotd_beam0");
    protected SpriteAPI aotd_beam1 = Global.getSettings().getSprite("rendering", "aotd_beam1");
    protected SpriteAPI aotd_beam2 = Global.getSettings().getSprite("rendering", "aotd_beam2");
    protected SpriteAPI aotd_beam3 = Global.getSettings().getSprite("rendering", "aotd_beam3");
    protected SpriteAPI aotd_beam4 = Global.getSettings().getSprite("rendering", "aotd_beam4");
    public float lastMagnitude;
    public float distance;
    float magnitudeEffect = 0.0f; //Strength of effect between 0 and 1
    @Override
    public void render(CampaignEngineLayers layer, ViewportAPI viewport) {
        if (aotd_pluto_general_glow == null) {
            aotd_pluto_general_glow = Global.getSettings().getSprite("rendering", "aotd_pluto_general_glow");
            aotd_pluto_laser_glow_white = Global.getSettings().getSprite("rendering", "aotd_pluto_laser_glow_white");
            aotd_pluto_laser_glow = Global.getSettings().getSprite("rendering", "aotd_pluto_laser_glow");
            aotd_pluto_laserfire_glow = Global.getSettings().getSprite("rendering", "aotd_pluto_laserfire_glow");
            aotd_pluto_laserflare_glow = Global.getSettings().getSprite("rendering", "aotd_pluto_laserflare_glow");
            aotd_beam0 = Global.getSettings().getSprite("rendering", "aotd_beam0");
            aotd_beam1 = Global.getSettings().getSprite("rendering", "aotd_beam1");
            aotd_beam2 = Global.getSettings().getSprite("rendering", "aotd_beam2");
            aotd_beam3 = Global.getSettings().getSprite("rendering", "aotd_beam3");
            aotd_beam4 = Global.getSettings().getSprite("rendering", "aotd_beam4");
        }
        if(entity.getMemory().contains(GPBaseMegastructure.memKey)){
            PlutoMegastructure megastructure = (PlutoMegastructure) entity.getMemory().get(GPBaseMegastructure.memKey);
            renderAtCenter(aotd_pluto_general_glow,1f);
            if(megastructure.getLaserSection().isRestored){
                renderAtCenter(aotd_pluto_laser_glow, magnitudeEffect);
                renderAtCenter(aotd_pluto_laserflare_glow, magnitudeEffect);
            }



        }



    }

    private void adjustAlpha(float percent,float step) {
        // Ensure percent is clamped between 0 and 1
        percent = Math.max(0f, Math.min(1f, percent));

        if (magnitudeEffect < percent) {
            // Increase currentAlpha toward percent
            magnitudeEffect = Math.min(magnitudeEffect + step, percent);
        } else if (magnitudeEffect > percent) {
            // Decrease currentAlpha toward percent
            magnitudeEffect = Math.max(magnitudeEffect - step, percent);
        }
    }
    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(entity.getMemory().contains(GPBaseMegastructure.memKey)){
            PlutoMegastructure megastructure = (PlutoMegastructure) entity.getMemory().get(GPBaseMegastructure.memKey);
            lastMagnitude = megastructure.getLaserSection().getCurrentMagnitude();
            float percent = lastMagnitude/ OpticCommandNexus.maxMagnitude;
            adjustAlpha(percent,amount*0.5f);
        }

    }

    public void renderAtCenter(SpriteAPI sprite, float alphaMult){
        sprite.setAlphaMult(alphaMult);
        sprite.setAngle(this.entity.getCircularOrbitAngle() - 90);
        sprite.renderAtCenter(this.entity.getLocation().x, this.entity.getLocation().y);
    }
}
