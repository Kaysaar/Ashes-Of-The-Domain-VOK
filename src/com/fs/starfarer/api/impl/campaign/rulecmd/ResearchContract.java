package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDAIStance;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.attitude.FactionResearchAttitudeData;

import java.util.List;
import java.util.Map;

public class ResearchContract extends BaseCommandPlugin{
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
    protected PersonAPI person;
    protected FactionAPI faction;
    protected FactionResearchAttitudeData attitudeData;
    protected boolean buysDatabanks;
    protected float valueMult;
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        memory = getEntityMemory(memoryMap);

        entity = dialog.getInteractionTarget();
        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        person = dialog.getInteractionTarget().getActivePerson();
        faction = person.getFaction();
        attitudeData  = AoTDMainResearchManager.getInstance().getSpecificFactionManager(faction).getAttitudeData();
        buysDatabanks = !faction.isPlayerFaction();
        valueMult =attitudeData.getDatabankCashMultiplier();
        return true;
    }
    public boolean isValidForContract(FactionAPI faction){

        FactionResearchAttitudeData attitudeData = AoTDMainResearchManager.getInstance().getSpecificFactionManager(faction).getAttitudeData();
        AoTDAIStance stance = attitudeData.getStance();
        return stance!=AoTDAIStance.CLEANSE&&stance!=AoTDAIStance.NEGLECT&&AoTDMainResearchManager.getInstance().getManagerForPlayer().getPlayerCurrentContract()==null;
    }


}
