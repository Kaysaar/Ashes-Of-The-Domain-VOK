package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;

public class FloatingCity extends BaseColonyDevelopment {
    @Override
    public String getName() {
        return "Floating City";
    }

    @Override
    public boolean canShowOnMarket(MarketAPI market) {
        if (market.getPrimaryEntity() instanceof PlanetAPI planetAPI) {
            return planetAPI.isGasGiant();
        }
        return false;
    }

    @Override
    public boolean canBeAppliedOnMarket(MarketAPI market) {
        return canShowOnMarket(market);
    }

    @Override
    public void apply(MarketAPI market) {

    }

    @Override
    public void unapply(MarketAPI market) {

    }
}
