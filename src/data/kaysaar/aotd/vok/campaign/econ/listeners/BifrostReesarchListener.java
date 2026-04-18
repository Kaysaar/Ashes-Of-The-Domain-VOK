package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.AoTDMegastructureRules;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;


public class BifrostReesarchListener implements AoTDResearchListener{
    @Override
    public void finishedResearchOfTechnology(String id, FactionAPI faction) {
        if(id.equals(AoTDTechIds.BIFROST_GATE)){
            //NOTHING TO IMPL
        }
    }
}
