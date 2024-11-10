package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.LinkedHashMap;

public class SentinelCore extends GPMegaStructureSection {
    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        super.addButtonsToList(currentButtons);
        ButtonData data1 = new ButtonData("Adjust effective range", this, this.isRestored, Color.CYAN, "adjustRange", new OnHoverButtonTooltip(this, "adjustRange"), "adjustRange", this.getSpec().getSectionID());
        currentButtons.put("adjustRange", data1);
    }
    @Override
    public boolean isRestorationAllowed() {
        return  megastructureTiedTo.getSectionById("nidavelir_nexus").isRestored;
    }
}

