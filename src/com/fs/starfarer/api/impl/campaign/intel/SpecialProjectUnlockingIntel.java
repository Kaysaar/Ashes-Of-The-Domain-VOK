package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GpSpecialProjectData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelDP;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelDPSpecialProj;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.SpecialProjectButtonData;

import java.awt.*;

public class SpecialProjectUnlockingIntel extends BaseIntelPlugin{

    public GpSpecialProjectData data;
    public static Object Button_SHIP = new Object();
    public SpecialProjectUnlockingIntel(GpSpecialProjectData unlockedProj){
        this.data=unlockedProj;
    }
    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        // The location on the map of the intel
        try {
            return Global.getSector().getPlayerFaction().getProduction().getGatheringPoint().getPrimaryEntity();
        }
        catch (Exception  e ){

        }
    return Global.getSector().getPlayerFleet();
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color title = getTitleColor(mode);

        // Title of the intel
        info.addPara(getName(), title,0f);
        info.addPara("Due to recent actions a new project has been unlocked, to be undertaken by our shipyards",5f);

    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("Due to recent actions a new project has been unlocked, to be undertaken by our shipyards",5f);
        info.addPara("New Project unlocked : %s",5f,Color.ORANGE,"Project "+data.getSpec().getNameOverride());
        addGenericButton(info,width,"Access Shipyards",Button_SHIP);
    }



    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "respite40");
    }

    @Override
    protected String getName() {
        return "Project : "+data.getSpec().getNameOverride();
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return super.doesButtonHaveConfirmDialog(buttonId);
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if(buttonId==Button_SHIP){
            ui.showDialog(null,new NidavelirMainPanelDPSpecialProj());
        }

    }
}
