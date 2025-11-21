package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CoronalCollector extends GPMegaStructureSection {
    boolean isInPenalty = false;

    public boolean isInPenalty() {
        return isInPenalty;
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("Allows building a Hypershunt Receiver in a stable location within the effective range of the nearest Hypershunt, that gives unique effects to markets and megastructures.",5f);
        tooltip.addPara("If demand for Domain-grade machinery is not met, effects of the Hypershunt Receiver will be lowered by %s",5f,Color.ORANGE,"50%");
    }

    @Override
    public void apply() {
        super.apply();
        isInPenalty = getPenaltyFromManager(AoTDCommodities.DOMAIN_GRADE_MACHINERY)<1;
    }

    @Override
    public HashMap<String, Integer> getGPUpkeep() {
        return super.getGPUpkeep();
    }

    @Override
    public void aboutToReconstructSection() {
        super.aboutToReconstructSection();
        this.getMegastructureTiedTo().getEntityTiedTo().getMemory().set("$usable",true);
    }
}
