package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.NidavelirShipyard;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
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
        nid.shipyard.waitingTime = 1f;
    }

    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        super.addButtonsToList(currentButtons);
        ButtonData data1 = new ButtonData("Assign Manpower", this, this.isRestored, new Color(239, 60, 60, 255), "adjustRange", new OnHoverButtonTooltip(this, "adjustRange"), "adjustRange", this.getSpec().getSectionID());
        currentButtons.put("adjustRange", data1);
        ButtonData data2 = new ButtonData("Automate Section", this, this.isRestored, new Color(98, 231, 184, 255), "adjustRange", new OnHoverButtonTooltip(this, "adjustRange"), "adjustRange", this.getSpec().getSectionID());
        currentButtons.put("adjustRange2", data2);
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
