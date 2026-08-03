package data.kaysaar.aotd.vok.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;

import static data.kaysaar.aotd.vok.campaign.econ.industry.grandwonders.GardensOfElysium.memKeyForEffect;

public class AoTDCRHullmod extends BaseHullMod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if(!Misc.isAutomated(stats)&& Global.getSector().getPlayerMemoryWithoutUpdate().is(memKeyForEffect,true)){
            stats.getMaxCombatReadiness().modifyFlat("aotd_elysium",0.2f,"Elysian Wonders");
        }

    }


}
