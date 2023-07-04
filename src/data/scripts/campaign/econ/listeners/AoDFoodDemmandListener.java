package data.scripts.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import data.scripts.campaign.econ.conditions.AoDFoodDemand;

public class AoDFoodDemmandListener implements PlayerColonizationListener, EconomyTickListener {
    public static void applyAdditionalFoodDemmand() {

        for (MarketAPI m : Global.getSector().getEconomy().getMarketsCopy()) {
            AoDFoodDemand.applyRessourceCond(m);
        }

    }

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planetAPI) {
        MarketAPI m = planetAPI.getMarket();
        AoDFoodDemand.applyRessourceCond(m);

    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI marketAPI) {

    }

    @Override
    public void reportEconomyTick(int i) {
        applyAdditionalFoodDemmand();
    }

    @Override
    public void reportEconomyMonthEnd() {

    }
}
