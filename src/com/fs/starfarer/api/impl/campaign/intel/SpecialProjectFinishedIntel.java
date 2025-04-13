package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class SpecialProjectFinishedIntel extends BaseIntelPlugin{
    public static Object Button_SHIP = new Object();
    public SpecialProjectFinishedIntel(){

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
        info.addPara(getName(), title,0f);
//        info.addPara("Project : %s is finished",5f,Color.ORANGE,data.getSpec().getNameOverride());

    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("After investing huge amount of resources we have finally managed to finish this project",5f);
//        info.addPara("Project result : 1x %s",5f, Misc.getTooltipTitleAndLightHighlightColor(),Global.getSettings().getHullSpec(data.getSpec().getRewardId()).getHullName());
//        info.addPara("Vessel is located in %s on %s",5f,Color.ORANGE,"Local storage",Global.getSector().getPlayerFaction().getProduction().getGatheringPoint().getName());
    }



    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "respite40");
    }

    @Override
    protected String getName() {
        return "Project : ";
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return super.doesButtonHaveConfirmDialog(buttonId);
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if(buttonId==Button_SHIP){

        }

    }
}
