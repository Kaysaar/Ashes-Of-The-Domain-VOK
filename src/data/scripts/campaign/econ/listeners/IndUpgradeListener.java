package data.scripts.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import data.scripts.campaign.econ.conditions.IndUpgradeCondition;

public class IndUpgradeListener implements PlayerColonizationListener, EconomyTickListener {
    public static void applyIndustyUpgradeCondition() {

        for (MarketAPI m : Global.getSector().getEconomy().getMarketsCopy()) {
            IndUpgradeCondition.applyIndustryUpgradeCondition(m);
        }

    }

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planetAPI) {
        MarketAPI m = planetAPI.getMarket();
        IndUpgradeCondition.applyIndustryUpgradeCondition(m);

    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI marketAPI) {

    }

    @Override
    public void reportEconomyTick(int i) {
        applyIndustyUpgradeCondition();
    }

    @Override
    public void reportEconomyMonthEnd() {

    }
}
