package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.sections;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

public class BifrostSection extends GPMegaStructureSection {
    public SectorEntityToken gateTiedTo;

    public SectorEntityToken getGateTiedTo() {
        return gateTiedTo;
    }

    @Override
    public String getName() {
        return "Bifrost Gate : "+gateTiedTo.getStarSystem().getName();
    }

    public void setGateTiedTo(SectorEntityToken gateTiedTo) {
        this.gateTiedTo = gateTiedTo;
    }
}
