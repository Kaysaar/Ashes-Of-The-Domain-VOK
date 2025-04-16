package data.kaysaar.aotd.vok.scripts.specialprojects.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectSpecManager;

public class AoTDSpecialProjectStage {
    public String specId;
    public boolean isCompleted=false;
    public float progress= 0f;
    public boolean paidForStage = false;
    public AoTDSpecialProjectStage(String specId){
        this.specId = specId;
    }
    public AoTDSpecialProjectStageSpec getSpec(){
        return SpecialProjectSpecManager.getStageSpec(specId);
    }
    public boolean isPaidForStage() {
        return paidForStage;
    }
    public float getProgressComputed(){
        return Math.min(progress/getSpec().getDays(),1f);
    }

    public void setPaidForStage(boolean paidForStage) {
        this.paidForStage = paidForStage;
    }

    public float getProgress() {
        return progress;
    }
    public boolean isCompleted() {
        if(getProgressComputed()==1){
            setCompleted(true);
        }
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
    public void advance(float amount){
        float days = Global.getSector().getClock().convertToDays(amount);
        if(Global.getSettings().isDevMode()){
            days*=10;
        }
        progress+=days;
        if(getProgressComputed()==1){
            setCompleted(true);
        }
    }
    public boolean canPayForStage(){
        boolean credits = Global.getSector().getPlayerFleet().getCargo().getCredits().get()>= getSpec().getCreditCosts();
        return credits && getSpec().getOtherCosts().stream().allMatch(x-> SpecialProjectManager.haveMetReqForItem(x.getId(),x.getAmount(),x.getCostType()));
    }
    public boolean haveMetCriteriaToStartOrResumeStage(){
        return isPaidForStage()||canPayForStage();
    }
    public void payForStage(){
        if(!paidForStage){
            setPaidForStage(true);
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(getSpec().getCreditCosts());
            getSpec().getOtherCosts().stream().forEach(x->SpecialProjectManager.eatItems(x,SpecialProjectManager.marketId, Misc.getPlayerMarkets(false)));
        }
    }
}
