package data.kaysaar.aotd.vok.scripts.specialprojects;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectStageWindow;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;

public class AoTDSpecialProject {
    public String specID;

    ArrayList<AoTDSpecialProjectStage>stages = new ArrayList<>();
    public AoTDSpecialProjectSpec getProjectSpec(){
        return SpecialProjectSpecManager.getSpec(specID);
    }
    public boolean wasCompleted =  false;
    public boolean wasInitalized = false;
    public void setSpecID(String specID) {
        this.specID = specID;

    }
    public void init(){
        wasInitalized = false;
        wasCompleted = false;
    }
    public void initAsPicked(){
        stages = new ArrayList<>();
        for (String s : getProjectSpec().getStageMap().keySet()) {
            stages.add(new AoTDSpecialProjectStage(s));
        }
    }

    public AoTDSpecialProjectStage getStage(String id ){
        for (AoTDSpecialProjectStage stage : stages) {
            if(stage.specId.equals(id)){
                return stage;
            }
        }
        return null;
    }
    public float getCreditCostsComputed(){
        return 0f;
    }
    public HashMap<String,Integer>getOtherCostsOverrideForStage(String stageId){
        return new HashMap<>();
    }
    public HashMap<String,Integer>getGPCostOverrideForStage(String stageId){
        return new HashMap<>();
    }

    public ArrayList<SpecialProjectStageWindow> getStagesForUI(CustomPanelAPI mainPanel){
        ArrayList<SpecialProjectStageWindow>windows = new ArrayList<>();
        if(wasInitalized){
            for (AoTDSpecialProjectStage spec : stages) {
                AoTDSpecialProjectStageSpec specStage = SpecialProjectSpecManager.getStageSpec(spec.specId);
                SpecialProjectStageWindow window =  new SpecialProjectStageWindow(this,spec.specId,mainPanel,specStage.getMode(),specStage.getOriginMode(),specStage.getUiCordsOfBox(),specStage.getUiCordsOnHologram());
                windows.add(window);

            }
        }
        else{
            for (String spec : getProjectSpec().getStageMap().keySet()) {
                AoTDSpecialProjectStageSpec specStage = SpecialProjectSpecManager.getStageSpec(spec);
                SpecialProjectStageWindow window =  new SpecialProjectStageWindow(this,spec,mainPanel,specStage.getMode(),specStage.getOriginMode(),specStage.getUiCordsOfBox(),specStage.getUiCordsOnHologram());
                windows.add(window);

            }
        }


        return windows;
    }

}
