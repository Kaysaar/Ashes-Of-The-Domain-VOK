package data.kaysaar.aotd.vok.scripts.specialprojects.listeners;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.loading.VariantSource;
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
                member.getVariant().setSource(VariantSource.REFIT);
                if (mega.getSectionById("nidavelir_vanguard").isRestored) {
                    member.getVariant().addPermaMod("aotd_nidavelir_hullmod",false);
                }
                if (mega.getSectionById("nidavelir_bulwark").isRestored) {
                    member.getVariant().addPermaMod(HullMods.FLUX_COIL,false);
                    member.getVariant().addPermaMod(HullMods.FLUX_DISTRIBUTOR,false);

                }
            }
        }
    }
    @Override
    public void reportPlayerProducedStuff(GPSpec spec, Object param, int amount) {
        addBonuses(param);
    }
}
