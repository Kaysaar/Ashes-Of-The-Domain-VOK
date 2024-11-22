package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.AoTDMegastructureRules;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.BifrostMega;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;

public class BifrostReesarchListener implements AoTDResearchListener{
    @Override
    public void finishedResearchOfTechnology(String id, FactionAPI faction) {
        if(id.equals(AoTDTechIds.BIFROST_GATE)){
            if(GPManager.getInstance().getMegastructuresBasedOnClass(BifrostMega.class).isEmpty()){
                AoTDMegastructureRules.claimMegastructureManually(null,null,"aotd_bifrost");
            }
        }
    }
}
