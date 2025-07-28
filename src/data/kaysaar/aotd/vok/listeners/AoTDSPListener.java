package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.LinkedHashMap;
import java.util.List;

public class AoTDSPListener extends PlayerAfterBattleListener {
    public static LinkedHashMap<String, String> unlockingMap = new LinkedHashMap<>();

    static {
        unlockingMap.put("guardian", "$aotd_aqq_guardian");
        unlockingMap.put("nsp_nemetor", "$aotd_aqq_nemetor");
    }

    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {
        executeCodeForFleet(AoTDMisc.getNonPlayerFleet(result).getDisabled());
        executeCodeForFleet(AoTDMisc.getNonPlayerFleet(result).getDestroyed());
    }
    public void executeCodeForFleet(List<FleetMemberAPI> fleets){
        for (FleetMemberAPI fleetMemberAPI : fleets) {
            if (unlockingMap.containsKey(fleetMemberAPI.getVariant().getHullSpec().getHullId())) {
                Global.getSector().getPlayerFaction().getMemory().set(unlockingMap.get(fleetMemberAPI.getVariant().getHullSpec().getHullId()), true);
            }
        }
    }
}