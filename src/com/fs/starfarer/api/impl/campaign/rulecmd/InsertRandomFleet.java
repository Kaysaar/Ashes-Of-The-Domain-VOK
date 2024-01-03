package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InsertRandomFleet extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        List<AoTDFactionResearchManager>managers = AoTDMainResearchManager.getInstance().getFactionResearchManagers();
        Collections.shuffle(managers);
        CampaignFleetAPI fleet = FleetFactory.createGenericFleet(managers.get(0).getFaction().getId(),"Exploratory fleet",1,200);
        dialog.getInteractionTarget().getMemory().set("$defenderFleet",fleet);
        return true;
    }
}
