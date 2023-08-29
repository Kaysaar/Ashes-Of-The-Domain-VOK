package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BeyondVeilIntel;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.GalatiaOfferIntel;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GalatiaOfferQuest extends BaseCommandPlugin {
    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
     protected PlanetAPI planetQuest;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected MarketAPI market;
    protected InteractionDialogAPI dialog;
    protected FactionAPI faction;

    protected OptionPanelAPI options;

    public GalatiaOfferQuest() {
    }

    public GalatiaOfferQuest(SectorEntityToken entity) {
        init(entity);
    }

    protected void init(SectorEntityToken entity) {
        memory = entity.getMemoryWithoutUpdate();
        this.entity = entity;

        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        faction = entity.getFaction();

        market = entity.getMarket();


    }

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if(dialog==null)return false;
        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;
        entity = dialog.getInteractionTarget();
        init(entity);
        this.dialog = dialog;
        planetQuest = (PlanetAPI) Global.getSector().getPersistentData().get("$aotd_galatia_planet");
        memory = getEntityMemory(memoryMap);

        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();
        if(command.contains("showPlanet")){
            showPlanet();
        }
        if(command.contains("rejectedQuest")){
            rejectedQuest();
        }
        if(command.contains("acceptedQuest")){
            acceptedQuest();
        }

        return true;
    }
    protected void showPlanet(){
        String icon = Global.getSettings().getSpriteName("intel", "red_planet");
        Set<String> tags = new LinkedHashSet<String>();
        tags.add(Tags.INTEL_MISSIONS);
        if(planetQuest!=null){
            dialog.getVisualPanel().showMapMarker(planetQuest.getStarSystem().getCenter(),
                    "Destination: " + planetQuest.getStarSystem().getName(), Misc.getBasePlayerColor(),
                    true, icon, null, tags);
        }
    }
    protected void acceptedQuest(){
        if(planetQuest!=null){
            addIntel();
        }
    }
    protected void rejectedQuest(){
        if(planetQuest!=null){
            planetQuest.getMemoryWithoutUpdate().unset("$aotd_galatia_planet");
            Global.getSector().getPersistentData().remove("$aotd_galatia_planet");
        }
    }
    protected void addIntel(){
        if (planetQuest != null) {
            GalatiaOfferIntel intel = new GalatiaOfferIntel(planetQuest);
            //intel.setImportant(true);
            Global.getSector().getIntelManager().addIntel(intel, false, text);
        }
    }
}
