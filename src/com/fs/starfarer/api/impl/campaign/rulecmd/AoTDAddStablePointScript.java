package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.createTooltipOfResourcesForDialog;

public class AoTDAddStablePointScript extends  BaseCommandPlugin{
    public static String memKey = "$aotd_stable_point_janus";
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    protected StarSystemAPI starSystem;
    protected void init(SectorEntityToken entity) {
        memory = entity.getMemoryWithoutUpdate();
        this.entity = entity;
        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();
        starSystem = entity.getStarSystem();
    }
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        entity = dialog.getInteractionTarget();
        init(entity);
        if(entity.getMarket()==null)return false;
        memory = getEntityMemory(memoryMap);
        if(command.equals("checkIfValid")){
            AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayerFaction();
            return AoTDMisc.doesMarketBelongToFaction(Global.getSector().getPlayerFaction(), entity.getMarket()) && manager.haveResearched(AoTDTechIds.JANUS_DEVICE_ANALYSIS)&&!entity.getStarSystem().getMemoryWithoutUpdate().is(memKey,true)&&entity.getStarSystem().getEntitiesWithTag(Tags.STABLE_LOCATION).size()<=3;
        }
        if(command.equals("printReq")){
            TooltipMakerAPI tooltipMakerAPI = dialog.getTextPanel().beginTooltip();
            tooltipMakerAPI.addTitle("Resources: consumed (available)");
            tooltipMakerAPI.addCustom(createTooltipOfResourcesForDialog(550,45,45,getCostForStablePoint(),false),5f);
            for (Map.Entry<String, Integer> entry : getCostForStablePoint().entrySet()) {
                if(AoTDMisc.retrieveAmountOfItemsFromPlayer(entry.getKey())<entry.getValue()){
                    dialog.getOptionPanel().setEnabled("aotd_stable_point_create",false);
                    break;
                }
            }
            dialog.getTextPanel().addTooltip();
        }

        if(command.equals("addStable")){
            StarSystemGenerator.addStableLocations(entity.getStarSystem(), 1);
            for (Map.Entry<String, Integer> entry : getCostForStablePoint().entrySet()) {
                AoTDMisc.eatPlayerItems(entry.getKey(),entry.getValue());
            }
            Global.getSoundPlayer().playUISound("ui_objective_constructed", 1f, 1f);
            entity.getStarSystem().getMemoryWithoutUpdate().set(memKey,true);
        }
        return true;
    }
    public LinkedHashMap<String,Integer>getCostForStablePoint(){
        LinkedHashMap<String,Integer>costs = new LinkedHashMap<>();
        costs.put(Commodities.FUEL,500);
        costs.put(AoTDCommodities.ADVANCED_COMPONENTS,100);
        costs.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY,150);
        return costs;
    }
}
