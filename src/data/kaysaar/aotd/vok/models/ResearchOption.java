package data.kaysaar.aotd.vok.models;

import com.fs.starfarer.api.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResearchOption {
    public String Id;
    public String Name;
    public float TimeToResearch;
    public ArrayList<String> ReqTechsToResearchFirst;
    public ResearchOptionEra Tier;
    public HashMap<String, ResearchRewardType> Rewards;
    public HashMap<String, Integer> ReqItemsToResearchFirst;
    public String IconId;
    public UIInfo UiInfo;
    public ResearchOptionSpec spec;
    public boolean isResearched = false;
    public boolean havePaidForResearch =false;
    public Pair<String, String>otherReq;
    public boolean metOtherReq  = false;
    public  float daysSpentOnResearching = 0f;
    public String modID;

    public ResearchOption(String id, String name, int tier, String iconId, float timeToResearch, ArrayList<String> reqToResearch, int column, int row, HashMap<String, ResearchRewardType> rewards,boolean isResearched) {
        Id = id;
        Name = name;
        Tier = getValueFromNumber(tier);
        UiInfo = new UIInfo(column, row);
        ReqTechsToResearchFirst = reqToResearch;
        Rewards = rewards;
        TimeToResearch = timeToResearch;
        IconId = iconId;
        if(isResearched){
            this.isResearched = isResearched;
        }
    }
    public void update(){
        Id = spec.Id;
        Name = spec.Name;
        Tier = spec.getTier();
        UiInfo = spec.getUiInfo();
        ReqTechsToResearchFirst = spec.getReqTechsToResearchFirst();
        Rewards = spec.getRewards();
        TimeToResearch = spec.getTimeToResearch();
        IconId = spec.getIconId();
        if(spec.isResearched()){
            isResearched = spec.isResearched();
        }

        ReqItemsToResearchFirst = new HashMap<String, Integer>();
        ReqItemsToResearchFirst.putAll(spec.getReqItemsToResearchFirst());

        this.otherReq = spec.otherReq;
        this.modID = spec.getModId();
    }

    public ResearchOption(ResearchOptionSpec spec) {
        this.spec = spec;
        update();
    }

    public ResearchOptionEra getValueFromNumber(int tier) {
        for (ResearchOptionEra value : ResearchOptionEra.values()) {
            if (value.ordinal() == tier) return value;
        }
        return null;
    }

    public ResearchOptionSpec getSpec() {
        return spec;
    }

    public void setSpec(ResearchOptionSpec spec) {
        this.spec = spec;
    }

    public boolean isResearched() {
        return isResearched;
    }

    public void setResearched(boolean researched) {
        isResearched = researched;
    }

    public void updateFromSpec() {
        update();
    }
}
