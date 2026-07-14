package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

import static data.kaysaar.aotd.vok.campaign.econ.industry.FloatingCityThrusters.getFuelCostPerStructure;
import static data.kaysaar.aotd.vok.campaign.econ.industry.FloatingCityThrusters.getFuelCostPerStructureExcludingBuildingStructures;

public class FloatingCityFuelStockpile extends BaseIndustry {
    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void apply() {
        int total =getFuelCostPerStructureExcludingBuildingStructures(market);
        demand(Commodities.FUEL, total);
    }
}
