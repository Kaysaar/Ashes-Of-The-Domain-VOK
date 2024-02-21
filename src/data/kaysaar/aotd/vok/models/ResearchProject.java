package data.kaysaar.aotd.vok.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.ui.AoTDResearchUIDP;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public abstract class ResearchProject {
    public String id;
    public ArrayList<SpecialProjectStage>stages = new ArrayList<>();
    public float currentValueOfOptions = 0;
    public ResearchProjectSpec spec;
    public float currentProgress = 0.0f;
    public ArrayList<String>researchLog = new ArrayList<>();
    public boolean haveDoneIt = false;
    public boolean currentlyOngoing = false;
    public boolean haveMetReqOnce = false;// We do this when player met at least criteria for one second, so it wont be overwritten in instant
    public ArrayList<String>optionsTakenIds= new ArrayList<>();
    public float totalDays  = calculateTotalDays();
    public boolean haveReachedCriticalMoment = false;
    public int indexOfCurrentStage = 0;

    public void setCurrentlyOngoing(boolean currentlyOngoing) {
        this.currentlyOngoing = currentlyOngoing;
    }

    public float calculateTotalDays() {
        float toReturn = 0f;
        for (SpecialProjectStage stage : stages) {
            toReturn+=stage.durationOfStage;
        }
        return toReturn;
    }
    private float calculatePercentOfCertainStage(int indexOfStage){
        return stages.get(indexOfStage).durationOfStage/totalDays;
    }
    public void generateDescriptionForCriticalMoment(TooltipMakerAPI tooltipMakerAPI){

    }
    public void generateTooltipInfoForProject(TooltipMakerAPI tooltipMakerAPI){

    }
    public void generateDescriptionForCurrentResults(TooltipMakerAPI tooltipMakerAPI){

    }
    public void generateTooltipForOption(String optionId, TooltipMakerAPI tooltip){
        tooltip.addSectionHeading(retrieveNameOfOption(optionId), Alignment.MID,0f);
    }
    public float calculateProgress(){
        return ((currentProgress/calculateTotalDays()));
    }
    public boolean haveMetReqForProjectToAppear(){
        return true;
    }
    public boolean haveMetReqForProject(){
        return true;
    }
    public String retrieveNameOfOption(String id){
        for (SpecialProjectStage stage : stages) {
            if(stage.optionsNameMap.get(id)!=null){
                return stage.optionsNameMap.get(id);
            }
        }
        return null;
    }
    public boolean haveMetReqForOption(String optionId){
        return true;
    }
    public void payForOption(String optionId){};
    public void applyOptionResults(String optionId){
        stages.get(indexOfCurrentStage).chosenOption = optionId;
        payForOption(optionId);
        currentValueOfOptions+= stages.get(indexOfCurrentStage).optionsForStage.get(optionId);
        if(indexOfCurrentStage<stages.size()) indexOfCurrentStage++;
        haveReachedCriticalMoment = false;
    }
    public HashMap<String,String>retrieveOptionsFromStage(int indexOfStage){
        return stages.get(indexOfStage).optionsNameMap;
    }

    public void applyProjectOutcomeWhenCompleted(){
        haveDoneIt = true;
        currentlyOngoing=false;
        AoTDMainResearchManager.getInstance().setCurrentProject(null);
    }
    public void payForProjectIfNecessary(){

    }
    public void startResearchProject(){
        reset();
        payForProjectIfNecessary();
        currentlyOngoing=true;
        indexOfCurrentStage = 0;
        AoTDMainResearchManager.getInstance().setCurrentProject(this);

    }
    public void advance(float amount){
        if(haveMetReqForProjectToAppear()){
            if(!haveMetReqOnce){
                haveMetReqOnce = true;
                MessageIntel intel = new MessageIntel("New Special Project Available!", Misc.getTooltipTitleAndLightHighlightColor());
                intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.INTERACTION_DIALOG,new AoTDResearchUIDP());
            }

        }
        if(currentlyOngoing&&!haveReachedCriticalMoment&&!haveDoneIt){
            currentProgress+= Global.getSector().getClock().convertToDays(amount);
        }
        if(currentProgress>=calculateInterval()&&!haveReachedCriticalMoment&&indexOfCurrentStage<stages.size()){
            haveReachedCriticalMoment = true;
            MessageIntel intel = new MessageIntel("Your attention is  immediately required towards  "+spec.nameOfProject+" project!", Misc.getNegativeHighlightColor());
            intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
            intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
            Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.INTERACTION_DIALOG,new AoTDResearchUIDP());

        }
        if(currentProgress>=calculateTotalDays()&&!haveDoneIt){
            applyProjectOutcomeWhenCompleted();
        }

    }
    public void reset(){
        haveDoneIt = false;
        currentProgress = 0.0f;
        indexOfCurrentStage = 0;
        for (SpecialProjectStage stage : stages) {
            stage.chosenOption=null;
        }
    }
    public int calculateInterval(){
        int daysToInterval = 0;
        for (SpecialProjectStage stage : stages) {
            if(indexOfCurrentStage>=stage.numberOfStage){
                daysToInterval+=stage.durationOfStage- MathUtils.getRandomNumberInRange(1,10);
            }
        }
        return daysToInterval;
    }
   public void init (ResearchProjectSpec spec){
       for (SpecialProjectStage stage : spec.getStages()) {
           SpecialProjectStage stageCopy = new SpecialProjectStage();
           stageCopy.optionsNameMap = stage.optionsNameMap;
           stageCopy.optionsForStage = stage.optionsForStage;
           stageCopy.durationOfStage = stage.durationOfStage;
           stageCopy.numberOfStage = stage.numberOfStage;
           stages.add(stageCopy);

       }
       id = spec.getId();
       this.spec = spec;

   }
   public SpecialProjectStage getCertainStage(int index){
        if(index>=stages.size())return null;
        return stages.get(index);
   }



}
