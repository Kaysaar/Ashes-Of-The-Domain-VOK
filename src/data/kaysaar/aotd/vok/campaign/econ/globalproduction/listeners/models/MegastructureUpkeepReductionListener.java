package data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.models;

import com.fs.starfarer.api.combat.MutableStat;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.util.HashMap;

public interface MegastructureUpkeepReductionListener {
    public void applyUpkeepReductionGP(GPMegaStructureSection megastructure, HashMap<String,Integer> currentGPUpkeep);
    public void applyUpkeepReductionCredits(GPMegaStructureSection section,MutableStat upkeepMult);
}
