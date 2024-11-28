package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.ui.P;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastrcutre;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
        if(buttonId.equals("restore")&&!isRestored&&!isRestorationAllowed()){
            tooltip.addPara("Note! First %s must be restored, before we are able to restore this section!",5f, Misc.getNegativeHighlightColor(),Color.ORANGE,"Nexus Ring");
        }
        if(buttonId.equals("assignManpower")){
            tooltip.addPara("We can assign to this megastructure certain amount of workers, to produce enormous amount of commodities",5f);
        }
        if(buttonId.equals("automateSection")){
            if(isAutomated){
                tooltip.addPara("We can deactivate automation systems and disconnect it from %s",5f,Color.ORANGE,"Hypershunt Receiver");

            }
            else{
                tooltip.addPara("Our engineers think it is possible to link %s directly with %s, allowing full automation of this section, as long as connection to Hypershunt remains.",5f,Color.ORANGE,this.getName(),"Hypershunt Receiver");

            }
        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(!HypershuntMegastrcutre.isWithinReciverSystem(this.getMegastructureTiedTo().entityTiedTo)){
            isAutomated = false;
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
        ButtonData data1 = new ButtonData("Assign Manpower", this, this.isRestored&&!this.isAutomated, new Color(239, 60, 60, 255), "assignManpower", new OnHoverButtonTooltip(this, "assignManpower"), "assignManpower", this.getSpec().getSectionID());
        currentButtons.put("assignManpower", data1);
        if(!isAutomated){
            ButtonData data2 = new ButtonData("Automate Section", this, this.isRestored&& HypershuntMegastrcutre.isWithinReciverSystem(this.getMegastructureTiedTo().getEntityTiedTo()), new Color(98, 231, 184, 255), "automateSection", new OnHoverButtonTooltip(this, "automateSection"), "automateSection", this.getSpec().getSectionID());
            currentButtons.put("automateSection", data2);
        }
        else{
            ButtonData data2 = new ButtonData("De-automate Section", this, this.isRestored&& HypershuntMegastrcutre.isWithinReciverSystem(this.getMegastructureTiedTo().getEntityTiedTo()), new Color(98, 231, 184, 255), "automateSection", new OnHoverButtonTooltip(this, "automateSection"), "automateSection", this.getSpec().getSectionID());
            currentButtons.put("automateSection", data2);
        }


    }
    public void createTooltipForMainSection(TooltipMakerAPI tooltip) {

    }
    public void printMenu(TooltipMakerAPI  tooltip, int manpowerToBeAssigned, boolean wantToAutomate){

    }
    public void printEffects(TooltipMakerAPI tooltip, int manpowerToBeAssigned, boolean wantToAutomate) {
        HashMap<String,Integer>increase = new HashMap<>();
        HashMap<String,Integer>demand = new HashMap<>();
        for (Map.Entry<String, Float> s : NidavelirComplexMegastructure.commoditiesProd.entrySet()) {
            increase.put(s.getKey(), (int) (manpowerToBeAssigned*s.getValue()));

        }
        for (Map.Entry<String, Float> s : NidavelirComplexMegastructure.commoditiesDemand.entrySet()) {
            demand.put(s.getKey(), (int) (manpowerToBeAssigned*s.getValue()));
        }
        tooltip.addPara("This section is going to produce this amount of GP units of resources, as long as demand is met",5f);
        tooltip.addSectionHeading("Production", Alignment.MID,5f);
        tooltip.addCustom(MegastructureUIMisc.createResourcePanel(tooltip.getWidthSoFar(),40,40,increase, Misc.getPositiveHighlightColor()),5f);
        tooltip.addSectionHeading("Demand", Alignment.MID,5f);
        tooltip.addCustom(MegastructureUIMisc.createResourcePanel(tooltip.getWidthSoFar(),40,40,demand,Misc.getNegativeHighlightColor()),10f);
    }
    public int getEffectiveManpowerForEffects(){
        int manpowerPoints = getCurrentManpowerAssigned();
        if(isAutomated){
            manpowerPoints = 12;

        }
        return manpowerPoints;
    }
    @Override
    public HashMap<String, Integer> getProduction(HashMap<String, Float> penaltyMap) {
        HashMap<String, Integer>map = new HashMap<>();
        int manpower = currentManpowerAssigned;
        if(isAutomated){
            manpower = 12;
        }
        for (Map.Entry<String, Float> s : NidavelirComplexMegastructure.commoditiesProd.entrySet()) {
            float totalVal = s.getValue()*manpower*(float) AoTDMisc.getOrDefault(penaltyMap, AoTDCommodities.REFINED_METAL,1f);
            AoTDMisc.putCommoditiesIntoMap(map,s.getKey(), (int) totalVal);

        }

        return map;

    }
    @Override
    public void applyAdditionalGPChanges(HashMap<String, Integer> map) {
        int manpower = getCurrentManpowerAssigned();
        if(isAutomated){
            manpower =12;
        }
        map.put(AoTDCommodities.REFINED_METAL, (int) (NidavelirComplexMegastructure.commoditiesDemand.get(AoTDCommodities.REFINED_METAL)*manpower));
    }

}
