package com.fs.starfarer.api.impl.campaign.aotd_entities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin;

public class PlutoMiningStation extends BaseCustomEntityPlugin {
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
        renderAtCenter(aotd_pluto_general_glow);
        renderAtCenter(aotd_pluto_laser_glow);
        renderAtCenter(aotd_pluto_laserflare_glow);
    }
    public void renderAtCenter(SpriteAPI sprite){
        sprite.setAngle(this.entity.getCircularOrbitAngle() - 90);
        sprite.renderAtCenter(this.entity.getLocation().x, this.entity.getLocation().y);
    }
}
