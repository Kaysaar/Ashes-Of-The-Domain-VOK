package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Set;

public class PCFPlanetIntel extends BaseIntelPlugin {
    /*
    * Title: the main name of the intel on the left side of the intel menu
    * Subtitle: the smaller text below the title
    * Small description: the text to the right side of the intel tab, has a title as well
    * */

    // The name of the intel tab
    private static final String INTEL_PLANET = "Pre Collapse Facilities";
    private final PlanetAPI planet;

    private boolean deleteIntel = false;

    // Don't touch this one
    public PCFPlanetIntel(PlanetAPI planet) {
        this.planet = planet;
    }
    public static boolean doesContainPlanet(PlanetAPI planet){
        for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(PCFPlanetIntel.class)) {
            if(intelInfoPlugin instanceof PCFPlanetIntel intel){
                if(intel.getPlanet().equals(planet)){
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color title = getTitleColor(mode);

        // Title of the intel
        info.addPara(getName(), title, 0f);

        float initPad;
        if (mode == ListInfoMode.IN_DESC) {
            initPad = 10f;
        } else {
            initPad = 3f;
        }

        // Subtitle of the intel
        bullet(info);
        info.addPara(planet.getStarSystem().getName(), initPad, getBulletColorForMode(mode));
        unindent(info);
    }

    @Override
    protected String getName() {
        // The text in the title of the intel
        if(getPlanet().getMarket().getMemory().is("$aotd_fac_explored",true)){
            return "Pre-Collapse Facility - "+planet.getName()+" : Plundered";
        }
        return "Pre-Collapse Facility - "+planet.getName();
    }

    @Override
    public String getSmallDescriptionTitle() {
        // The text in the title of the small description
        return planet.getName();
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        // Create the small description here, you can place images, multiple paragraphs, anything
        info.addPara("This planet might contain long lost ruins of the Domain's technological might.", Misc.getGrayColor(), 0);
        if (getName().contains("Plundered")) {
            addDeleteButton(info,width-10);
        }
    }

    // 3 Methods regarding intel deletion upon button press
    @Override
    protected void notifyEnded() {
        if (getName().contains("Plundered")) {
            Global.getSector().getIntelManager().removeIntel(this);
        }
    }
    @Override
    protected void addDeleteButton(TooltipMakerAPI info, float width) {
        addDeleteButton(info, width, "Delete Pre-Collapse Facility entry");
    }
    @Override
    protected void createDeleteConfirmationPrompt(TooltipMakerAPI prompt) {
        prompt.addPara("Are you sure you want to permanently delete this Pre-Collapse Facility log entry?", Misc.getTextColor(), 0f);
    }

    @Override
    public String getIcon() {
        // The icon of the intel
        return Global.getSettings().getSpriteName("intel", "hostilities");
    }

    // Don't touch this one
    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(INTEL_PLANET);

        return tags;
    }

    // Color of the intel text
    @Override
    public FactionAPI getFactionForUIColors() {
        return super.getFactionForUIColors();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        // The location on the map of the intel
        return getPlanet();
    }

    @Override
    public boolean shouldRemoveIntel() {
        // The condition which will remove the intel
        return planet == null || !planet.isAlive() || deleteIntel;
    }

    @Override
    public String getCommMessageSound() {
        // Sound when the intel pops up
        return "ui_discovered_entity";
    }

    @Override
    public IntelSortTier getSortTier() {
        // Intel tab sort tier (don't really understand this one either tbh)
        return IntelSortTier.TIER_6;
    }

    // Don't touch this one
    public PlanetAPI getPlanet() {
        return planet;
    }
}
