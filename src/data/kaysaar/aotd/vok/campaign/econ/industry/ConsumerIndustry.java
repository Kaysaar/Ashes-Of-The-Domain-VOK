package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.LightIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.ArrayList;


public class ConsumerIndustry extends LightIndustry {
    @Override
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        demand(Commodities.ORGANICS, size + 2);
        demand(Commodities.HEAVY_MACHINERY, size);
        supply(Commodities.DOMESTIC_GOODS, size + 4);
        //supply(Commodities.SUPPLIES, size - 3);

        //if (!market.getFaction().isIllegal(Commodities.LUXURY_GOODS)) {
        if (!market.isIllegal(Commodities.LUXURY_GOODS)) {
            supply(Commodities.LUXURY_GOODS, size);
        } else {
            supply(Commodities.LUXURY_GOODS, 0);
        }
        //if (!market.getFaction().isIllegal(Commodities.DRUGS)) {

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                Commodities.DOMESTIC_GOODS,
                Commodities.LUXURY_GOODS
                //Commodities.SUPPLIES
        );

        if (!isFunctional()) {
            supply.clear();
        }
    }

    @Override
    public String getCurrentImage() {
        return getSpec().getImageName();
    }

    @Override
    public void unapply() {
        super.unapply();
    }

    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.CONSUMER_GOODS_PRODUCTION, market);
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.CONSUMER_GOODS_PRODUCTION, market);
    }

    @Override
    public String getUnavailableReason() {
        ArrayList<String> reasons = new ArrayList<>();
        if (!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.CONSUMER_GOODS_PRODUCTION, market)) {
            reasons.add(AoTDMainResearchManager.getInstance().getNameForResearchBd(AoTDTechIds.CONSUMER_GOODS_PRODUCTION));

        }
        StringBuilder bd = new StringBuilder();
        boolean insert = false;
        for (String reason : reasons) {
            if (insert) {
                bd.append("\n");
            }
            bd.append(reason);

            insert = true;
        }

        return bd.toString();
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
