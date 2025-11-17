package data.kaysaar.aotd.vok.scripts.specialprojects.models;

import ashlib.data.plugins.ui.models.ProgressBarComponentV2;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.ProjectStageCompletionIntel;
import com.fs.starfarer.api.impl.campaign.intel.SpecialProjectFinishedIntel;
import com.fs.starfarer.api.impl.campaign.intel.SpecialProjectUnlockingIntel;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectCompletionListener;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectSpecManager;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectStageWindow;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectUIManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.GPUIMisc.createResourcePanelForSmallTooltipCondensed;

public class AoTDSpecialProject {

    public String specID;
    public float penalty;
    ArrayList<AoTDSpecialProjectStage> stages = new ArrayList<>();
    public int countOfCompletion = 0;
    public ArrayList<String> getCurrentlyAttemptedStages() {
        return currentlyAttemptedStages;
    }

    public int getCountOfCompletion() {
        return countOfCompletion;
    }
    public void restartProject(){
        currentlyAttemptedStages.clear();
        wasCompleted = false;
        for (AoTDSpecialProjectStage stage : stages) {
            stage.setCompleted(false);
            stage.setProgress(0f);
            stage.setPaidForStage(false);
        }
    }
    public void printAdditionalReqForStage(TooltipMakerAPI tooltip,String stageId){

    }
    public boolean canAttemptStage(String stageID){
        return true;
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
    public boolean wasEverDiscovered() {
        return wasEverDiscovered;
    }

    public void setWasEverDiscovered(boolean wasEverDiscovered) {
        if(wasEverDiscovered &&!this.wasEverDiscovered){
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
        if(!Global.getSettings().getModManager().isModEnabled(getProjectSpec().getModId()))return;
        if(!wasEverDiscovered()){
            if( checkIfProjectShouldUnlock()){
                setWasEverDiscovered(true);
                createIntelForUnlocking();
            }

        }
    }
    public boolean doCheckForBlacksiteUnlock() {
        if(!Global.getSettings().getModManager().isModEnabled(getProjectSpec().getModId()))return false;
        if(!wasEverDiscovered()){
            if( checkIfProjectShouldUnlock()){
                return true;
            }

        }
        return false;
    }
    public void createIntelForUnlocking() {
        SpecialProjectUnlockingIntel intel = new SpecialProjectUnlockingIntel(this);
        Global.getSector().getIntelManager().addIntel(intel);
    }
    public boolean checkIfProjectShouldUnlock(){
        return false;
    }
    public void advance(float amount) {
        float days = amount*penalty;
        float additionalDays = days* AoTDMainResearchManager.getInstance().getManagerForPlayer().getBlackSiteSpecialProjBonus().getModifiedValue();
        for (String currentlyAttemptedStage : currentlyAttemptedStages) {
            getStage(currentlyAttemptedStage).advance(days+additionalDays);
        }
        ArrayList<String>forNotifitcations = new ArrayList<>();
        for (AoTDSpecialProjectStage stage : stages) {
            if(stage.isCompleted()){
                if(currentlyAttemptedStages.contains(stage.getSpec().getId())){
                    forNotifitcations.add(stage.getSpec().getId());
                }
                currentlyAttemptedStages.remove(stage.getSpec().getId());

            }
        }
        if(getTotalProgress()==1f&&!wasCompleted){
            wasCompleted = true;
            countOfCompletion++;

            Object reward = grantReward();
            Global.getSector().getListenerManager().getListeners(SpecialProjectCompletionListener.class).forEach(x->x.completedProject(this.specID,reward));
            sentFinishNotification();
            projectCompleted();
            forNotifitcations.clear();
            BlackSiteProjectManager.getInstance().setCurrentlyOnGoingProject(null);
        }
        forNotifitcations.forEach(x->Global.getSector().getIntelManager().addIntel(new ProjectStageCompletionIntel(this,getStage(x))));
        forNotifitcations.clear();
    }
    public boolean canDoProject(){
        return countOfCompletion==0||getProjectSpec().hasTag("repeatable");
    }
    public void sentFinishNotification(){
        SpecialProjectFinishedIntel intel = new SpecialProjectFinishedIntel(this);
        Global.getSector().getIntelManager().addIntel(intel);
    }
    public Object grantReward(){
        return null;
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
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
    }
    public void createRewardSectionForInfo(TooltipMakerAPI tooltip, float width) {
        createRewardSection(tooltip,width);
        MarketAPI market  = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if(market==null)market = Misc.getPlayerMarkets(true).get(0);
        tooltip.addPara("Price is located in local storage of "+market.getName(),5f);
    }
    public void printSpecialization(TooltipMakerAPI tooltip){
        AoTDSpecializationSpec spec = getSpecialization();
        if(spec!=null){
            tooltip.addPara("Project type : %s",5f, spec.getColorOfString(),spec.getName());
        }

    }
    public AoTDSpecializationSpec getSpecialization(){
        return SpecialProjectSpecManager.getSpecializations().stream().filter(s -> getProjectSpec().hasTag(s.getId())).findFirst().orElse(null);

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

        ProgressBarComponentV2 component = new ProgressBarComponentV2(width - 110, 18, getTotalProgress(), Misc.getBasePlayerColor().darker().darker());

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
        ProgressBarComponentV2 component = new ProgressBarComponentV2(width - 5, 11, getProgressForStage(stageID), Misc.getBasePlayerColor().darker().darker());
        tooltip.addCustom(component.getRenderingPanel(), 2f);
        test.getPosition().setSize(width, tooltip.getHeightSoFar());
        test.addUIElement(tooltip).inTL(0, 0);
        return test;
    }

    public boolean isStageButtonEnabled(String stageId) {
        return true;
    }
    public boolean isProjectEnabled(){
        return !BlackSiteProjectManager.getInstance().isCurrentOnGoing(this);
    }
}
