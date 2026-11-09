package data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl.view;

import com.fs.starfarer.api.campaign.SectorEntityToken;

public class PlutoSystemStructureView extends BaseMegastructureSystemStructureView {

    @Override
    protected String getMegastructureId() {
        return "aotd_pluto_station";
    }

    @Override
    protected SectorEntityToken getMegastructureOwnerEntity() {
        if (currentlyAssignedToken == null) return null;
        // Pluto's script is stored on its planet, not on the orbiting station.
        return currentlyAssignedToken.getOrbitFocus();
    }
}
