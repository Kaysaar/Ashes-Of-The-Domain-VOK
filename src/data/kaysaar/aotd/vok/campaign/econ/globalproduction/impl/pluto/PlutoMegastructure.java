package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;

import java.awt.*;
import java.util.HashMap;

public class PlutoMegastructure extends GPBaseMegastructure {

    @Override
    public void createAdditionalInfoToButton(TooltipMakerAPI tooltipMakerAPI) {
        TooltipMakerAPI tooltip =  tooltipMakerAPI.beginSubTooltip(tooltipMakerAPI.getWidthSoFar());
        tooltip.addPara("Available ore supply units %s",5f, Color.ORANGE,"30");
        tooltip.addPara("Available transplutonic ore supply units %s",5f, Color.ORANGE,"30");
        tooltipMakerAPI.addCustom(tooltip,5f);
        tooltipMakerAPI.setHeightSoFar(tooltipMakerAPI.getHeightSoFar()+40);

    }

    @Override
    public void createAdditionalInfoForMega(TooltipMakerAPI tooltip) {
        super.createAdditionalInfoForMega(tooltip);
        tooltip.addSectionHeading("Current effects", Alignment.MID,5f);
        tooltip.addPara("Available ore supply units %s",5f, Color.ORANGE,"30");
        tooltip.addPara("Available transplutonic ore supply units %s",5f, Color.ORANGE,"30");
    }
}
