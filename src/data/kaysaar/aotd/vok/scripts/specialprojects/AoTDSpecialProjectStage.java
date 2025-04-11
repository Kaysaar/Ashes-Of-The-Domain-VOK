package data.kaysaar.aotd.vok.scripts.specialprojects;

import com.fs.starfarer.api.Global;

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
        progress+=days;
        if(getProgressComputed()==1){
            setCompleted(true);
        }
    }
}
