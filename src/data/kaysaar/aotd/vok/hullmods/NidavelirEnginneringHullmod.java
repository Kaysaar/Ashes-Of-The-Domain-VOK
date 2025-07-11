package data.kaysaar.aotd.vok.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

public class NidavelirEnginneringHullmod extends BaseHullMod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if (AoTDMisc.getNidavelirIfOwned() != null) {
            NidavelirComplexMegastructure mega = AoTDMisc.getNidavelirIfOwned();
            if (mega.getSectionById("nidavelir_vanguard").isRestored) {
                stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlatAlways("aotd_nidavelir_vanguard", 1,"");
            }
            if (mega.getSectionById("nidavelir_bulwark").isRestored) {
                stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlatAlways("aotd_nidavelir_bullwark", 2,"");
            }
        }
    }

}
