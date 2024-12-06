package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections;

import com.fs.starfarer.api.impl.campaign.aotd_entities.HypershuntReciverEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastrcutre;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.PlutoMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpticCommandNexus extends GPMegaStructureSection {
    public  int currentMagnitude = 1 ;
    public static  int maxMagnitude =10;
    public boolean connectedToHypershunt = false;
    public boolean wasConnectedToHypershunt = false;
    public static int minMagnitude = 0; //0  means turned off;
    int supplyUnitsPerMagnitude = 5;
    public boolean isFiringLaser(){
        return currentMagnitude>0;
    }

    public int getCurrentMagnitude() {
        return currentMagnitude;
    }
    public PlutoMegastructure getMega(){
        return (PlutoMegastructure) getMegastructureTiedTo();
    }
    public void createTooltipForOreMiningLite(TooltipMakerAPI tooltip) {
        int amountOre = getAvailableOresAmount(Commodities.ORE);
        int amountR_Ore= getAvailableOresAmount(Commodities.RARE_ORE);

        tooltip.addPara("Available ore supply units %s",5f, Color.ORANGE,amountOre+"");
        tooltip.addPara("Available transplutonic ore supply units %s",5f, Color.ORANGE,amountR_Ore+"");


    }
    public void createTooltipForOreMining(TooltipMakerAPI tooltip) {
        int amountOre = getAvailableOresAmount(Commodities.ORE);
        int amountR_Ore= getAvailableOresAmount(Commodities.RARE_ORE);

        tooltip.addSectionHeading("Ore section", Alignment.MID,5f);
        tooltip.addPara("Available ore supply units %s",5f, Color.ORANGE,amountOre+"");
        for (PlutoForgeSection consumerSection : getMega().getConsumerSections()) {
            consumerSection.createTooltipForResourceProductionLite(Commodities.ORE,consumerSection.getAssignedResources(Commodities.ORE),tooltip);
        }
        tooltip.addSectionHeading("Transplutonic ore section", Alignment.MID,5f);

        tooltip.addPara("Available transplutonic ore supply units %s ",5f, Color.ORANGE,amountR_Ore+"");
        for (PlutoForgeSection consumerSection : getMega().getConsumerSections()) {
            consumerSection.createTooltipForResourceProductionLite(Commodities.RARE_ORE,consumerSection.getAssignedResources(Commodities.RARE_ORE),tooltip);
        }

    }
    public  int getAvailableOresAmount(String idOfOre){
        float amount = currentMagnitude*supplyUnitsPerMagnitude;
        if(!connectedToHypershunt){
            amount*=getPenaltyFromManager(AoTDCommodities.PURIFIED_TRANSPLUTONICS);
        }
        if(!isRestored){
            amount = 0;
        }
        int ore;
        ore = ((PlutoMegastructure)(getMegastructureTiedTo())).getAvailableResources((int) amount,idOfOre);
        return (int) ore;
    }
    @Override
    public void createTooltipForButtons(TooltipMakerAPI tooltip, String buttonId) {
        super.createTooltipForButtons(tooltip, buttonId);
    }

    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        ButtonData data1 = new ButtonData("Adjust laser strength", this, this.isRestored,new Color(224, 42, 42,255), "adjustLaser", new OnHoverButtonTooltip(this, "adjustLaser"), "adjustLaser", this.getSpec().getSectionID());
        currentButtons.put("adjustLaser", data1);
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("Allows extraction of ores and transplutonic ores by using laser",5f);
        if(HypershuntMegastrcutre.isWithinReciverSystem(this.getMegastructureTiedTo().getEntityTiedTo())){
            tooltip.addPara("Due to connection with Hypershunt, power of laser is doubled, together with extraction yield", Misc.getPositiveHighlightColor(),5f);
        }
        if(isRestored){
            int amount = 30;
            tooltip.addPara("Currently extracting %s of %s",5f,Color.ORANGE,""+amount,"ore");
            tooltip.addPara("Currently extracting %s of %s",5f,Color.ORANGE,""+amount,"transplutonic ore");
        }
    }

    @Override
    public HashMap<String, Integer> getProduction(HashMap<String, Float> penaltyMap) {
        HashMap<String,Integer> map = new HashMap<>();
//        for (Map.Entry<String, Float> s : NidavelirComplexMegastructure.commoditiesProd.entrySet()) {
//            float totalVal = s.getValue() AoTDMisc.getOrDefault(penaltyMap, AoTDCommodities.REFINED_METAL,1f);
//            AoTDMisc.putCommoditiesIntoMap(map,s.getKey(), (int) totalVal);
//
//        }

        return map;

    }
}
