package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.PlutoMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.LinkedHashMap;

public class PlutoForgeSection extends GPMegaStructureSection {
    public LinkedHashMap<String,Integer>resourcesDesignated = new LinkedHashMap<>();

    public LinkedHashMap<String, Integer> getResourcesDesignated() {
        if(resourcesDesignated == null) {
            resourcesDesignated = new LinkedHashMap<>();
        }
        return resourcesDesignated;
    }
    public void updateResourceDesignated(String commodity,Integer newAmount){
        getResourcesDesignated().put(commodity,newAmount);
    }

    public int getAssignedResources(String key){
        if(getResourcesDesignated().get(key)==null){
            getResourcesDesignated().put(key,0);
        }
        return getResourcesDesignated().get(key);
    }
    public PlutoMegastructure getMega(){
        return (PlutoMegastructure) getMegastructureTiedTo();
    }
    public void createTooltipForResourceProduction(String resource , int oreUnitsAssigned,TooltipMakerAPI tooltip){

    }
    public void createTooltipForResourceProductionLite(String resource , int oreUnitsAssigned,TooltipMakerAPI tooltip){

    }
    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        ButtonData data1 = new ButtonData("Assign ore", this, this.isRestored,new Color(119, 119, 119,255), "assignO", new OnHoverButtonTooltip(this, "assignO"), "assignO", this.getSpec().getSectionID());
        currentButtons.put("assignO", data1);
         data1 = new ButtonData("Assign transplutonic ore", this, this.isRestored,new Color(252, 245, 240,255), "assignTO", new OnHoverButtonTooltip(this, "assignTO"), "assignTO", this.getSpec().getSectionID());
        currentButtons.put("assignTO", data1);
    }
    @Override
    public boolean isRestorationAllowed() {
        return  megastructureTiedTo.getSectionById("pluto_ocn").isRestored&&super.isRestorationAllowed();
    }

    @Override
    public void createTooltipForButtonsAfterRest(TooltipMakerAPI tooltip, String buttonId) {
        if(buttonId.equals("assignO")||buttonId.equals("assignTO")) {
            tooltip.addPara("With resources excavated by using station's powerful laser, we can refine them, to different resources",5f);
        }
    }
    public int getAmountOfResources(String key){
        return 0;
    }

}
