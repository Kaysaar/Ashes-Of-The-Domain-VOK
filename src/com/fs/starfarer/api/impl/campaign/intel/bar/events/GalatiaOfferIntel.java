package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Set;

public class GalatiaOfferIntel extends BaseIntelPlugin {

    public static int FINISHED_XP = 20000;
    public static int PAY_PILOT_XP = 5000;

    protected PlanetAPI planet;
    public boolean founded = false;




    public GalatiaOfferIntel(PlanetAPI planet) {
        this.planet = planet;
        Misc.makeImportant(planet, "galatia");


        //cache.getMemoryWithoutUpdate().set("$saic_eventRef", this);
        //Global.getSector().addScript(this);



    }

    @Override
    public boolean isImportant() {
        return true;
    }
    @Override
    public boolean shouldRemoveIntel() {
        return Global.getSector().getMemory().is("$aotd_galatia_done",true);
        //return false;
    }
    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color tc = Misc.getTextColor();
        float pad = 3f;
        float opad = 10f;

        info.addPara("Data presented on Tri-pad of an scientist points to " + planet.getName() + " located in " + planet.getStarSystem().getName() + " Star system", 10f);
        addBulletPoints(info, ListInfoMode.IN_DESC);
    }
    public String getSortString() {
        return "Ouroboros";
    }

    public String getName() {
        if (isEnded() || isEnding()) {
            return "Ouroboros - Finished";
        }
        return "Ouroboros";
    }
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "red_planet");
    }

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        super.createLargeDescription(panel, width, height);
    }
    @Override
    public FactionAPI getFactionForUIColors() {
        return super.getFactionForUIColors();
    }

    public String getSmallDescriptionTitle() {
        return getName();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        if(founded&&Global.getSector().getStarSystem("Galatia")!=null){
            return Global.getSector().getStarSystem("Galatia").getEntityById("station_galatia_academy");
        }
        return planet;

    }
    @Override
    public String getCommMessageSound() {
        return "ui_discovered_entity";
    }
    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        info.addPara(getName(), c, 0f);
        addBulletPoints(info, mode);
    }

    @Override
    protected void notifyEnded() {
        super.notifyEnded();
        Global.getSector().removeScript(this);


    }
    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_MISSIONS);
        tags.add(Tags.INTEL_ACCEPTED);
        tags.add(Tags.INTEL_EXPLORATION);
        return tags;
    }
    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode) {

        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        float pad = 3f;
        float opad = 10f;

        float initPad = pad;
        if (mode == ListInfoMode.IN_DESC) initPad = opad;

        Color tc = getBulletColorForMode(mode);

        bullet(info);
        boolean isUpdate = getListInfoParam() != null;

        initPad = 0f;

        unindent(info);
    }
}
