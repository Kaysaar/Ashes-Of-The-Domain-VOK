package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.impl.campaign.BiFrostGateEntity;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BiFrostCMD extends BaseCommandPlugin {
    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {


        this.dialog = dialog;
        this.memoryMap = memoryMap;

        memory = getEntityMemory(memoryMap);

        entity = dialog.getInteractionTarget();
        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        if (command.equals("selectDestination")) {
            selectDestination();
        }
        return true;
    }

    ArrayList<SectorEntityToken>getAllBifrostGates(){
        ArrayList<SectorEntityToken> toReturn = new ArrayList<>();
        for (MarketAPI factionMarket : Misc.getFactionMarkets(Global.getSector().getPlayerFaction())) {
            for (SectorEntityToken connectedEntity : factionMarket.getConnectedEntities()) {
                if(connectedEntity.hasTag("bifrost")){
                    toReturn.add(connectedEntity);
                }
            }
        }
        if(toReturn.isEmpty()){
            return null;
        }
        return toReturn;
    }

    protected void selectDestination() {
        final ArrayList<SectorEntityToken> gates =
                new ArrayList<>(getAllBifrostGates());
        gates.remove(entity);
        dialog.showCampaignEntityPicker("Select destination", "Destination:", "Initiate transit",
                Global.getSector().getPlayerFaction(), gates,
                new BaseCampaignEntityPickerListener() {
                    public void pickedEntity(SectorEntityToken entityToTravel) {
                        dialog.dismiss();
                        entity.getMemoryWithoutUpdate().set("$used",true);
                        entityToTravel.getMemoryWithoutUpdate().set("$used",true);
                        entity.getMemoryWithoutUpdate().set("$cooldown",30f);
                        entityToTravel.getMemoryWithoutUpdate().set("$cooldown",30f);
                        Global.getSector().setPaused(false);
                        JumpPointAPI.JumpDestination dest = new JumpPointAPI.JumpDestination(entityToTravel, null);
                        Global.getSector().doHyperspaceTransition(playerFleet, entity, dest, 2f);

                        float distLY = Misc.getDistanceLY(entityToTravel, entity);
                        if (entity.getCustomPlugin() instanceof BiFrostGateEntity) {
                            BiFrostGateEntity plugin = (BiFrostGateEntity) entity.getCustomPlugin();
                            plugin.showBeingUsed(distLY);
                        }
                        if (entity.getCustomPlugin() instanceof BiFrostGateEntity) {
                            BiFrostGateEntity plugin = (BiFrostGateEntity) entity.getCustomPlugin();
                            plugin.showBeingUsed(distLY);
                        }

                        ListenerUtil.reportFleetTransitingGate(Global.getSector().getPlayerFleet(),
                                entity, entityToTravel);
                    }

                    public void cancelledEntityPicking() {

                    }

                    public String getMenuItemNameOverrideFor(SectorEntityToken entity) {
                        return null;
                    }

                    public String getSelectedTextOverrideFor(SectorEntityToken entity) {
                        return entity.getName() + " - " + entity.getContainingLocation().getNameWithTypeShort();
                    }

                    public void createInfoText(TooltipMakerAPI info, SectorEntityToken entity) {

                        int available = (int) Global.getSector().getPlayerFleet().getCargo().getFuel();

                        Color reqColor = Misc.getHighlightColor();
                        Color availableColor = Misc.getHighlightColor();

                        info.setParaSmallInsignia();
//					LabelAPI label = info.addPara("Transit requires %s fuel. "
//							+ "You have %s units of fuel available.", 0f,
//							Misc.getTextColor(),
//							//Misc.getGrayColor(),
//							availColor, Misc.getWithDGS(cost), Misc.getWithDGS(available));
//					label.setHighlightColors(reqColor, availColor);

                        ;
                    }

                    public boolean canConfirmSelection(SectorEntityToken entity) {
                        return true;
                    }

                    public float getFuelColorAlphaMult() {
                        return 0.5f;
                    }

                    public float getFuelRangeMult() { // just for showing it on the map when picking destination
                        if (true) return 0f;
                        if (Misc.GATE_FUEL_COST_MULT <= 0) return 0f;
                        return 1f / Misc.GATE_FUEL_COST_MULT;
                    }
                });
    }
}
