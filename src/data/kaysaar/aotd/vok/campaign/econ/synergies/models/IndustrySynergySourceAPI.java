package data.kaysaar.aotd.vok.campaign.econ.synergies.models;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public interface IndustrySynergySourceAPI {
    public float calculateEfficiencyFromIndustry(Industry ind,boolean includeDemand);
    public float getBonusForImproved();
    public float getBonusForAICore(String aiCoreID);
    public void addToTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded);
    public void addToTooltipForInfo(Industry industry,TooltipMakerAPI tooltip);
    public String getId();
}
