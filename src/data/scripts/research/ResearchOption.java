package data.scripts.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.loading.IndustrySpecAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResearchOption {
    public String industryId;

    public float currentResearchDays = -1;
    public int researchTier;
    public boolean isResearched;
    public int researchCost;
    public ArrayList<String> requieredIndustriesToResearchIds;

    public HashMap<String, Integer> requieredItems;
    public boolean initalized = false;
    public String industryName;
    public int reqCost;
    public boolean hasDowngrade;
    public String downgradeId;
    public String modId;
    public boolean isDisabled;
    public List<String> hiddenFactor;
    public boolean hasTakenResearchCost = false;

    public String getIndustryName() {
        for (IndustrySpecAPI indSpec : Global.getSettings().getAllIndustrySpecs()) {
            if (indSpec.getId().equals(this.industryId)) {
                return indSpec.getName();

            }
        }
        return "Sth fucked";
    }

    public int calculateReqInit() {
        return researchCost * researchTier;
    }


    public ResearchOption(String industryId, int researchCost, int researchTier, boolean isResearched,
                          ArrayList<String> requieredIndustriesToResearchIds, HashMap<String, Integer> requieredItems, boolean hasDowngrade, String downgradeId
            , boolean isDisabled, String modId, List<String> hiddenFactor) {
        this.industryId = industryId;
        this.isResearched = isResearched;
        this.researchTier = researchTier;
        this.researchCost = researchCost;
        this.requieredIndustriesToResearchIds = requieredIndustriesToResearchIds;
        this.industryName = getIndustryName();
        this.requieredItems = requieredItems;
        reqCost = calculateReqInit();
        this.downgradeId = downgradeId;
        this.currentResearchDays = (float) researchCost;
        this.hasDowngrade = hasDowngrade;
        this.isDisabled = isDisabled;
        this.modId = modId;
        this.hiddenFactor = hiddenFactor;
    }


}
