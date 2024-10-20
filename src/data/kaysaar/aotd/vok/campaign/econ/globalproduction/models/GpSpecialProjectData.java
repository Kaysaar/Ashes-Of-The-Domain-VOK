package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FactionProductionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.intel.SpecialProjectFinishedIntel;
import com.fs.starfarer.api.impl.campaign.intel.SpecialProjectUnlockingIntel;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GpSpecialProjectData {
    String specID;
    public GPSpec getSpec() {
        for (GPSpec specialProjectSpec : GPManager.getInstance().getSpecialProjectSpecs()) {
            if(this.specID.equals(specialProjectSpec.getProjectId())){
                return specialProjectSpec;
            }
        }
        return null;
    }

    public void setSpecID(String specID) {
        this.specID = specID;
    }

    public int getCurrentStage() {
        return currentStage;
    }
    public boolean showedInfoAboutUnlocking = false;

    public boolean isShowedInfoAboutUnlocking() {
        return showedInfoAboutUnlocking;
    }

    public void setShowedInfoAboutUnlocking(boolean showedInfoAboutUnlocking) {
        this.showedInfoAboutUnlocking = showedInfoAboutUnlocking;
    }

    public GpSpecialProjectData(String spec){
        this.specID = spec;
    }

    public boolean havePaidInitalCost = false;
    float totalDaysSpent = 0f;
    int currentStage = -1;
    float daysSpentOnStage = 0f;
    public boolean canShow = false;

    public boolean isMainFocus(){
        return GPManager.getInstance().getCurrProjOnGoing()!=null&&GPManager.getInstance().getCurrProjOnGoing().getSpec().getProjectId().equals(this.getSpec().getProjectId());
    }
    public String getStatusString(){
        if(!hasStarted){
            return "Not started";
        } else if (isFinished()) {
            return "Finished";
        }
        else if (isMainFocus()){
            return "On-going";
        }
        else{
            return  "Paused";
        }
    }
    public float getProgressOfStage(int stage){
        if(currentStage==-1){
            currentStage = 0;
        }
        if(stage<currentStage){
            return 1;
        }
        if(stage>currentStage){
            return  0;
        }
        float percent = daysSpentOnStage/ getSpec().getDaysPerStage().get(stage);
        if(percent>=1){
            percent =1;

        }
        return percent;
    }
    public float getCurrentProgressOfStage(){
        return getProgressOfStage(currentStage);
    }
    public int getCurrentProgressOfStagePercent(){
        return getProgressOfStagePercent(currentStage);
    }
    public int getProgressOfStagePercent(int stage){
        if(stage<currentStage){
            return 100;
        }
        if(stage>currentStage){
            return  0;
        }
        float percent = daysSpentOnStage/ getSpec().getDaysPerStage().get(stage);
        if(percent>=1){
            percent =1;

        }
        percent*=100;
        return (int)percent;
    }

    public Color getStatusColor(){
        if(!hasStarted){
            return Misc.getNegativeHighlightColor();
        } else if (isFinished()) {
            return Misc.getPositiveHighlightColor();
        }
        else if (GPManager.getInstance().getCurrProjOnGoing()!=null&&GPManager.getInstance().getCurrProjOnGoing().getSpec().getProjectId().equals(this.getSpec().getProjectId())){
            return Color.ORANGE;
        }
        else{
            return Misc.getGrayColor();
        }
    }
    public boolean canSupportStageConsumption(HashMap<String, Integer> availableResources) {
        if(availableResources==null)return false;
        if(currentStage>= getSpec().amountOfStages){
            return false;
        }
        boolean allRes = true;
        for (Map.Entry<String, Integer> entry : getSpec().getStageSupplyCost().get(currentStage).entrySet()) {
            if(availableResources.get(entry.getKey())==0){
                allRes = false;
                break;
            }
        }

        return allRes;
    }
    public boolean isDiscovered(){
        boolean allIsTrue = true;;
        if(!getSpec().isDiscoverable){
            return false;
        }
        else {
            if(getSpec().getMemFlagsToMetForDiscovery().isEmpty()){
                return Global.getSector().getPlayerFaction().knowsShip(getSpec().rewardId);
            }
            else{
                for (String s : getSpec().getMemFlagsToMetForDiscovery()) {
                    if(!Global.getSector().getPlayerFaction().getMemory().is(s,true)){
                        allIsTrue = false;
                        break;
                    }
                }
                return allIsTrue;
            }
        }
    }

    public void setCanShow(boolean canShow) {
        this.canShow = canShow;
    }

    public boolean hasStarted = false;

    public HashMap<String, Integer> retrieveCostForCurrStage() {
        return getSpec().getStageSupplyCost().get(currentStage);
    }

    public int getDurationOfStage(int stage) {
        return getSpec().getDaysPerStage().get(stage);
    }

    public boolean isFinished() {
        return currentStage >= getSpec().getAmountOfStages();
    }

    public boolean haveRecivedAward = false;

    public float getReqTotalDaysToProgress(int stage) {
        int index = 0;
        float totaldays = 0;
        for (Integer integer : this.getSpec().getDaysPerStage()) {
            if (index >= stage) {
                totaldays += integer;
                break;
            }
            totaldays += integer;
            index++;
        }
        return totaldays;
    }
    public float getTotalProgress(){
        float totality = 0;
        for (Integer integer :this.getSpec().getDaysPerStage()) {
            totality+=integer;
        }
        float per = totalDaysSpent/totality;
        if(per>=1){
            per =1;
        }
        return per;

    }
    public int getTotalProgressPercent(){
        float totality = 0;
        for (Integer integer :this.getSpec().getDaysPerStage()) {
            totality+=integer;
        }
        float per = totalDaysSpent/totality;
        if(per>=1){
            per =1;
        }
        per*=100;
        return (int)per;

    }
    public float penalty=1;

    public void setPenalty(float penalty) {
        this.penalty = penalty;
    }

    public void advance(float amount) {

        if(!hasStarted)return;
        if(!isMainFocus())return;
        if (isFinished()) {
            return;
        }
        float days =  Global.getSector().getClock().convertToDays(amount);

        if(AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.MEGA_ASSEMBLY_SYSTEMS)){
            days*=2;
        }
        if(Global.getSettings().isDevMode()){
            days*=20;
        }
        if(penalty<1){
            days*= penalty*0.5f;
        }
        if(penalty>=1){
            penalty=1;
        }

        totalDaysSpent += days;
        daysSpentOnStage+=days;
        if (totalDaysSpent >= getReqTotalDaysToProgress(currentStage)) {
            currentStage++;
            if (isFinished()) {
                if (!haveRecivedAward) {
                    haveRecivedAward = true;
                    FactionAPI pf = Global.getSector().getPlayerFaction();
                    FactionProductionAPI prod = pf.getProduction();

                    MarketAPI gatheringPoint = prod.getGatheringPoint();
                    if (gatheringPoint == null) return;
                    CargoAPI local = Misc.getStorageCargo(gatheringPoint);
                    SpecialProjectFinishedIntel intel = new SpecialProjectFinishedIntel(this);
                    Global.getSector().getIntelManager().addIntel(intel, false);
                    getSpec().getListenerFromPlugin().receiveReward(Global.getSettings().getHullSpec(getSpec().rewardId),local);

                    GPManager.getInstance().setCurrentFocus(null);


                }
                daysSpentOnStage = 0f;
                return;
            }

        }


    }

}
