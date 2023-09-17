package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PreCollapseFacIntel extends BaseIntelPlugin {

    protected PlanetAPI planet;
    protected PreCollapseFacBarEvent event;


    public PreCollapseFacIntel(PlanetAPI planet, PreCollapseFacBarEvent event) {
        this.planet = planet;
        this.event = event;
        Misc.makeImportant(planet, planet.getName()+"aotd_precollapse");

    }
    @Override
    public boolean isImportant() {
        return true;
    }

    @Override
    public boolean callEvent(String ruleId, InteractionDialogAPI dialog,
                             List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String action = params.get(0).getString(memoryMap);

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        CargoAPI cargo = playerFleet.getCargo();


        return true;
    }
    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color tc = Misc.getTextColor();
        float pad = 3f;
        float opad = 10f;

        info.addPara("Data presented on this old Tripad points to planet "+planet.getName()+" located in "+ planet.getStarSystem().getName()+" Star system",10f);
        addBulletPoints(info, ListInfoMode.IN_DESC);


    }
    public String getSortString() {
        return "Pre Collapse Facility - "+planet.getName();
    }

    public String getName() {

        return "Pre Collapse Facility - "+planet.getName();
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
        return planet;
    }

    @Override
    public boolean shouldRemoveIntel() {
        if(planet.getMarket().getMemory().is("$isSurveyed",true)){
            notifyEnded();
        }
        return planet.getMarket().getMemory().is("$isSurveyed",true);
        //return false;
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
        Misc.makeUnimportant(planet, planet.getName()+"aotd_precollapse");

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


    public void endAfterDelay() {
        Misc.makeUnimportant(planet, planet.getName()+"aotd_precollapse");
        super.endAfterDelay();
    }

    @Override
    protected void notifyEnding() {
        super.notifyEnding();
    }
}
