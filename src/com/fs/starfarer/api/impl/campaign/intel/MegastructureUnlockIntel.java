package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GpSpecialProjectData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.scripts.CoreUITracker;

import java.awt.*;

public class MegastructureUnlockIntel extends BaseIntelPlugin{
    public GPBaseMegastructure data;
    public static Object Button_Megastructure = new Object();
    public MegastructureUnlockIntel(GPBaseMegastructure unlockedProj){
        this.data=unlockedProj;
    }
    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
     return  data.getEntityTiedTo();   // The location on the map of the intel
    }


    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color title = getTitleColor(mode);

        // Title of the intel
        info.addPara(getName(), title,0f);
        info.addPara("A new megastucture has been claimed by our faction!",5f);
        info.addPara("Received a story point!", Misc.getPositiveHighlightColor(),5f);

    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("New Megastructure  : %s",5f,Color.ORANGE,data.getName());
        addGenericButton(info,width,"Access Megastructure Tab", Button_Megastructure);
    }



    @Override
    public String getIcon() {
        return data.getIcon();
    }

    @Override
    protected String getName() {
        return "Megastructure : "+data.getName();
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return super.doesButtonHaveConfirmDialog(buttonId);
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if(buttonId== Button_Megastructure){
            CoreUITracker.setMemFlag("megastructures");
            Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.OUTPOSTS);
        }

    }

}
