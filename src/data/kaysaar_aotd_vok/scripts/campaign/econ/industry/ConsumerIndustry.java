package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;

public class ConsumerIndustry extends BaseIndustry {
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        demand(Commodities.ORGANICS, size+2);
        demand(AodCommodities.POLYMERS,size-2);
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

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS,AodCommodities.POLYMERS,Commodities.HEAVY_MACHINERY);
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
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        tooltip.addSectionHeading("Lost Technology", Alignment.MID,10f);
        tooltip.addPara("This industry utilizes sophisticated resources to produce much greater quantities of commonly used commodities around the Persean Sector",10f);
    }

    @Override
    public void unapply() {
        super.unapply();
    }


    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
