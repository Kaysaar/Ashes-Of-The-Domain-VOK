package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.scripts.coreui.CoreUITracker;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

import java.awt.*;

public class SpecialProjectFinishedIntel extends BaseIntelPlugin {
    public static Object Button_SHIP = new Object();
    public AoTDSpecialProject specialProject;

    public SpecialProjectFinishedIntel(AoTDSpecialProject specialProject) {
        this.specialProject = specialProject;
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        // The location on the map of the intel
        return Global.getSector().getPlayerFaction().getProduction().getGatheringPoint().getPrimaryEntity();
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color title = getTitleColor(mode);

        // Title of the intel
        info.addPara(getName(), title, 0f);
        info.addPara("Project : %s is finished",5f,Color.ORANGE,specialProject.getNameOverride());

    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("After investing huge amount of resources we have finally managed to finish this project", 5f);
        specialProject.createRewardSectionForInfo(info,width);
        addGenericButton(info,width,"Access Special Projects",Button_SHIP);
    }

    @Override
    public boolean shouldRemoveIntel() {
        return getDaysSincePlayerVisible()>5;
    }
    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "sp_unlock_finish");
    }

    @Override
    protected String getName() {
        return "Project : "+specialProject.getNameOverride();
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return super.doesButtonHaveConfirmDialog(buttonId);
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if(buttonId==Button_SHIP){
            CoreUITracker.setMemFlag(CoreUITracker.getStringForCoreTabResearch());
            CoreUITracker.setMemFlagForTechTab("black site projects");
            Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.OUTPOSTS);
        }

    }
}
