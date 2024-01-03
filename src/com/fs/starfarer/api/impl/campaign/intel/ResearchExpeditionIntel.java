package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Set;

public class ResearchExpeditionIntel extends BaseIntelPlugin {
    private static final String INTEL_PLANET = "Research Expeditions";
    private final FactionAPI factionAPI;
    public String idOfIntel;
    public float counter;
    public MarketAPI launchMarket;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean success = false;
    public boolean failed = false;
    public boolean finished = false;


    public ResearchExpeditionIntel(FactionAPI factionAPI, float timeToExpedition) {
        this.factionAPI = factionAPI;
        idOfIntel = "research_" + factionAPI.getId();
        counter = timeToExpedition;

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
        unindent(info);
    }

    @Override
    protected String getName() {
        // The text in the title of the intel
        String expedition = "Research Expedition";
        if (factionAPI.getId().contains("luddic")) {
            expedition = "Cleansing";
        }
        return expedition+" - " + factionAPI.getDisplayName();

    }

    public void setLaunchMarket(MarketAPI launchMarket) {
        this.launchMarket = launchMarket;
    }

    @Override
    public String getSmallDescriptionTitle() {
        // The text in the title of the small description
        String expedition = "Research Expedition";
        if (factionAPI.getId().contains("luddic")) {
            expedition = "Cleansing";
        }
        if (counter >= 0) {
            return expedition + " - Preparations";
        }
        if (!finished) {
            return expedition+" : Launched";
        }
        if (!finished) {
            if(failed){
                return expedition+" : Failed";
            }
            else{
                return expedition+" : On-going";
            }

        }
        else{
            if (success) {
                return expedition+" : Successful";
            }
            if(!failed){
                return expedition+" : Returning from expedition";
            }
            if (failed) {
                return expedition+" : Failed";
            }
        }

        return "";

    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        // Create the small description here, you can place images, multiple paragraphs, anything
        info.addImage(factionAPI.getLogo(), width, 96, 5);
        if (counter >= 0) {
            info.addPara("Strange activity has been recently seen in " + factionAPI.getDisplayName() + ". Their exploratory fleets are beginning to be mobilized.From what intel we can gather, it all indicates that massive exploration fleet is being prepared and it is ", 10);
            info.addPara((int) counter + " days till expedition will embark", 10f);
        } else {
            if (launchMarket != null) {
                info.addPara("Given current intel, expedition has been launched from " + launchMarket.getName(), 10);
            }
            if(!finished){
                if(!success&&failed){
                    info.addPara("Expedition has failed",10f);
                }
            }
            else{
                if(!success&&!failed){
                    assert launchMarket != null;
                    info.addPara("Expedition has been successful - currently it's returning to "+launchMarket.getName(),10f);
                }
            }
            if(success){
                info.addPara("Expedition has been successful and it boosted faction's technological capabilities",10f);
            }

        }

    }

    @Override
    public void advance(float amount) {
        if (counter >= 0) {
            counter -= Global.getSector().getClock().convertToDays(amount);
        }
        super.advance(amount);

    }


    @Override
    public String getIcon() {
        // The icon of the intel
        return Global.getSettings().getSpriteName("intel", "hostilities");
    }

    @Override
    public void createConfirmationPrompt(Object buttonId, TooltipMakerAPI prompt) {
        super.createConfirmationPrompt(buttonId, prompt);
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
        return null;
    }

    @Override
    public boolean shouldRemoveIntel() {
        // The condition which will remove the intel
        return super.shouldRemoveIntel();
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
}
