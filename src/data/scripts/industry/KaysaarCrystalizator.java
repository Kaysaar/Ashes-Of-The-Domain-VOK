package data.scripts.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;

public class KaysaarCrystalizator extends BaseIndustry {
    public void apply() {
        super.apply(true);

        int size = market.getSize();


        demand(Commodities.HEAVY_MACHINERY, size - 2); // have to keep it low since it can be circular
        demand(Commodities.ORE, size + 3);
        demand(AodCommodities.POLYMERS, size - 2);
        supply(Commodities.METALS, size+2);

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.HEAVY_MACHINERY, Commodities.ORE,AodCommodities.POLYMERS);

        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        if (!isFunctional()) {
            supply.clear();
        }
        applyDeficitToProduction(2, deficit, Commodities.METALS);
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
