package data.kaysaar.aotd.vok.campaign.econ.items;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.IndustryBlueprintItemPlugin;

public class StarCitadelDesignsPlugin extends IndustryBlueprintItemPlugin {
    @Override
    public String getName() {
        if (industry != null) {
            return industry.getName() + " Schematics";
        }
        return super.getName();
    }

    @Override
    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        if (spec != null) return (int) spec.getBasePrice();
        return 0;
    }
}
