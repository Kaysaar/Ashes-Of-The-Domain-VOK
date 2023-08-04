package com.fs.starfarer.api.impl.campaign.intel.bar.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeyondVeilIntel extends BaseIntelPlugin {

    public static int FINISHED_XP = 20000;
    public static int PAY_PILOT_XP = 5000;

    protected PlanetAPI planet;
    protected BeyondVeilBarEvent event;




    public BeyondVeilIntel(PlanetAPI planet, BeyondVeilBarEvent event) {
        this.planet = planet;
        this.event = event;
        Misc.makeImportant(planet, "veil");


        //cache.getMemoryWithoutUpdate().set("$saic_eventRef", this);
        //Global.getSector().addScript(this);



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
        //MemoryAPI memory = planet.getMemoryWithoutUpdate();

        return true;
        }


    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color tc = Misc.getTextColor();
        float pad = 3f;
        float opad = 10f;

        info.addPara("Data presented on Tri-pad of an old veteran points to "+planet.getName()+" located in "+ planet.getStarSystem().getName()+" Star system",10f);
        addBulletPoints(info, ListInfoMode.IN_DESC);


    }
    public String getSortString() {
        return "Beyond the Veil";
    }

    public String getName() {
        if (isEnded() || isEnding()) {
            return "Beyond the Veil - Finished";
        }
        return "Beyond the Veil";
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
        return Global.getSector().getMemory().contains("$aotd_veil_done");
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
        Misc.makeUnimportant(planet, "veil");


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
        Misc.makeUnimportant(planet, "veil");
        super.endAfterDelay();
    }

    @Override
    protected void notifyEnding() {
        super.notifyEnding();
    }
}
