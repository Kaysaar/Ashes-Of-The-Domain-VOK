package data.kaysaar.aotd.vok.scripts.specialprojects.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.SpecialProjectUnlockingIntel;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectSpecManager;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectStageWindow;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectUIManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc.createResourcePanelForSmallTooltipCondensed;

public class AoTDSpecialProject {

    public String specID;
    public float penalty;
    ArrayList<AoTDSpecialProjectStage> stages = new ArrayList<>();

    public ArrayList<String> getCurrentlyAttemptedStages() {
        return currentlyAttemptedStages;
    }

    public void setPenalty(float penalty) {
        this.penalty = penalty;
    }

    public float getPenalty() {
        return penalty;
    }

    public AoTDSpecialProjectSpec getProjectSpec() {
        return SpecialProjectSpecManager.getSpec(specID);
    }
    public String getNameOverride(){
        return getProjectSpec().getName();
    }
    public ArrayList<String> currentlyAttemptedStages = new ArrayList<>();

    public float getCombinedWeight() {
        float weight = 0;
        for (AoTDSpecialProjectStage stage : stages) {
            weight += this.getProjectSpec().getStageMap().get(stage.getSpec().getId());
        }
        return weight;
    }
    public boolean wasEverDiscovered = false;
    public void sentProjectUnlockNotification(){

    }

    public boolean wasEverDiscovered() {
        return wasEverDiscovered;
    }

    public void setWasEverDiscovered(boolean wasEverDiscovered) {
        if(wasEverDiscovered &&!this.wasEverDiscovered){
            sentProjectUnlockNotification();
        }
        this.wasEverDiscovered = wasEverDiscovered;
    }

    public boolean shouldShowOnUI(){
        return true;
        //return wasEverDiscovered();
    }
    public float getProgressForStage(String stageId) {
        return getStage(stageId).getProgressComputed();
    }

    public float getGainedWeight() {
        float weight = 0f;
        for (AoTDSpecialProjectStage stage : stages) {
            weight += (this.getProjectSpec().getStageMap().get(stage.getSpec().getId())) * stage.getProgressComputed();
        }
        return weight;
    }


    public boolean wasCompleted = false;

    public void setSpecID(String specID) {
        this.specID = specID;

    }

    public void projectCompleted() {

    }

    public float getTotalProgress() {
        return Math.min(1, getGainedWeight() / getCombinedWeight());
    }

    public boolean checkIfProjectWasCompleted() {
        return getTotalProgress() == 1;
    }


    public void init() {
        stages = new ArrayList<>();
        for (String s : getProjectSpec().getStageMap().keySet()) {
            stages.add(new AoTDSpecialProjectStage(s));
        }
    }

    public HashMap<String, Integer> getGpCostFromStages() {
        HashMap<String, Integer> comodities = new HashMap<>();
        for (String currentlyAttemptedStage : currentlyAttemptedStages) {
            getStage(currentlyAttemptedStage).getSpec().getGpCost().forEach((key, value) -> AoTDMisc.putCommoditiesIntoMap(comodities, key, value));

        }
        return comodities;
    }
    public void applyBonusesFromSkills(HashMap<String,Integer>gpCost){

    }
    public void update() {
        for (String s : getProjectSpec().getStageMap().keySet()) {
            if (stages.stream().noneMatch(x -> x.specId.equals(s))) {
                stages.add(new AoTDSpecialProjectStage(s));
            }
        }
        Iterator<AoTDSpecialProjectStage> it = stages.iterator();
        while (it.hasNext()) {
            AoTDSpecialProjectStage stage = it.next();
            if (!getProjectSpec().getStageMap().containsKey(stage.specId)) {
                it.remove();
            }
        }
        if (currentlyAttemptedStages == null) currentlyAttemptedStages = new ArrayList<>();
        Iterator<String> its = currentlyAttemptedStages.iterator();
        while (its.hasNext()) {
            String stage = its.next();
            if (!getProjectSpec().getStageMap().containsKey(stage)) {
                its.remove();
            }
        }

    }

    public void doCheckForProjectUnlock() {
        if(!wasEverDiscovered()){
            if( checkIfProjectShouldUnlock()){
                setWasEverDiscovered(true);
                createIntelForUnlocking();
            }

        }
    }
    public void createIntelForUnlocking() {
        SpecialProjectUnlockingIntel intel = new SpecialProjectUnlockingIntel(this);
        Global.getSector().getIntelManager().addIntel(intel);
    }
    public boolean checkIfProjectShouldUnlock(){
        return false;
    }
    public void advance(float amount) {
        for (String currentlyAttemptedStage : currentlyAttemptedStages) {
            getStage(currentlyAttemptedStage).advance(amount*penalty);
        }
        if(getTotalProgress()==1f){
            wasCompleted = true;
            grantReward();
            sentFinishNotification();
            SpecialProjectManager.getInstance().setCurrentlyOnGoingProject(null);
        }
    }
    public void sentFinishNotification(){

    }
    public void grantReward(){

    }


