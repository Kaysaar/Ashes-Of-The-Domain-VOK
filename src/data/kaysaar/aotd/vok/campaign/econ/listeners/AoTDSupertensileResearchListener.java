package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.campaign.FactionAPI;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.MegastructureStatManager;


public class AoTDSupertensileResearchListener implements AoTDResearchListener{
    @Override
    public void finishedResearchOfTechnology(String id, FactionAPI faction) {
        if(id.equals(AoTDTechIds.SUPERTENSILES)){
            MegastructureStatManager.getInstance().getMegastructureResourceCostMult().modifyMult("aotd_super",0.8f,"Supertensiles");
        }
    }
}
