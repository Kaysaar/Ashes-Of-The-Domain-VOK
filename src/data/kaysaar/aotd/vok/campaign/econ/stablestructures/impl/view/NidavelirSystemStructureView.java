package data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl.view;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.NidavelirMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;

public class NidavelirSystemStructureView extends BaseMegastructureSystemStructureView {

    @Override
    protected String getMegastructureId() {
        return "aotd_nidavelir";
    }


    @Override
    public String getImageName() {
        return super.getImageName();
    }

    @Override
    protected SectorEntityToken getMegastructureOwnerEntity() {
        if (currentlyAssignedToken == null || currentlyAssignedToken.getStarSystem() == null) return null;

        // The damaged and restored shipyard visuals are replaced during restoration.
        // Resolve the real planetary owner via the currently active visual.
        for (PlanetAPI planet : currentlyAssignedToken.getStarSystem().getPlanets()) {
            BaseMegastructureScript script = BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(
                    planet, getMegastructureId());
            if (!(script instanceof NidavelirMegastructure)) continue;
            NidavelirMegastructure nidavelir = (NidavelirMegastructure) script;
            if (nidavelir.getVisual() == null) continue;
            if (nidavelir.getVisual().getEntity() == currentlyAssignedToken) return planet;
        }
        return null;
    }
}
