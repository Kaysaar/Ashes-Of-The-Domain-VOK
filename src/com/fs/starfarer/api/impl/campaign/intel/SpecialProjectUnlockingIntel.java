package com.fs.starfarer.api.impl.campaign.intel;

import ashlib.data.plugins.coreui.CommandTabMemoryManager;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

import java.awt.*;

public class SpecialProjectUnlockingIntel extends BaseIntelPlugin{
    public AoTDSpecialProject project;
    public static Object Button_SHIP = new Object();
    public SpecialProjectUnlockingIntel(AoTDSpecialProject unlockedProj){
        this.project = unlockedProj;
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
    public boolean shouldRemoveIntel() {
        return getDaysSincePlayerVisible()>5;
    }
    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color title = getTitleColor(mode);

        // Title of the intel
        info.addPara(getName(), title,0f);
        info.addPara("Due to recent actions a new project has been unlocked, to be undertaken by our specialist at Black Site",5f);

    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("Due to recent actions a new project has been unlocked, to be undertaken by our specialists at Black Site",5f);
        info.addPara("New Project unlocked : %s",5f,Color.ORANGE,"Project:  "+project.getNameOverride());
        addGenericButton(info,width,"Access Black Site Projects",Button_SHIP);
    }



    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "sp_unlock_finish");
    }

    @Override
    protected String getName() {
        return "Project : " +project.getNameOverride();
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return super.doesButtonHaveConfirmDialog(buttonId);
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if(buttonId==Button_SHIP){
            CommandTabMemoryManager.getInstance().setLastCheckedTab("research & production");
            CommandTabMemoryManager.getInstance().getTabStates().put("research & production","black site projects");
            Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.OUTPOSTS);
        }

    }
}
