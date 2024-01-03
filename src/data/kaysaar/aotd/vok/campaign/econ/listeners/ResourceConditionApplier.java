package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import data.kaysaar.aotd.vok.campaign.econ.conditions.IcDemmand;
import data.kaysaar.aotd.vok.campaign.econ.conditions.WaterMinningCond;


public class ResourceConditionApplier implements PlayerColonizationListener, EconomyTickListener {
    public static void applyResourceConditionToAllMarkets() {
        for (MarketAPI m : Global.getSector().getEconomy().getMarketsCopy()) {
            IcDemmand.applyRessourceCond(m);
            WaterMinningCond.applyIndustryUpgradeCondition(m);
        }
    }

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planetAPI) {
        MarketAPI m = planetAPI.getMarket();
        WaterMinningCond.applyIndustryUpgradeCondition(m);
        IcDemmand.applyRessourceCond(m);

    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI marketAPI) {

    }

    @Override
    public void reportEconomyTick(int i) {
        applyResourceConditionToAllMarkets();
    }

    @Override
    public void reportEconomyMonthEnd() {

    }


}
