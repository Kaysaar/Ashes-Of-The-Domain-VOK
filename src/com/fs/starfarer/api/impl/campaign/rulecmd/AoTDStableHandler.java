package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.aotd_entities.HypershuntReceiverEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.*;
import java.util.List;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.createTooltipOfResourcesForDialog;

public class AoTDStableHandler extends BaseCommandPlugin {
    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    protected FactionAPI faction;


    protected void init(SectorEntityToken entity) {
        memory = entity.getMemoryWithoutUpdate();
        this.entity = entity;
        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        faction = entity.getFaction();


//		DebugFlags.OBJECTIVES_DEBUG = false;
//		DebugFlags.OBJECTIVES_DEBUG = true;
    }
    @Override
    public boolean execute(String ruleId, final InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        entity = dialog.getInteractionTarget();
        init(entity);

        memory = getEntityMemory(memoryMap);

        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();
        if(command.contains("wantToConstruct")){
            String arg = command.split(":")[1];
            if(arg.equals("aotd_hypershunt_receiver")){
                dialog.getOptionPanel().clearOptions();
                dialog.getOptionPanel().addOption("Proceed","SL_aotd_hypershunt_construct",Color.ORANGE,null);
                dialog.getOptionPanel().addOption("Nevermind","SL_cancelBuild");
                dialog.getTextPanel().addPara(Global.getSettings().getDescription("aotd_hypershunt_receiver", Description.Type.CUSTOM).getText1());
               TooltipMakerAPI tooltipMakerAPI =  dialog.getTextPanel().beginTooltip();
               tooltipMakerAPI.addTitle("Resources: consumed (available)");
               tooltipMakerAPI.addCustom(createTooltipOfResourcesForDialog(550,45,45,getCost(arg),false),5f);

                for (Map.Entry<String, Integer> entry : getCost(arg).entrySet()) {
                    if(AoTDMisc.retrieveAmountOfItemsFromPlayer(entry.getKey())<entry.getValue()){
                        dialog.getOptionPanel().setEnabled("SL_aotd_hypershunt_construct",false);
                        break;
                    }
                }
               dialog.getTextPanel().addTooltip();

            }


        }
        if(command.contains("salvageExplain")){
            String arg = command.split(":")[1];
            if(arg.equals("aotd_hypershunt_receiver")){
                dialog.getOptionPanel().clearOptions();
                dialog.getOptionPanel().addOption("Proceed","SL_aotd_hypershunt_deconstruct",Color.ORANGE,null);
                dialog.getOptionPanel().addOption("Nevermind","defaultLeave");
                TooltipMakerAPI tooltipMakerAPI =  dialog.getTextPanel().beginTooltip();
                tooltipMakerAPI.addTitle("Potential Salvage");
                tooltipMakerAPI.addCustom(createTooltipOfResourcesForDialog(550,45,45,getCostForSalvage(arg),true),5f);
                dialog.getTextPanel().addTooltip();

            }


        }
        if(command.contains("construct")){
            String arg = command.split(":")[1];
            for (Map.Entry<String, Integer> entry : getCost(arg).entrySet()) {
                AoTDMisc.eatPlayerItems(entry.getKey(),entry.getValue());
            }
            build(arg, Factions.PLAYER);


        }
        if(command.contains("salvageStart")){
            dialog.getOptionPanel().clearOptions();
            HypershuntReceiverEntityPlugin plugin = (HypershuntReceiverEntityPlugin) entity.getCustomPlugin();

            dialog.getOptionPanel().addOption("Break it for salvage","AoTD_Mega_Salvage");
            dialog.getOptionPanel().addOption("Leave","defaultLeave");
            dialog.getOptionPanel().setShortcut("defaultLeave",Keyboard.KEY_ESCAPE,false,false,false,false);
        }
        if(command.contains("salvageComplete")){
            CargoAPI salvage = Global.getFactory().createCargo(true);
            for (Map.Entry<String, Integer> entry : getCostForSalvage("aotd_hypershunt_receiver").entrySet()) {
                if(Global.getSettings().getSpecialItemSpec(entry.getKey())==null){
                    salvage.addCommodity(entry.getKey(),entry.getValue());
                }
                else{
                    salvage.addSpecial(new SpecialItemData(entry.getKey(),null),entry.getValue());
                }

            }
            dialog.getVisualPanel().showLoot("Salvaged", salvage, false, true, true, new CoreInteractionListener() {
                public void coreUIDismissed() {
                    dialog.dismiss();
                    dialog.hideTextPanel();
                    dialog.hideVisualPanel();
                    LocationAPI loc = entity.getContainingLocation();
                    SectorEntityToken built = loc.addCustomEntity(null,
                            null,
                            Entities.STABLE_LOCATION, // type of object, defined in custom_entities.json
                            Factions.NEUTRAL); // faction
                    if (entity.getOrbit() != null) {
                        built.setOrbit(entity.getOrbit().makeCopy());
                    }
                    loc.removeEntity(entity);
                    updateOrbitingEntities(loc, entity, built);

                    built.getMemoryWithoutUpdate().set(MemFlags.RECENTLY_SALVAGED, true, 30f);

                    ListenerUtil.reportObjectiveDestroyed(entity, built, Global.getSector().getFaction(Factions.PLAYER));
                }
            });
            options.clearOptions();
            dialog.setPromptText("");
        }
        return true;
    }
    public LinkedHashMap<String,Integer>getCost(String idOfStructure){
        LinkedHashMap<String,Integer>costs = new LinkedHashMap<>();
        if(idOfStructure.equals("aotd_hypershunt_receiver")){
            costs.put(AoTDCommodities.REFINED_METAL,1000);
            costs.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY,100);
            costs.put(Items.CORONAL_PORTAL,1);
        }
        return costs;
    }
    public LinkedHashMap<String,Integer>getCostForSalvage(String idOfStructure){
        LinkedHashMap<String,Integer>costs = new LinkedHashMap<>();
        if(idOfStructure.equals("aotd_hypershunt_receiver")){
            costs.put(AoTDCommodities.REFINED_METAL,700);
            costs.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY,50);
            costs.put(Items.CORONAL_PORTAL,1);
        }
        return costs;
    }
    public void build(String type, String factionId) {
        if (entity.hasTag(Tags.NON_CLICKABLE)) return;
        if (entity.hasTag(Tags.FADING_OUT_AND_EXPIRING)) return;

        LocationAPI loc = entity.getContainingLocation();
        SectorEntityToken built = loc.addCustomEntity(null,
                null,
                type, // type of object, defined in custom_entities.json
                factionId); // faction
        if (entity.getOrbit() != null) {
            built.setOrbit(entity.getOrbit().makeCopy());
        }
        built.setLocation(entity.getLocation().x, entity.getLocation().y);
        loc.removeEntity(entity);
        updateOrbitingEntities(loc, entity, built);

        //entity.setContainingLocation(null);
        built.getMemoryWithoutUpdate().set("$originalStableLocation", entity);

        if (text != null) {
            Global.getSoundPlayer().playUISound("ui_objective_constructed", 1f, 1f);

        }
        dialog.dismiss();
    }
    public void updateOrbitingEntities(LocationAPI loc, SectorEntityToken prev, SectorEntityToken built) {
        if (loc == null) return;
        for (SectorEntityToken other : loc.getAllEntities()) {
            if (other == prev) continue;
            if (other.getOrbit() == null) continue;
            if (other.getOrbitFocus() == prev) {
                other.setOrbitFocus(built);
            }
        }
    }
    public void printCost(){

    }

}
