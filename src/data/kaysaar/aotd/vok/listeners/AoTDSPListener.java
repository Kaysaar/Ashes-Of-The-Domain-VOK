package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import data.kaysaar.aotd.vok.listeners.PlayerAfterBattleListener;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

public class AoTDSPListener extends PlayerAfterBattleListener {
    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {
        for (FleetMemberAPI fleetMemberAPI : AoTDMisc.getNonPlayerFleet(result).getDisabled()) {
            if (fleetMemberAPI.getVariant().getHullSpec().getHullId().equals("guardian")) {
                Global.getSector().getPlayerFaction().getMemory().set("$aotd_aqq_guardian", true);
            }
        }
        for (FleetMemberAPI fleetMemberAPI : AoTDMisc.getNonPlayerFleet(result).getDestroyed()) {
            if (fleetMemberAPI.getVariant().getHullSpec().getHullId().equals("guardian")) {
                Global.getSector().getPlayerFaction().getMemory().set("$aotd_aqq_guardian", true);
            }
        }

    }
}