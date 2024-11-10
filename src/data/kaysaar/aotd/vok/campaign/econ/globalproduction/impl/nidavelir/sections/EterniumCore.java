package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class EterniumCore extends GPMegaStructureSection {
    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        super.addButtonsToList(currentButtons);
        ButtonData data1 = new ButtonData("Assign Manpower", this, this.isRestored, new Color(239, 60, 60, 255), "adjustRange", new OnHoverButtonTooltip(this, "adjustRange"), "adjustRange", this.getSpec().getSectionID());
        currentButtons.put("adjustRange", data1);
        ButtonData data2 = new ButtonData("Automate Section", this, this.isRestored, new Color(98, 231, 184, 255), "adjustRange", new OnHoverButtonTooltip(this, "adjustRange"), "adjustRange", this.getSpec().getSectionID());
        currentButtons.put("adjustRange2", data2);
    }

    @Override
    public boolean isRestorationAllowed() {
        return  megastructureTiedTo.getSectionById("nidavelir_nexus").isRestored;
    }
    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("For each assigned manpower point to section:",5f);
        tooltip.addPara("Increase speed of special projects completion by %s",3f,Color.ORANGE,"2%");
        tooltip.addPara("Increase GP production of %s by %s for each assigned point of manpower, as long as demand for refined metal is met",5f,Color.ORANGE,"ship hulls, weapons, advanced components and domain heavy machinery",""+10);
        tooltip.addPara("Increase GP demand of %s by %s for each assigned point of manpower",5f,Color.ORANGE,"refined metal",""+10);

    }

    @Override
    public HashMap<String, Integer> getProduction() {
        return super.getProduction();
    }
}
