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
import data.kaysaar.aotd.vok.campaign.econ.items.ModularConstructorPlugin;
import data.kaysaar.aotd.vok.campaign.econ.items.ModularConstructorRepo;
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
            reasoning = "There are no ore deposits on this planet that are large enough to support this industry. ";
        }
        if (AoDUtilis.getRareOreAmount(market) < -1) {
            if (reasoning != null) {
                reasoning += "\nThere are no transplutonic ore deposits on this planet that are large enough to support this industry. ";
            }

        }



        return reasoning;
    }

    @Override
    public boolean showWhenUnavailable() {
        return (AoDUtilis.getRareOreAmount(market) >= -1 || AoDUtilis.getNormalOreAmount(market) >= -1) && AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DEEP_MINING_METHODS, market);
    }
}
