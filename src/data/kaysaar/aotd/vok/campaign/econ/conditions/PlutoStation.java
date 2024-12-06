package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;

public class PlutoStation extends BaseMarketConditionPlugin {
    @Override
    public void apply(String id) {
        super.apply(id);
        if(!market.hasIndustry("pluto_station")){
            market.addIndustry("pluto_station");
        }
    }
}
