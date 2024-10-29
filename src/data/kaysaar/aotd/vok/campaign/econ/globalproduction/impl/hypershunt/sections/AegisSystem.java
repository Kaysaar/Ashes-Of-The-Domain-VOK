package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.campaign.CampaignEngine;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;

public class AegisSystem extends GPMegaStructureSection {
    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("Allows for spawning of one huge fleet, that will defend megastructure, as long as demand for ship hulls an weapons is met",5f);
        if(!isRestored){
            tooltip.addPara("Our scavenge team also reported weird readings coming from currently inaccessible sections of %s, " +
                    "similar to those of ships we met when discovered hypershunt for first time.",5f,Color.ORANGE,this.getName());
        }
        else{
            tooltip.addPara("Gained two %s weapon blueprints",5f,new Color(196, 32, 250),"[ULTRA-REDACTED]");
        }
    }

    @Override
    public void aboutToReconstructSection() {

    }
}
