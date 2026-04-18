package data.kaysaar.aotd.vok.campaign.econ.produciton.listeners;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.loading.VariantSource;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.NidavelirMegastructure;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

public class AoTDProdListener implements AoTDProductionListenerAPI{
    @Override
    public void onShipProductionFinished(FleetMemberAPI member) {
        if (AoTDMisc.getNidavelirIfOwned() != null) {
            NidavelirMegastructure mega = AoTDMisc.getNidavelirIfOwned();
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
