package data.kaysaar.aotd.vok.scripts.research.models;

import java.util.HashMap;
import java.util.Map;

public class SpecialProjectStage {
    public HashMap<String,Integer> optionsForStage = new HashMap<>();
    public String chosenOption = null;
    public  int numberOfStage;
    public HashMap<String, String>optionsNameMap = new HashMap<>();

    public float durationOfStage = 0f;
    public void setChosenOption(String id){
        chosenOption = id;
    }
    //Case when this is on going project and somoene does changes!
    public void ensureDecisionExist(){
        if(optionsForStage.get(chosenOption)==null){
            for (Map.Entry<String, Integer> entry : optionsForStage.entrySet()) {
                setChosenOption(entry.getKey());
                break;
            }
        }
    }


}
