package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import static data.kaysaar.aotd.vok.plugins.AoDUtilis.checkForItemBeingInstalled;

public class Benefication extends BaseIndustry {
    @Override
    public void apply() {
        super.apply(true);
        int size = market.getSize();
        AoDUtilis.ensureIndustryHasNoItem(this);
        demand(Commodities.HEAVY_MACHINERY, size - 2);
        demand(AoTDCommodities.RECITIFICATES, size);
        demand(Commodities.DRUGS, size - 2);
        if (AoDUtilis.getRareOreAmount(market) >= -1) {
            supply(AoTDCommodities.PURIFIED_RARE_ORE, AoDUtilis.getRareOreAmount(market) + (market.getSize() - 2));
            supply(Commodities.RARE_ORE, market.getSize() - 2 + AoDUtilis.getRareOreAmount(market));//for ideal size 6 with alpha core 6
        }
        if (AoDUtilis.getNormalOreAmount(market) >= -1) {
            supply(AoTDCommodities.PURIFIED_ORE, AoDUtilis.getNormalOreAmount(market) + (market.getSize() - 2));
            supply(Commodities.ORE, market.getSize() + AoDUtilis.getNormalOreAmount(market));///for ideal size 6 with alpha core: 7
        }
        Pair<String, Integer> deficit = getMaxDeficit(AoTDCommodities.RECITIFICATES, Commodities.HEAVY_MACHINERY, Commodities.DRUGS);

        if (!isFunctional()) {
            supply.clear();
            unapply();
        }
    }


    @Override
    public void unapply() {
        super.unapply();

    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }

    @Override
    public boolean isAvailableToBuild() {
        return ((AoDUtilis.getRareOreAmount(market) >= 0 || AoDUtilis.getNormalOreAmount(market) >= 0)) &&
                AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DEEP_MINING_METHODS, market);

    }


    @Override
    public String getUnavailableReason() {
        String reasoning = null;

        if (AoDUtilis.getNormalOreAmount(market) < -1) {
            reasoning = "There is no ore on that planet large enough to support that industry ";
        }
        if (AoDUtilis.getRareOreAmount(market) < -1) {
            if (reasoning != null) {
                reasoning += "\nThere is no transplutonic ore on that planet large enough to support that industry ";
            }

        }

        if (!checkForItemBeingInstalled(market, Industries.MINING, Items.MANTLE_BORE)) {
            if (reasoning != null) {
                reasoning += "\nMantle Bore required to be installed in Mining";
            } else {
                reasoning = "Mantle Bore required to be installed in Mining";
            }
        }


        return reasoning;
    }

    @Override
    public boolean showWhenUnavailable() {
        return (AoDUtilis.getRareOreAmount(market) >= -1 || AoDUtilis.getNormalOreAmount(market) >= -1) && AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DEEP_MINING_METHODS, market);
    }
}
