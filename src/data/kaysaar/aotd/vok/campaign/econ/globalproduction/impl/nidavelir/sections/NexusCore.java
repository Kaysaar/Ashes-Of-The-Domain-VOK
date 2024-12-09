package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.aotd_entities.NidavelirShipyard;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.util.LinkedHashMap;

public class NexusCore extends GPMegaStructureSection {
    @Override
    public void aboutToReconstructSection() {
        super.aboutToReconstructSection();
        NidavelirComplexMegastructure nid = (NidavelirComplexMegastructure) this.getMegastructureTiedTo();
        SectorEntityToken token = nid.shipyard.getEntity();
        nid.shipyard.isVanising = true;
        nid.shipyard.seconds = 1;
        nid.shipyard.elapsed = 0;
        Misc.fadeAndExpire(token);

        nid.shipyard = null;
        nid.shipyard = (NidavelirShipyard) nid.getEntityTiedTo().getStarSystem().addCustomEntity(null, "Nid", "nid_shipyards", null).getCustomPlugin();
        nid.shipyard.trueInit("aotd_nidavelir", "aotd_nidavelir_shadow", (PlanetAPI) nid.getEntityTiedTo());
        nid.shipyard.waitingTime = 0.8f;
    }

    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        super.addButtonsToList(currentButtons);
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("Allows restoration of rest sections",Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.addPara("Colony with Nidavelir complex will start producing ship hulls, weapons, advanced components and domain heavy machinery",Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.addPara("Each section restored will provide colony with ability to produce more",Misc.getTooltipTitleAndLightHighlightColor(),5f);
    }

    @Override
    public void createTooltipForButtonsAfterRest(TooltipMakerAPI tooltip, String buttonId) {

    }
}
