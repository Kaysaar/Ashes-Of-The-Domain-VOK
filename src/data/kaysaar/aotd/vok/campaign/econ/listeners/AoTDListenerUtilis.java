package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.combat.MutableStat;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.HashMap;
import java.util.Map;

public class AoTDListenerUtilis {
    public static void finishedResearch(String option, FactionAPI faction){
        for (AoTDResearchListener listener : Global.getSector().getListenerManager().getListeners(AoTDResearchListener.class)) {
            listener.finishedResearchOfTechnology(option,faction);
        }
    }

}
