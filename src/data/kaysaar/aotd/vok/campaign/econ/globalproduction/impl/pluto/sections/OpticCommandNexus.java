package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.PlutoMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class OpticCommandNexus extends GPMegaStructureSection {
    public int currentMagnitude = 0;
    public static int maxMagnitude = 10;
    public static int upkeepMulter = 5000;
    public boolean connectedToHypershunt = false;
    public boolean wasConnectedToHypershunt = false;
    public static int minMagnitude = 0; //0  means turned off;

    public void setCurrentMagnitude(int currentMagnitude) {
        this.currentMagnitude = currentMagnitude;
    }

    int supplyUnitsPerMagnitude = 5;
    public int getMaxMagnitude(){
        if(HypershuntMegastructure.isWithinReceiverSystem(this.getMega().getEntityTiedTo())){
            return maxMagnitude;
        }
        return maxMagnitude-3;
    }
    public boolean isFiringLaser() {
        return currentMagnitude > 0;
    }

    public int getCurrentMagnitude() {
        return currentMagnitude;
    }

    @Override
    public float addAdditionalUpkeep() {
        return currentMagnitude*upkeepMulter;
    }

    public PlutoMegastructure getMega() {
        return (PlutoMegastructure) getMegastructureTiedTo();
    }

    public void createTooltipForOreMiningLite(TooltipMakerAPI tooltip) {
        int amountOre = getAvailableOresAmount(Commodities.ORE);
        int amountR_Ore = getAvailableOresAmount(Commodities.RARE_ORE);

        tooltip.addPara("Available ore supply units %s", 5f, Color.ORANGE, amountOre + "");
        tooltip.addPara("Available transplutonic ore supply units %s", 5f, Color.ORANGE, amountR_Ore + "");


    }

    public void createTooltipForOreMining(TooltipMakerAPI tooltip) {
        int amountOre = getAvailableOresAmount(Commodities.ORE);
        int amountR_Ore = getAvailableOresAmount(Commodities.RARE_ORE);

        tooltip.addSectionHeading("Ore section", Alignment.MID, 5f);
        tooltip.addPara("Available ore supply units %s", 5f, Color.ORANGE, amountOre + "");
        for (PlutoForgeSection consumerSection : getMega().getConsumerSections()) {
            consumerSection.createTooltipForResourceProductionLite(Commodities.ORE, consumerSection.getAssignedResources(Commodities.ORE), tooltip);
        }
        tooltip.addSectionHeading("Transplutonic ore section", Alignment.MID, 5f);

        tooltip.addPara("Available transplutonic ore supply units %s ", 5f, Color.ORANGE, amountR_Ore + "");
        for (PlutoForgeSection consumerSection : getMega().getConsumerSections()) {
            consumerSection.createTooltipForResourceProductionLite(Commodities.RARE_ORE, consumerSection.getAssignedResources(Commodities.RARE_ORE), tooltip);
        }

    }

    public int getAvailableOresAmount(String idOfOre) {
        float amount = currentMagnitude * supplyUnitsPerMagnitude;
        if (!isRestored) {
            amount = 0;
        }
        int ore;
        ore = ((PlutoMegastructure) (getMegastructureTiedTo())).getAvailableResources((int) amount, idOfOre);
        return (int) ore;
    }

    @Override
    public void createTooltipForButtons(TooltipMakerAPI tooltip, String buttonId) {
        super.createTooltipForButtons(tooltip, buttonId);
        if (buttonId.equals("adjustLaser")) {
            int percent = 70;
            if (HypershuntMegastructure.isWithinReceiverSystem(this.getMega().getEntityTiedTo())) {
                percent = 100;
            }
            tooltip.addPara("We can adjust the strength of the laser for optimal excavation of resources. Currently we are able to use %s of the laser's potential.", 5f, Color.ORANGE, percent + "%");
            if (!GPManager.getInstance().getMegastructuresBasedOnClass(HypershuntMegastructure.class).isEmpty()) {
                if (HypershuntMegastructure.isWithinReceiverSystem(this.getMega().getEntityTiedTo())) {
                    tooltip.addPara("Due to the connection with a hypershunt we are now able to access the full potential of this structure.", 5f);
                } else {
                    tooltip.addPara("With a hypershunt under our control, it is advised to build a Hypershunt Receiver in this system to make use of its vast energy. Thanks to this, we will be able to unlock the full potential of this megastructure.", 5f);

                }
            }
        }
    }

    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        ButtonData data1 = new ButtonData("Adjust laser strength", this, this.isRestored, new Color(224, 42, 42, 255), "adjustLaser", new OnHoverButtonTooltip(this, "adjustLaser"), "adjustLaser", this.getSpec().getSectionID());
        currentButtons.put("adjustLaser", data1);
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("Allows the extraction of ores and transplutonic ores by using the laser", 5f);
        if (HypershuntMegastructure.isWithinReceiverSystem(this.getMegastructureTiedTo().getEntityTiedTo())) {
            tooltip.addPara("Due to the connection with a Hypershunt, the power of the laser and the extraction yield are both doubled.", Misc.getPositiveHighlightColor(), 5f);
        }
        if (isRestored) {
            int amount = 30;
            tooltip.addPara("Currently extracting %s of %s", 5f, Color.ORANGE, "" + amount, "ore");
            tooltip.addPara("Currently extracting %s of %s", 5f, Color.ORANGE, "" + amount, "transplutonic ore");
        }
    }

    @Override
    public HashMap<String, Integer> getProduction(HashMap<String, Float> penaltyMap) {
        HashMap<String, Integer> map = new HashMap<>();
//        for (Map.Entry<String, Float> s : NidavelirComplexMegastructure.commoditiesProd.entrySet()) {
//            float totalVal = s.getValue() AoTDMisc.getOrDefault(penaltyMap, AoTDCommodities.REFINED_METAL,1f);
//            AoTDMisc.putCommoditiesIntoMap(map,s.getKey(), (int) totalVal);
//
//        }

        return map;

    }
}
