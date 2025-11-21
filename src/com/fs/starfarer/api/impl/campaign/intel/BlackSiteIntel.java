package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

public class BlackSiteIntel extends BaseIntelPlugin{
    public static Object Button_SHIP = new Object();
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
        info.addPara("Our scientists have achieved a major breakthrough — but its very nature risks drawing unwanted attention. A solution has been proposed for this problem...", 5f);


    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("Our scientists have achieved a major breakthrough — but its very nature risks drawing unwanted attention. R&D advises setting up a Black Site before pursuing this or any related endeavors.", 5f);
        info.addPara("Unlocked new upgrade for %s : %s",3f,new Color[]{Color.ORANGE,Global.getSector().getFaction(Factions.PIRATES).getBaseUIColor()},"Research Facility", "Black Site");
    }



    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "sp_unlock_finish");
    }

    @Override
    protected String getName() {
        return "Black Site Proposition";
    }

}
