package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.intel.MegastructureSectionCompletedIntel;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDListenerUtilis;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GPMegaStructureSection {
    public String specID;
    public GPBaseMegastructure megastructureTiedTo;
    public MutableStat upkeepMult = new MutableStat(1f);
    public String getName() {
        return getSpec().getName();
    }
    public float getPenaltyFromManager(String ... resources){
        return GPManager.getInstance().getTotalPenaltyFromResources(resources);
    }
    public void init(GPBaseMegastructure megastructureTiedTo, boolean isRestored) {
        progressOfRestoration = 0f;
        this.megastructureTiedTo = megastructureTiedTo;
        this.isRestored = isRestored;
        if (isRestored) {
            progressOfRestoration = 1f;
        }
    }

    public float progressOfRestoration;
    public boolean isRestored;
    public boolean isRestoring;
    public float penaltyFromLackOfResources=1f;

    boolean isAboutToBeRemoved = false;

    public void apply() {
        if (!isAboutToBeRemoved) {
            if (isRestored) {
                applyEffectOfSection();
            }
        }
        AoTDListenerUtilis.applyUpkeepReductionCredits(this,upkeepMult);

    }

    public float getUpkeep() {
        upkeepMult.unmodify();
        float upkeep = getSpec().getRunningCost();
        if(isRestoring){
            upkeep= getSpec().getRenovationCost();
        }
        upkeep+=addAdditionalUpkeep();
        for (GPMegaStructureSection megaStructureSection : megastructureTiedTo.getMegaStructureSections()) {
            megaStructureSection.applyReductionOfUpkeep(upkeepMult);
        }
        AoTDListenerUtilis.applyUpkeepReductionCredits(this,upkeepMult);
        return upkeep*upkeepMult.getModifiedValue();
    }
    public void applyReductionOfUpkeep(MutableStat statToChange){

    }
    public float addAdditionalUpkeep(){
        return 0f;
    }

    public HashMap<String, Integer> getGPUpkeep() {
        HashMap<String,Integer> costs = new LinkedHashMap<>();
        if (isRestoring) {
            costs.putAll(getSpec().getGpRestorationCost());
        }
        else if(!isRestored){
            costs.putAll(getSpec().getGpUpkeepOfSection());
        }
        else {
            costs.putAll(getSpec().getGpAfterRestorationCost());
        }
        applyAdditionalGPCost(costs);
        AoTDListenerUtilis.applyUpkeepReductionForGP(this,costs);
        return costs;
    }
    public void applyAdditionalGPCost(HashMap<String,Integer> map){

    }

    public GpMegaStructureSectionsSpec getSpec() {
        return GPManager.getInstance().getMegaSectionSpecFromList(this.specID);
    }

    public GPBaseMegastructure getMegastructureTiedTo() {
        return megastructureTiedTo;
    }

    public void advance(float amount) {
        penaltyFromLackOfResources =1;
        unapply();
        apply();
        if (isRestoring) {
            progressOfRestoration += Global.getSector().getClock().convertToDays(amount) * getPenaltyFromManager(getGPUpkeep().keySet().toArray(new String[0]));
            if(Global.getSettings().isDevMode()){
                progressOfRestoration*=100;
            }
            if (progressOfRestoration >= getSpec().daysForRenovation) {
                isRestoring = false;
                isRestored = true;
                Global.getSector().getPlayerFleet().getCommanderStats().addStoryPoints(1);
                aboutToReconstructSection();
                Global.getSector().getIntelManager().addIntel(new MegastructureSectionCompletedIntel(this));

            }
        }
    }
    public float getProgressPercentage() {
        float baseDays = getSpec().daysForRenovation;
        if (baseDays <= 1) baseDays = 1;

        // Calculate progress using penalized time
        float progressPercentage = progressOfRestoration / (baseDays);

        // Ensure the progress does not exceed 100%
        return Math.min(progressPercentage, 1.0f);
    }
    public void aboutToReconstructSection(){

    }
    public void unapply() {
        upkeepMult.unmodify();
        unapplyEffectOfSection();
    }
    public HashMap<String,Integer>getProduction(){
        HashMap<String,Integer>production = new HashMap<>();
        return production;
    }

    public void aboutToGetRemoved() {
        this.isAboutToBeRemoved = true;
        unapply();
    }

    public void applyEffectOfSection() {

    }

    public void unapplyEffectOfSection() {

    }

    public void createTooltipForOtherInfoSection(TooltipMakerAPI tooltip, float width) {
        createTooltipForBenefits(tooltip);
        if (isRestoring) {
            tooltip.addPara("Restored to %s capacity", 5f, Color.ORANGE, (int) (getProgressPercentage() * 100) + "%");
            ProgressBarComponent component = new ProgressBarComponent(width-25,21,getProgressPercentage(), Misc.getDarkPlayerColor().brighter().brighter());
            tooltip.addCustom(component.getRenderingPanel(),5f);
        }
        createProductionSection(tooltip,width);
        createUpkeepSection(tooltip,width);
        addPenaltyFromLackOfResourcesInfo(tooltip,width);

    }
    public void createProductionSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addSectionHeading("Production",Alignment.MID,5f);
        tooltip.addCustom(MegastructureUIMisc.createResourcePanelForSmallTooltip(width, 20, 20, getProduction(), Misc.getPositiveHighlightColor()), 5f);


    }
    public void createUpkeepSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addSectionHeading("Upkeep",Alignment.MID,5f);
        tooltip.addPara("Current monthly upkeep : %s", 5f, Color.ORANGE, Misc.getDGSCredits(getUpkeep()));
        tooltip.addCustom(MegastructureUIMisc.createResourcePanelForSmallTooltip(width, 20, 20, getGPUpkeep(), null), 5f);


    }
    public void addPenaltyFromLackOfResourcesInfo(TooltipMakerAPI tooltip, float width) {;

    }
    public LinkedHashMap<String, ButtonData> generateButtons() {
        LinkedHashMap<String, ButtonData> buttons = new LinkedHashMap<>();


        if (isRestoring && !isRestored) {
            ButtonData data1 = new ButtonData("Pause restoration", this, true, Color.ORANGE, "pauseRestore", new OnHoverButtonTooltip(this, "pauseRestore"), "pauseRestore", this.getSpec().getSectionID());
            buttons.put("pauseRestore", data1);
        } else {
            String restoration = "Restore section";
            if (progressOfRestoration > 0f) {
                restoration = "Continue Restoration";
            }
            if(!isRestored){
                ButtonData data = new ButtonData(restoration, this, true, Color.ORANGE, "restore", new OnHoverButtonTooltip(this, "restore"), "restore", this.getSpec().getSectionID());
                buttons.put("restore", data);
            }

        }
        addButtonsToList(buttons);

        return buttons;
    }

    public void addButtonsToList(LinkedHashMap<String,ButtonData>currentButtons) {

    }
    public void startReconstruction() {
        this.isRestoring = true;
    }

    public void pauseReconstruction() {
        this.isRestoring = false;
    }

    public IntelInfoPlugin notifyAboutCompletion() {
        return null;
    }

    public void createTooltipForButtons(TooltipMakerAPI tooltip, String buttonId) {
        if (buttonId.equals("restore")) {
            tooltip.addPara("Currently this section is in ruins, but with enough effort and resources can be restored", 5f);
            createTooltipForBenefits(tooltip);
        }
        if (buttonId.equals("pauseRestore")) {
            tooltip.addPara("Restoration will be halted, so we can use our resources elsewhere.", 5f);
        }
    }

    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        if (isRestored) {
            tooltip.addSectionHeading("Effects of " + this.getName(), Alignment.MID, 5f);

        } else {
            tooltip.addSectionHeading("Effects upon restoration", Alignment.MID, 5f);

        }

    }


}
