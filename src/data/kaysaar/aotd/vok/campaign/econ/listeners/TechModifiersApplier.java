package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.conditions.HazmatCondition;
import data.kaysaar.aotd.vok.campaign.econ.conditions.IcDemmand;
import data.kaysaar.aotd.vok.campaign.econ.conditions.ResearchedUpkeepModifier;
import data.kaysaar.aotd.vok.campaign.econ.conditions.WaterMinningCond;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class TechModifiersApplier implements PlayerColonizationListener, EconomyTickListener {
    public static void applyResourceConditionToAllMarkets() {
        for (MarketAPI m : Global.getSector().getEconomy().getMarketsCopy()) {
            if(AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION,m)){
                ResearchedUpkeepModifier.applyRessourceCond(m);
            }
            if(AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.HAZMAT_WORKING_EQUIPMENT,m)){
                HazmatCondition.applyRessourceCond(m);
            }
        }
    }

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planetAPI) {
        MarketAPI m = planetAPI.getMarket();
        if(AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION,m)){
            HazmatCondition.applyRessourceCond(m);
            ResearchedUpkeepModifier.applyRessourceCond(m);
        }

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