    public AoTDSpecialProjectStage getStage(String id) {
        for (AoTDSpecialProjectStage stage : stages) {
            if (stage.specId.equals(id)) {
                return stage;
            }
        }
        return null;
    }

    public float getCreditCostsComputed() {
        return 0f;
    }

    public ArrayList<OtherCostData> getOtherCostsOverrideForStage(String stageId) {
        return getStage(stageId).getSpec().getOtherCosts();
    }

    public HashMap<String, Integer> getGPCostOverrideForStage(String stageId) {
        return getStage(stageId).getSpec().getGpCost();
    }

    public ArrayList<SpecialProjectStageWindow> getStagesForUI(CustomPanelAPI mainPanel, SpecialProjectUIManager manager ) {
        ArrayList<SpecialProjectStageWindow> windows = new ArrayList<>();
        for (AoTDSpecialProjectStage spec : stages) {

            SpecialProjectStageWindow window = new SpecialProjectStageWindow(this, spec, mainPanel, spec.getSpec().getMode(), spec.getSpec().getOriginMode(), spec.getSpec().getUiCordsOfBox(), spec.getSpec().getUiCordsOnHologram(),manager);
            windows.add(window);

        }


        return windows;
    }

    public ArrayList<AoTDSpecialProjectStage> getStages() {
        return stages;
    }

    public void createDetailedTooltipForButton(TooltipMakerAPI tooltip, float width) {
        createTooltipForButton(tooltip, width+100,false);
        for (AoTDSpecialProjectStage stagesSpec : this.getStages()) {
            tooltip.addCustom(createSubStageProgressMoved(width-10 , stagesSpec.getSpec().getId(), 10), 2f);

        }
        tooltip.addPara(this.getProjectSpec().getDescription(), 5f);

        tooltip.addSectionHeading("Effects upon project completion", Misc.getDarkHighlightColor(), null, Alignment.MID, width, 3f);
        createRewardSection(tooltip, width);
    }
    public void shouldAppearOnUI(){

    }
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain " + Global.getSettings().getHullSpec("uaf_supercap_slv_core").getHullNameWithDashClass(), Misc.getPositiveHighlightColor(), 5f);
    }
    public void printSpecialization(TooltipMakerAPI tooltip){
        tooltip.addPara("Project type : %s",5f, Color.ORANGE,getSpecialization());
    }
    public String getSpecialization(){
        if(getProjectSpec().hasTag("ship_enginnering")){
            return "Ship Engineering";
        }
        if(getProjectSpec().hasTag("physics")){
            return "Physics";
        }
        if(getProjectSpec().hasTag("computers")){
            return "Computers";
        }
        return "";
    }

    public void createTooltipForButton(TooltipMakerAPI tooltip, float width,boolean smallButton) {
        tooltip.setTitleFont(Fonts.ORBITRON_16);

        tooltip.addTitle(this.getNameOverride());
        if(!smallButton){
            tooltip.addSectionHeading("Upkeep", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, width - 110, 5f);

            tooltip.addCustom(createResourcePanelForSmallTooltipCondensed(width - 110, 20, 20, new HashMap<>(), new HashMap<>()), 5f);
        }
        else{
            printSpecialization(tooltip);
        }

        ProgressBarComponent component = new ProgressBarComponent(width - 110, 18, getTotalProgress(), Misc.getBasePlayerColor().darker().darker());
        tooltip.addCustom(component.getRenderingPanel(), 5);

        LabelAPI labelAPI = tooltip.addSectionHeading(""+(int)(this.getTotalProgress()*100)+"%", Misc.getTextColor(), null, Alignment.MID, width - 110, -18f);

    }

    public CustomPanelAPI createSubStageProgressMoved(float width, String stageID, float opadX) {
        CustomPanelAPI test = Global.getSettings().createCustom(width, 1, null);
        CustomPanelAPI t = createSubStageProgress(width - opadX, 1, stageID);

        test.addComponent(t).inTL(opadX, 0);
        test.getPosition().setSize(width, t.getPosition().getHeight());
        return test;
    }

    public CustomPanelAPI createSubStageProgress(float width, float height, String stageID) {
        CustomPanelAPI test = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = test.createUIElement(width, height, false);
        tooltip.addSectionHeading(getStage(stageID).getSpec().getName(), Misc.getBasePlayerColor(), null, Alignment.MID, width, 0f);
        ProgressBarComponent component = new ProgressBarComponent(width - 5, 11, getProgressForStage(stageID), Misc.getBasePlayerColor().darker().darker());
        tooltip.addCustom(component.getRenderingPanel(), 2f);
        test.getPosition().setSize(width, tooltip.getHeightSoFar());
        test.addUIElement(tooltip).inTL(0, 0);
        return test;
    }

    public boolean isStageButtonEnabled(String stageId) {
        return true;
    }
    public boolean isProjectEnabled(){
        return !SpecialProjectManager.getInstance().isCurrentOnGoing(this);
    }
}
