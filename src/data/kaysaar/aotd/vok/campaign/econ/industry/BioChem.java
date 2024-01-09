package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.LightIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class BioChem extends LightIndustry {
    public void apply() {
        super.apply(true);
        AoDUtilis.ensureIndustryHasNoItem(this);
        int size = market.getSize();
        demand(Commodities.ORGANICS, size+2);
        demand(AoTDCommodities.BIOTICS,size-2);
        demand(AoTDCommodities.COMPOUNDS, size-2);
        //supply(Commodities.SUPPLIES, size - 3);

        //if (!market.getFaction().isIllegal(Commodities.LUXURY_GOODS)) {
        if (!market.isIllegal(Commodities.DRUGS)) {
            supply(Commodities.DRUGS, size+5);
        } else {
            supply(Commodities.DRUGS, 0);
        }
        //if (!market.getFaction().isIllegal(Commodities.DRUGS)) {

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS, AoTDCommodities.BIOTICS,AoTDCommodities.COMPOUNDS);
        applyDeficitToProduction(2, deficit,
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
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.CONSUMER_GOODS_PRODUCTION,market)&&market.isFreePort();
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.CONSUMER_GOODS_PRODUCTION,market);
    }

    @Override
    public String getUnavailableReason() {
        if( !AoDUtilis.checkForItemBeingInstalled(market, Industries.LIGHTINDUSTRY, Items.BIOFACTORY_EMBRYO)){
            return "Biofactory Embryo must be installed in Light Industry";
        }
        return "Market must have free port enabled.";
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
