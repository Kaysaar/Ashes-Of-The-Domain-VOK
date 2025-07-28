package data.kaysaar.aotd.vok.scripts.specialprojects.listeners;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPProductionListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPSpec;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectCompletionListener;

public class NidavelirSPListener implements SpecialProjectCompletionListener, GPProductionListener {
    @Override
    public void completedProject(String idOfProject, Object reward) {
        addBonuses(reward);
    }

    private static void addBonuses(Object reward) {
        if (reward instanceof FleetMemberAPI member) {
            if (AoTDMisc.getNidavelirIfOwned() != null) {
                NidavelirComplexMegastructure mega = AoTDMisc.getNidavelirIfOwned();
                if (mega.getSectionById("nidavelir_vanguard").isRestored) {
                    member.getStats().getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlatAlways("aotd_nidavelir_vanguard", 1,"");
                    member.getVariant().addPermaMod("aotd_nidavelir_hullmod");
                }
                if (mega.getSectionById("nidavelir_bulwark").isRestored) {

                    member.getStats().getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlatAlways("aotd_nidavelir_bullwark", 2,"");
                    member.getVariant().addPermaMod(HullMods.FLUX_COIL,true);
                    member.getVariant().addPermaMod(HullMods.FLUX_DISTRIBUTOR,true);
                    member.getVariant().addPermaMod("aotd_nidavelir_hullmod");
                }
            }
        }
    }
    @Override
    public void reportPlayerProducedStuff(GPSpec spec, Object param, int amount) {
        addBonuses(param);
    }
}
