package data.kaysaar.aotd.vok.scripts.research.contracts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class BaseResearchContractData {
    String factionID;
    LinkedHashMap<String,String> stageNames;
    LinkedHashMap<String,Integer>stageValues;
    LinkedHashMap<String,String>iconMap;
    float databankMult;
    int maxProgress;
    Set<String> prioritizedTech;
    float researchDestructionOfEnemyFactionMult;
    public FactionAPI getFaction(){
        return Global.getSector().getFaction(factionID);
    }
    public BaseResearchContractData (String factionID, LinkedHashMap<String,String>stageNames,    LinkedHashMap<String,Integer>stageValues,    LinkedHashMap<String,String>iconMap, int maxPoints){
        this.factionID = factionID;
        this.stageNames = stageNames;
        this.stageValues  = stageValues;
        this.iconMap = iconMap;
        this.maxProgress = maxPoints;
    }


}
