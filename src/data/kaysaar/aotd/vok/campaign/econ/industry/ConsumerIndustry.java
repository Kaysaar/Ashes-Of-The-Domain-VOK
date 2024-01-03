package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;


public class ConsumerIndustry extends BaseIndustry {
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        demand(Commodities.ORGANICS, size+2);
        demand(AoTDCommodities.POLYMERS,size-2);
        demand(Commodities.HEAVY_MACHINERY,size);
        supply(Commodities.DOMESTIC_GOODS, size+2);
        //supply(Commodities.SUPPLIES, size - 3);

        //if (!market.getFaction().isIllegal(Commodities.LUXURY_GOODS)) {
        if (!market.isIllegal(Commodities.LUXURY_GOODS)) {
            supply(Commodities.LUXURY_GOODS, size);
        } else {
            supply(Commodities.LUXURY_GOODS, 0);
        }
        //if (!market.getFaction().isIllegal(Commodities.DRUGS)) {
        if (!market.isIllegal(Commodities.DRUGS)) {
            supply(Commodities.DRUGS, size);
        } else {
            supply(Commodities.DRUGS, 0);
        }

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS, AoTDCommodities.POLYMERS,Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                Commodities.DOMESTIC_GOODS,
                Commodities.LUXURY_GOODS,
                //Commodities.SUPPLIES,
                Commodities.DRUGS);

        if (!isFunctional()) {
            supply.clear();
        }
    }


    @Override
    public void unapply() {
        super.unapply();
    }

    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.CONSUMER_GOODS_PRODUCTION,market);
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.CONSUMER_GOODS_PRODUCTION,market);
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
