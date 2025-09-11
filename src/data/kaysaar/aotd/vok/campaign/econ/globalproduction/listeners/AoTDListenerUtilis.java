package data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.combat.MutableStat;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.models.AoTDResourceListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.models.MegastructureUpkeepReductionListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.campaign.econ.listeners.AoTDResearchListener;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.HashMap;
import java.util.Map;

public class AoTDListenerUtilis {
    public static void applyUpkeepReductionForGP(GPMegaStructureSection section, HashMap<String,Integer> currentGPUpkeep){
        for (MegastructureUpkeepReductionListener listener : Global.getSector().getListenerManager().getListeners(MegastructureUpkeepReductionListener.class)) {
            listener.applyUpkeepReductionGP(section,currentGPUpkeep);
        }

    }
    public static void applyUpkeepReductionCredits(GPMegaStructureSection section, MutableStat upkeepMult){
        for (MegastructureUpkeepReductionListener listener : Global.getSector().getListenerManager().getListeners(MegastructureUpkeepReductionListener.class)) {
            listener.applyUpkeepReductionCredits(section,upkeepMult);
        }

    }
    public static void increaseProductionCapacity(HashMap<String,Integer>currentResourceOutput,Object toIgonre){
        for (AoTDResourceListener listener : Global.getSector().getListenerManager().getListeners(AoTDResourceListener.class)) {
            HashMap<String,Integer> data = listener.increaseProductionCapacity(toIgonre);
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                AoTDMisc.putCommoditiesIntoMap(currentResourceOutput,entry.getKey(),entry.getValue());
            }

        }

    }
    public static void increaseDemand(HashMap<String,Integer>currentDemand){
        for (AoTDResourceListener listener : Global.getSector().getListenerManager().getListeners(AoTDResourceListener.class)) {
            HashMap<String,Integer> data = listener.increaseDemand();
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                AoTDMisc.putCommoditiesIntoMap(currentDemand,entry.getKey(),entry.getValue());
            }

        }

    }
    public static void finishedResearch(String option, FactionAPI faction){
        for (AoTDResearchListener listener : Global.getSector().getListenerManager().getListeners(AoTDResearchListener.class)) {
            listener.finishedResearchOfTechnology(option,faction);
        }
    }

}
