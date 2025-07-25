package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class AoTDPlutoEncounter extends BaseCommandPlugin{
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    protected StarSystemAPI starSystem;
    protected void init(SectorEntityToken entity) {
        memory = entity.getMemoryWithoutUpdate();
        this.entity = entity;
        playerFaction = Global.getSector().getPlayerFaction();
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

        memory = getEntityMemory(memoryMap);
        if(command.equals("start")){
            dialog.getTextPanel().addPara("You fleet is approaching the %s", Color.ORANGE,"Pluto Mining Station");
            dialog.getTextPanel().addPara(Global.getSettings().getDescription(entity.getCustomDescriptionId(), Description.Type.CUSTOM).getText1());

        }
        if(entity.getOrbitFocus() != null){
            GPBaseMegastructure megastructure;
            SectorEntityToken token = entity.getOrbitFocus();
            if (token.getMemory().contains(GPBaseMegastructure.memKey)) {
                megastructure = (GPBaseMegastructure) token.getMemory().get(GPBaseMegastructure.memKey);
                if(!megastructure.isClaimed()){
                    dialog.getTextPanel().addPara("To claim this megastructure we need to colonize "+token.getName());
                }
            }
        }
        return true;
    }
}
