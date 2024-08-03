package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.lwjgl.opengl.GL11;

public class AoTDxIndieCollabListener extends PlayerAfterBattleListener {
    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {
        for (FleetMemberAPI fleetMemberAPI : AoTDMisc.getNonPlayerFleet(result).getDisabled()) {
            if (fleetMemberAPI.getVariant().getHullSpec().getHullId().equals("acs_junkhubship_module")) {
                Global.getSector().getPlayerFaction().getMemory().set("$acs_aqq_junkhubship", true);
            }
        }
        for (FleetMemberAPI fleetMemberAPI : AoTDMisc.getNonPlayerFleet(result).getDestroyed()) {
            if (fleetMemberAPI.getVariant().getHullSpec().getHullId().equals("acs_junkhubship_module")) {
                Global.getSector().getPlayerFaction().getMemory().set("$acs_aqq_junkhubship", true);
            }
        }

    }
}
