package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.scripts.CoreUITracker;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProjectStage;

import java.awt.*;

public class ProjectStageCompletionIntel extends BaseIntelPlugin {
    public static Object Button_SHIP = new Object();
    public AoTDSpecialProject specialProject;
    public AoTDSpecialProjectStage stage;
    public ProjectStageCompletionIntel(AoTDSpecialProject specialProject,AoTDSpecialProjectStage stage) {
        this.specialProject = specialProject;
        this.stage = stage;
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        // The location on the map of the intel
        return Global.getSector().getPlayerFaction().getProduction().getGatheringPoint().getPrimaryEntity();
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, IntelInfoPlugin.ListInfoMode mode) {
        Color title = getTitleColor(mode);

        // Title of the intel
        info.addPara(getName(), title, 0f);
        info.addPara("Project : %s",5f,Color.ORANGE,specialProject.getNameOverride());
        info.addPara("Stage : %s finished",5f,Color.ORANGE,stage.getSpec().getName());

    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("We have managed to complete %s stage of %s", 5f,Color.ORANGE,stage.getSpec().getName(),specialProject.getNameOverride());
        addGenericButton(info,width,"Access Special Projects",Button_SHIP);
    }


    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "sp_stage_finish");
    }

    @Override
    protected String getName() {
        return "Stage : "+stage.getSpec().getName();
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return super.doesButtonHaveConfirmDialog(buttonId);
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if(buttonId==Button_SHIP){
            CoreUITracker.setMemFlag(CoreUITracker.getStringForCoreTabResearch());
            CoreUITracker.setMemFlagForTechTab("special projects");
            Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.OUTPOSTS);
        }

    }
}
