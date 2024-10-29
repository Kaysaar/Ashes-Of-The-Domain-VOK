package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.LinkedHashMap;

public class WormholeGenerator extends GPMegaStructureSection {
    public int range =10;
    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("Allow to increase effective range of Hypershunt up to %s, as long as upkeep of purified transplutonics is met.",5f,Color.ORANGE,"70 LY");
        tooltip.addSectionHeading("Effective range",Alignment.MID,5f);
        tooltip.addPara("Current effective range is %s LY which costs us %s purified transplutonics",5f,Color.ORANGE,""+range,""+0);
    }

    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        ButtonData data1 = new ButtonData("Adjust effective range", this, this.isRestored, Color.CYAN, "adjustRange", new OnHoverButtonTooltip(this, "adjustRange"), "adjustRange", this.getSpec().getSectionID());
        currentButtons.put("adjustRange", data1);
    }

    @Override
    public void createTooltipForButtons(TooltipMakerAPI tooltip, String buttonId) {
        super.createTooltipForButtons(tooltip, buttonId);
        if(buttonId.equals("adjustRange")) {
            tooltip.addPara("Once restored we can adjust effective range of hypershunt",5f);
        }
    }
}
