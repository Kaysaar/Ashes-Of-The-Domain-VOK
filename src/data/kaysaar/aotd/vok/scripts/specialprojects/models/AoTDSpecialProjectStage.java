package data.kaysaar.aotd.vok.scripts.specialprojects.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectSpecManager;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public LinkedHashMap<String,Integer> delivered = new LinkedHashMap<String,Integer>();

    public LinkedHashMap<String, Integer> getDelivered() {
        return delivered;
    }
    public float getDaysLeft(){
        return getSpec().getDays() - progress;
    }

    public int takeResources(int available, String commodityId) {
        if (available <= 0) return 0;

        Integer requiredPerUnit = getSpec().getGpCost().get(commodityId);
        if (requiredPerUnit == null || requiredPerUnit <= 0) return 0;

        int totalRequired = requiredPerUnit;
        int alreadyDelivered = delivered.getOrDefault(commodityId, 0);
        int remainingNeeded = totalRequired - alreadyDelivered;

        if (remainingNeeded <= 0) return 0;

        int taken = Math.min(available, remainingNeeded);
        delivered.put(commodityId, alreadyDelivered + taken);
        return taken;
    }
    public float getMinAllowedProgress(){
        float curr = 1f;
        for (Map.Entry<String, Integer> entry : getSpec().getGpCost().entrySet()) {
            int amDelivered = delivered.getOrDefault(entry.getKey(), 0);
            float progress = (float) amDelivered /entry.getValue();
            if(amDelivered>=entry.getValue()){
                amDelivered = 1;
            }
            curr = Math.min(progress, curr);
        }
        return curr;
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
        if(getProgressComputed()>getMinAllowedProgress()&&getProgressComputed()!=1){
            return;
        }
        progress+=days;
        if(getProgressComputed()==1){
            setCompleted(true);
        }
    }
    public boolean canPayForStage(){
        boolean credits = Global.getSector().getPlayerFleet().getCargo().getCredits().get()>= getSpec().getCreditCosts();
        return credits && getSpec().getOtherCosts().stream().allMatch(x-> BlackSiteProjectManager.haveMetReqForItem(x.getId(),x.getAmount(),x.getCostType()));
    }
    public boolean haveMetCriteriaToStartOrResumeStage(){
        return isPaidForStage()||canPayForStage();
    }
    public void payForStage(){
        if(!paidForStage){
            setPaidForStage(true);
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(getSpec().getCreditCosts());
            getSpec().getOtherCosts().stream().forEach(x-> BlackSiteProjectManager.eatItems(x, BlackSiteProjectManager.marketId, Misc.getPlayerMarkets(false)));
        }
    }
}
