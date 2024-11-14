package data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableStat;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.models.MegastructureUpkeepReductionListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.HashMap;
import java.util.Map;

public class AoTDSupertencileListener implements MegastructureUpkeepReductionListener {
    @Override
    public void applyUpkeepReductionGP(GPMegaStructureSection megastructure, HashMap<String, Integer> currentGPUpkeep) {
        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(AoTDTechIds.SUPERTENCILES)){
            for (Map.Entry<String, Integer> entry : currentGPUpkeep.entrySet()) {
                currentGPUpkeep.put(entry.getKey(), (int) (entry.getValue()*0.8f));
            }
        }
    }

    @Override
    public void applyUpkeepReductionCredits(GPMegaStructureSection section, MutableStat upkeepMult) {

    }
}
