package com.fs.starfarer.api.impl.campaign.intel;

import ashlib.data.plugins.coreui.CommandTabMemoryManager;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.awt.*;

public class MegastructureSectionCompletedIntel extends BaseIntelPlugin{
    public BaseMegastructureSection data;
    public static Object Button_Megastructure = new Object();
    public MegastructureSectionCompletedIntel(BaseMegastructureSection unlockedProj){
        this.data=unlockedProj;
    }
    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return  data.getMegastructureTiedTo().getEntityTiedTo();   // The location on the map of the intel
    }


    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color title = getTitleColor(mode);

        // Title of the intel
        info.addPara(getName(), title,0f);
        if(data.isBuildable()){
            info.addPara(data.getName()+" has been constructed!",5f);

        }
        else{
            info.addPara(data.getName()+" has been restored!",5f);

        }
        info.addPara("Received a story point!", Misc.getPositiveHighlightColor(),5f);

    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        if(data.isBuildable()){
            info.addPara("%s has been build",5f,Color.ORANGE,data.getName());

        }
        else{
            info.addPara("%s section : %s has been restored",5f,Color.ORANGE,data.getMegastructureTiedTo().getName(),data.getName());

        }
    }



    @Override
    public String getIcon() {
        return data.getMegastructureTiedTo().getIcon();
    }

    @Override
    protected String getName() {
        return data.getName()+" - completed";
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return super.doesButtonHaveConfirmDialog(buttonId);
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {

    }
}
