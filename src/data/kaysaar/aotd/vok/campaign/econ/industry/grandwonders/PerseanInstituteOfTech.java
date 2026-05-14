package data.kaysaar.aotd.vok.campaign.econ.industry.grandwonders;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.tot.grandwonders.GrandWonderAPI;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class PerseanInstituteOfTech extends BaseIndustry implements GrandWonderAPI {
    @Override
    public LinkedHashMap<String, Integer> getDemandCostForRestoration() {
        return new LinkedHashMap<>();
    }

    @Override
    public void finishedConstruction(MarketAPI marketAPI) {

    }

    @Override
    public String getWonderTypeId() {
        return "technology";
    }

    @Override
    public void addToCustomSectionInTooltip(TooltipMakerAPI tooltipMakerAPI) {

    }

    @Override
    public LinkedHashMap<String, String> getRequirementsToBuildWonder() {
        return new LinkedHashMap<>();
    }

    @Override
    public boolean hasReqBeenMetOnMarket(String s) {
        return true;
    }

    @Override
    public LinkedHashSet<String> getIndustriesToPreventFromAppearingInMenu(MarketAPI marketAPI) {
        return new LinkedHashSet<>();
    }

    @Override
    public boolean shouldShowInListOfWonders(MarketAPI marketAPI) {
        return true;
    }

    @Override
    public void apply() {

    }
}
