package data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl.view;

import com.fs.starfarer.api.campaign.SectorEntityToken;

public class CoronalHypershuntSystemStructureView extends BaseMegastructureSystemStructureView {

    @Override
    protected String getMegastructureId() {
        return "coronal_hypershunt";
    }

    @Override
    protected SectorEntityToken getMegastructureOwnerEntity() {
        return currentlyAssignedToken;
    }
}
