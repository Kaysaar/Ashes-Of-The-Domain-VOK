package data.kaysaar.aotd.vok.models;

import com.fs.starfarer.api.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ResearchOptionSpec {
    public String Id;
    public String Name;
    public float TimeToResearch;
    public ArrayList<String> ReqTechsToResearchFirst;
    public ResearchOptionEra Tier;
    public HashMap<String, ResearchRewardType> Rewards;
    public HashMap<String, Integer> ReqItemsToResearchFirst;
    public String IconId;
    public UIInfo UiInfo;
    public Pair<String, String> otherReq;


    public boolean IsResearched;

    public String ModId;

    public String getModId() {
        return ModId;
    }

    public void setModId(String modId) {
        this.ModId = modId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isResearched() {
        return IsResearched;
    }

    public void setResearched(boolean researched) {
        IsResearched = researched;
    }

    public float getTimeToResearch() {
        return TimeToResearch;
    }

    public void setTimeToResearch(float timeToResearch) {
        TimeToResearch = timeToResearch;
    }

    public ArrayList<String> getReqTechsToResearchFirst() {
        return ReqTechsToResearchFirst;
    }

    public void setReqTechsToResearchFirst(ArrayList<String> reqTechsToResearchFirst) {
        ReqTechsToResearchFirst = reqTechsToResearchFirst;
    }

    public ResearchOptionEra getTier() {
        return Tier;
    }

    public void setTier(ResearchOptionEra tier) {
        Tier = tier;
    }

    public HashMap<String, ResearchRewardType> getRewards() {
        return Rewards;
    }

    public void setRewards(HashMap<String, ResearchRewardType> rewards) {
        Rewards = rewards;
    }

    public HashMap<String, Integer> getReqItemsToResearchFirst() {
        return ReqItemsToResearchFirst;
    }

    public void setReqItemsToResearchFirst(HashMap<String, Integer> reqItemsToResearchFirst) {
        ReqItemsToResearchFirst = reqItemsToResearchFirst;
    }

    public String getIconId() {
        return IconId;
    }

    public void setIconId(String iconId) {
        IconId = iconId;
    }

    public UIInfo getUiInfo() {
        return UiInfo;
    }

    public void setUiInfo(UIInfo uiInfo) {
        UiInfo = uiInfo;
    }

    public String getId() {
        return Id;
    }


    public ResearchOptionSpec(String id, String name, int tier, String iconId, float timeToResearch, ArrayList<String> reqToResearch, int column, int row, HashMap<String, ResearchRewardType> rewards,
                              String modId, boolean isResearched, HashMap<String, Integer> reqItemsToResearchFirst, Pair<String, String> otherReq) {
        Id = id;
        Name = name;
        Tier = getValueFromNumber(tier);
        UiInfo = new UIInfo(column, row);
        ReqTechsToResearchFirst = reqToResearch;
        Rewards = rewards;
        TimeToResearch = timeToResearch;
        IconId = iconId;
        ModId = modId;
        IsResearched = isResearched;
        ReqItemsToResearchFirst = reqItemsToResearchFirst;
        this.otherReq = otherReq;
    }

    public static ResearchOptionSpec initSpecFromJson(JSONObject obj) throws JSONException {
        String id = obj.getString("id");
        if (id == null || id.isEmpty()) return null;
        String name = obj.getString("name");
        int tier = obj.getInt("tier");
        int timeToResearch = obj.getInt("timeToResearch");
        String iconId = obj.getString("iconId");
        String req = obj.getString("reqToResearchFirst");
        String otherReq = obj.getString("otherReq");
        if (req.equals("none")) req = null;
        String itemReq = obj.getString("itemReqToResearch");
        String rewards = obj.getString("rewards");
        boolean isResearched = obj.getBoolean("isResearchedFromStart");
        ArrayList<String> reqMerged = new ArrayList<>();
        HashMap<String, Integer> reqItems = new HashMap<>();
        Pair<String, String> globalReq = null;
        if (req != null) {
            String[] reqSplited = req.split(",");
            for (String s : reqSplited) {
                reqMerged.add(s);
            }
        }
        if (itemReq != null) {
            String[] entrysplited = itemReq.split(",");
            for (String s : entrysplited) {
                String[] reqItemsSplited = s.split(":");
                if (reqItemsSplited.length == 2) {
                    reqItems.put(reqItemsSplited[0], Integer.parseInt(reqItemsSplited[1]));
                }
            }
            if (otherReq != null) {
                String[] reqSplited = otherReq.split(":");
                if (reqSplited.length == 2) {
                    globalReq = new Pair<>(reqSplited[0].trim(), reqSplited[1].trim());
                }

            }

        }
        int column = obj.getInt("columnNumber");
        int row = obj.getInt("rowNumber");
        String modId = obj.getString("modId");
        return new ResearchOptionSpec(id, name, tier, iconId, timeToResearch, reqMerged, column, row, generateResearchRewards(rewards), modId, isResearched, reqItems, globalReq);
    }

    public ResearchOptionEra getValueFromNumber(int tier) {
        for (ResearchOptionEra value : ResearchOptionEra.values()) {
            if (value.ordinal() == tier) return value;
        }
        return null;
    }

    public static HashMap<String, ResearchRewardType> generateResearchRewards(String csvEntry) {
        HashMap<String, ResearchRewardType> toReturn = new HashMap<>();
        String[] rewards = csvEntry.split("\n");
        for (String reward : rewards) {
            String[] splitedReward = reward.split(":");
            toReturn.put(splitedReward[0], getType(splitedReward[1]));
        }
        return toReturn;
    }

    public static ResearchRewardType getType(String type) {
        String trimedType = type.trim().toLowerCase();
        if (trimedType.equals("industry")) return ResearchRewardType.INDUSTRY;
        if (trimedType.equals("ship")) return ResearchRewardType.SHIP;
        if (trimedType.equals("weapon")) return ResearchRewardType.WEAPON;
        if (trimedType.equals("hullmod")) return ResearchRewardType.HULLMOD;
        if (trimedType.equals("special_ability")) return ResearchRewardType.SPECIAL_ABILITY;
        if (trimedType.equals("modifier")) return ResearchRewardType.MODIFIER;
        return ResearchRewardType.UNIDENTIFIED;
    }
}
