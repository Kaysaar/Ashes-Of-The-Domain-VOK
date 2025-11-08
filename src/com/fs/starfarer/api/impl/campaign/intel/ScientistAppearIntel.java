package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.scripts.managers.AoTDFactionManager;

import java.awt.*;

public class ScientistAppearIntel extends BaseIntelPlugin{
    public PersonAPI person;
    public ScientistAppearIntel(PersonAPI person){
        this.person = person;

        getMapLocation(null).getMarket().getCommDirectory().addPerson(person);
    }
    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        // The location on the map of the intel
        if(Global.getSettings().getModManager().isModEnabled("aotd_sop")){
            try {
                AoTDFactionManager.getInstance().getCapitalMarket().getPrimaryEntity();
            }
            catch(Exception e){

            }
        }
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
        info.addPara("You have received an unusual transmission.", 5f);


    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("A transmission has been received by our long range communications. Someone wants to contact you, currently this person is residing in "+getMapLocation(null).getName()+".", 5f);
    }



    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "sp_unlock_finish");
    }

    @Override
    protected String getName() {
        return "Incoming Transmission";
    }
}
