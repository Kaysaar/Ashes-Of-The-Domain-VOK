package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class CascadeReprocessor extends BaseIndustry {
    public void apply() {
        super.apply(true);
        AoDUtilis.ensureIndustryHasNoItem(this);
        int size = market.getSize()-8;

        if(market.getSize()<=8){// have to keep it low since it can be circular
            demand(AoTDCommodities.PURIFIED_RARE_ORE, 4);
            demand(AoTDCommodities.COMPOUNDS,6);
        }
        else{// have to keep it low since it can be circular
            demand(AoTDCommodities.PURIFIED_RARE_ORE, 4+size);
            demand(AoTDCommodities.COMPOUNDS,6+size);
        }
        supply(AoTDCommodities.PURIFIED_TRANSPLUTONICS, market.getSize()+1);
        supply(Commodities.RARE_METALS, market.getSize());
        supply(Commodities.METALS, market.getSize()-2);


        Pair<String, Integer> deficit = getMaxDeficit(AoTDCommodities.PURIFIED_RARE_ORE, Commodities.METALS, AoTDCommodities.COMPOUNDS);
        int maxDeficit = market.getSize()-3 ;// to allow *some* production so economy doesn't get into an unrecoverable state

        if (!isFunctional()) {
            supply.clear();
        }
    }


    @Override
    public void unapply() {
        super.unapply();
    }


    public float getPatherInterest() {
        return 2f + super.getPatherInterest();
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ALLOY_PURIFICATION,market);

    }
    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ALLOY_PURIFICATION,market);
    }
}
