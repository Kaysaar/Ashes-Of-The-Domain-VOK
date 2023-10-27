package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;

public class IsotopeSeparator extends BaseIndustry {
    public void apply() {
        super.apply(true);

        int size = market.getSize();


        demand(Commodities.HEAVY_MACHINERY, size - 2); // have to keep it low since it can be circular
        demand(Commodities.RARE_ORE, size + 4);
        demand(AodCommodities.COMPOUNDS, size - 2);
        supply(Commodities.RARE_METALS, size+2);

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.HEAVY_MACHINERY, Commodities.RARE_ORE,AodCommodities.POLYMERS);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit, Commodities.RARE_METALS);

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
}
