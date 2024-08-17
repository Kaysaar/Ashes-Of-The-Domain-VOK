package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.ht.HyperspaceTopographyEventIntel;
import com.fs.starfarer.api.impl.campaign.rulecmd.ResearchContract;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.contracts.BaseResearchContract;
import data.kaysaar.aotd.vok.scripts.research.contracts.ResearchContractSpec;

import java.awt.*;
import java.util.Map;

public class ResearchContractIntel extends BaseEventIntel {
    public static Color BAR_COLOR = Global.getSettings().getColor("progressBarFleetPointsColor");
    public static String KEY = "$aotd_contract";


    public ResearchContractIntel(TextPanelAPI text, boolean withIntelNotification) {
        super();
        setup();

        // now that the event is fully constructed, add it and send notification
        Global.getSector().getIntelManager().addIntel(this, !withIntelNotification, text);
    }
    public void setup(){
        factors.clear();
        stages.clear();
        BaseResearchContract contract = AoTDMainResearchManager.getInstance().getManagerForPlayer().getPlayerCurrentContract();
        setMaxProgress(contract.spec.valueToFinishContract);
        int i =0;
        for (Map.Entry<String, Integer> eventStageData : contract.getSpec().stages.entrySet()) {
            if(i<contract.getSpec().stages.size()-1){
                addStage(eventStageData.getKey(),eventStageData.getValue(),StageIconSize.MEDIUM);
            }
            else{
                addStage(eventStageData.getKey(),eventStageData.getValue(),true,StageIconSize.MEDIUM);
            }

            i++;
        }
        for (Map.Entry<String, Integer> entry : contract.getSpec().stages.entrySet()) {
            getDataFor(entry).keepIconBrightWhenLaterStageReached = true;

        }

    }
    @Override
    protected void notifyEnded() {
        super.notifyEnded();
    }
}
