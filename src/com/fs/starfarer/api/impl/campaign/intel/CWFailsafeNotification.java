package com.fs.starfarer.api.impl.campaign.intel;

import ashlib.data.plugins.coreui.CommandTabMemoryManager;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;

import java.awt.*;

public class CWFailsafeNotification extends BaseMegastuctureIntelPlugin {
    public static Object Button_Megastructure = new Object();

    public CWFailsafeNotification(GPBaseMegastructure unlockedProj) {
        super(unlockedProj);
    }


    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) { // Redundant, same as super, maybe remove - Purple Nebula
        return data.getEntityTiedTo();   // The location on the map of the intel
    }


    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color title = getTitleColor(mode);

        // Title of the intel
        info.addPara(getName(), title, 0f);
        info.addPara("Critical failure of Wormhole Stabilizer", 5f);
        info.addPara("Reduced range of hypershunt to %s in %s", 5f,Color.ORANGE,"10 LY",data.getEntityTiedTo().getStarSystem().getBaseName());

    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("Reduced range of hypershunt to %s in %s", 5f,Color.ORANGE,"10 LY",data.getEntityTiedTo().getStarSystem().getBaseName());
        info.addPara("Increase global production of %s so range can be increased again!",5f,Color.ORANGE,"Purified Transplutonics");

    }


    @Override
    public String getIcon() {
        return data.getIcon();
    } // Redundant, same as super, maybe remove - Purple Nebula


    @Override
    protected String getName() { // Redundant, same as super, maybe remove - Purple Nebula
        return "Megastructure : " + data.getName();
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return super.doesButtonHaveConfirmDialog(buttonId);
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (buttonId == Button_Megastructure) {
            CommandTabMemoryManager.getInstance().setLastCheckedTab("research & production");
            CommandTabMemoryManager.getInstance().getTabStates().put("research & production","megastructures");
            Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.OUTPOSTS);
        }

    }
}