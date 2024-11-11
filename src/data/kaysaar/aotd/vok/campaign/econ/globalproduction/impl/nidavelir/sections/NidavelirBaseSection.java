package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastrcutre;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.LinkedHashMap;

public class NidavelirBaseSection extends GPMegaStructureSection {
    public int currentManpowerAssigned;
    public static int MaxManpowerPerSection =  6;

    public int getCurrentManpowerAssigned() {
        return currentManpowerAssigned;
    }

    public void setCurrentManpowerAssigned(int currentManpowerAssigned) {
        this.currentManpowerAssigned = currentManpowerAssigned;
    }
    public boolean isAutomated;

    public boolean isAutomated() {
        return isAutomated;
    }
    @Override
    public void createTooltipForButtonsBeforeRest(TooltipMakerAPI tooltip, String buttonId) {
        if(buttonId.equals("restore")&&!isRestored&&isRestorationAllowed()){
            tooltip.addPara("Note! First %s must be restored, before we are able to restore this section!",5f, Misc.getNegativeHighlightColor(),Color.ORANGE,"Nexus Ring");
        }
    }


    public void setAutomated(boolean automated) {
        isAutomated = automated;
    }
    public boolean isRestorationAllowed() {
        return  megastructureTiedTo.getSectionById("nidavelir_nexus").isRestored;
    }
    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        super.addButtonsToList(currentButtons);
        ButtonData data1 = new ButtonData("Assign Manpower", this, this.isRestored, new Color(239, 60, 60, 255), "assignManpower", new OnHoverButtonTooltip(this, "assignManpower"), "assignManpower", this.getSpec().getSectionID());
        currentButtons.put("assignManpower", data1);
        ButtonData data2 = new ButtonData("Automate Section", this, this.isRestored&&!this.isAutomated&& HypershuntMegastrcutre.isWithinReciverSystem(this.getMegastructureTiedTo().getEntityTiedTo()), new Color(98, 231, 184, 255), "automateSection", new OnHoverButtonTooltip(this, "automateSection"), "automateSection", this.getSpec().getSectionID());
        currentButtons.put("automateSection", data2);
    }
}
