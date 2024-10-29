package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CoronalCollector extends GPMegaStructureSection {

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("Allows usage of Hypershunt tap for all colonies within effective range, some with unique effects",5f);
        tooltip.addPara("Decreases upkeep of purified transplutonics on other megastructures by %s",5f, Color.ORANGE,"20%");
        tooltip.addPara("If demand for Domain-grade machinery is not met, effects will be lowered by %s",5f,Color.ORANGE,"50%");
    }


    @Override
    public HashMap<String, Integer> getGPUpkeep() {
        return super.getGPUpkeep();
    }
}
