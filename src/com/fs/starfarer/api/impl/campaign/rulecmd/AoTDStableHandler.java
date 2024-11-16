package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.HypershuntReciverEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.Objectives;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.*;
import java.util.List;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

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
            if(arg.equals("aotd_hypershunt_reciver")){
                dialog.getOptionPanel().clearOptions();
                dialog.getOptionPanel().addOption("Proceed","SL_aotd_hypershunt_construct",Color.ORANGE,null);
                dialog.getOptionPanel().addOption("Nevermind","SL_cancelBuild");
                dialog.getTextPanel().addPara(Global.getSettings().getDescription("aotd_hypershunt_reciver", Description.Type.CUSTOM).getText1());
               TooltipMakerAPI tooltipMakerAPI =  dialog.getTextPanel().beginTooltip();
               tooltipMakerAPI.addTitle("Resources: consumed (available)");
               tooltipMakerAPI.addCustom(createResourcePanelForSmallTooltip(550,45,45,getCost(arg),false),5f);

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
            if(arg.equals("aotd_hypershunt_reciver")){
                dialog.getOptionPanel().clearOptions();
                dialog.getOptionPanel().addOption("Proceed","SL_aotd_hypershunt_deconstruct",Color.ORANGE,null);
                dialog.getOptionPanel().addOption("Nevermind","defaultLeave");
                TooltipMakerAPI tooltipMakerAPI =  dialog.getTextPanel().beginTooltip();
                tooltipMakerAPI.addTitle("Potential Salvage");
                tooltipMakerAPI.addCustom(createResourcePanelForSmallTooltip(550,45,45,getCostForSalvage(arg),true),5f);
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
            HypershuntReciverEntityPlugin plugin = (HypershuntReciverEntityPlugin) entity.getCustomPlugin();

            dialog.getOptionPanel().addOption("Break it for salvage","AoTD_Mega_Salvage");
            dialog.getOptionPanel().addOption("Leave","defaultLeave");
            dialog.getOptionPanel().setShortcut("defaultLeave",Keyboard.KEY_ESCAPE,false,false,false,false);
        }
        if(command.contains("salvageComplete")){
            CargoAPI salvage = Global.getFactory().createCargo(true);
            for (Map.Entry<String, Integer> entry : getCostForSalvage("aotd_hypershunt_reciver").entrySet()) {
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
        if(idOfStructure.equals("aotd_hypershunt_reciver")){
            costs.put(AoTDCommodities.REFINED_METAL,1000);
            costs.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY,100);
            costs.put(Items.CORONAL_PORTAL,1);
        }
        return costs;
    }
    public LinkedHashMap<String,Integer>getCostForSalvage(String idOfStructure){
        LinkedHashMap<String,Integer>costs = new LinkedHashMap<>();
        if(idOfStructure.equals("aotd_hypershunt_reciver")){
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
    public static CustomPanelAPI createResourcePanelForSmallTooltip(float width, float height, float iconSize, HashMap<String,Integer> costs,boolean isForSalvage) {
        CustomPanelAPI customPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = customPanel.createUIElement(width, height, false);
        float totalSize = width;
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = iconSize;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);


        float x = positions;
        ArrayList<CustomPanelAPI> panelsWithImage = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : costs.entrySet()) {
            float widthTempPanel = iconsize;
            int number = entry.getValue();
            int owned = (int) AoTDMisc.retrieveAmountOfItemsFromPlayer(entry.getKey());
            String icon = null;
            if(Global.getSettings().getSpecialItemSpec(entry.getKey())!=null){
                icon =Global.getSettings().getSpecialItemSpec(entry.getKey()).getIconName();
            }
            else{
                icon = Global.getSettings().getCommoditySpec(entry.getKey()).getIconName();
            }

            String text = "" +number;
            String text2 = "("+owned+")";
            if(isForSalvage){
                text2="";
            }
            widthTempPanel+=test.computeTextWidth(text+text2);
            CustomPanelAPI panelTemp = Global.getSettings().createCustom(widthTempPanel+iconSize+5,iconSize,null);
            TooltipMakerAPI tooltipMakerAPI = panelTemp.createUIElement(widthTempPanel+iconSize+5,iconSize,false);
            tooltipMakerAPI.addImage(icon, iconsize, iconsize, 0f);
            UIComponentAPI image = tooltipMakerAPI.getPrev();
            image.getPosition().inTL(x, topYImage);

            Color col = Misc.getTooltipTitleAndLightHighlightColor();
            if(number>owned&&!isForSalvage){
                col = Misc.getNegativeHighlightColor();
            }

            tooltipMakerAPI.addPara("%s %s", 0f, col, col, text,text2).getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
            panelTemp.addUIElement(tooltipMakerAPI).inTL(0, 0);
            panelsWithImage.add(panelTemp);
        }


        float totalWidth =0f;
        float secondRowWidth = 0f;
        float left;
        for (CustomPanelAPI panelAPI : panelsWithImage) {
            totalWidth+=panelAPI.getPosition().getWidth()+15;
        }
        left = totalWidth;
        ArrayList<CustomPanelAPI> panelsSecondRow = new ArrayList<>();
        if(totalWidth>=width){
            for (int i = panelsWithImage.size()-1; i >=0 ; i--) {
                left-=panelsWithImage.get(i).getPosition().getWidth()-15;
                panelsSecondRow.add(panelsWithImage.get(i));
                if(left<width){
                    break;
                }
                panelsWithImage.remove(i);
            }
        }
        for (CustomPanelAPI panelAPI : panelsSecondRow) {
            secondRowWidth+=panelAPI.getPosition().getWidth()+15;
        }
        float startingXFirstRow = 0;
        float startingXSecondRow = 0;
        if(!panelsSecondRow.isEmpty()){
            tooltip.getPosition().setSize(width,height*2+5);
            customPanel.getPosition().setSize(width,height*2+5);
        }
        for (CustomPanelAPI panelAPI : panelsWithImage) {
            tooltip.addCustom(panelAPI,0f).getPosition().inTL(startingXFirstRow,0);
            startingXFirstRow+=panelAPI.getPosition().getWidth()+5;
        }
        for (CustomPanelAPI panelAPI : panelsSecondRow) {
            tooltip.addCustom(panelAPI,0f).getPosition().inTL(startingXSecondRow,iconSize+5);
            startingXSecondRow+=panelAPI.getPosition().getWidth()+5;
        }

        customPanel.addUIElement(tooltip).inTL(0, 0);
        return customPanel;
    }
}
