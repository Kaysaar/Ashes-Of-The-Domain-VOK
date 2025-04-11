package data.kaysaar.aotd.vok.scripts.specialprojects;

public class AoTDSpecialProjectStage {
    public String specId;
    public boolean isCompleted=false;
    public float progress= 0f;
    public boolean paidForStage = false;
    public AoTDSpecialProjectStage(String specId){
        this.specId = specId;
    }


}
