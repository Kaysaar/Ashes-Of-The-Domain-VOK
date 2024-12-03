package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;

public class FusionForge extends GPMegaStructureSection  {

    @Override
    public boolean isRestorationAllowed() {
        return  megastructureTiedTo.getSectionById("pluto_ocn").isRestored;
    }
    @Override
    public void createTooltipForButtonsBeforeRest(TooltipMakerAPI tooltip, String buttonId) {
        if(buttonId.equals("restore")&&!isRestored&&!isRestorationAllowed()){
            tooltip.addPara("Note! First %s must be restored, before we are able to restore this section!",5f, Misc.getNegativeHighlightColor(), Color.ORANGE,"Optic Command Nexus");
        }
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("For each unit of ore dedicated to this section : %s ",5f,Color.ORANGE,"+4 to metal supply");
        tooltip.addPara("For each unit of transplutonic ore dedicated to this section : %s ",5f,Color.ORANGE,"+2 to transplutonic supply");
    }
}
