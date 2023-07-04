package data.scripts.industry;

import com.fs.graphics.C;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AoDIndustries;
import data.Ids.AodCommodities;

public class KaysaarPolicrystalizator extends BaseIndustry {
    public void apply() {
        super.apply(true);

        int size = market.getSize()-8;
        if(size<=0){
            size=0;
        }
        demand(Commodities.ORE, 10 + size);
        demand(Commodities.RARE_ORE, 7 + size); // have to keep it low since it can be circular
        demand(AodCommodities.PURIFIED_ORE, 5+size);
        demand(AodCommodities.POLYMERS,6+size);
        supply(AodCommodities.REFINED_METAL, market.getSize()-4); //1+1+1 3 at size 6

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORE, AodCommodities.PURIFIED_ORE, Commodities.RARE_ORE,AodCommodities.POLYMERS);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(1, deficit, AodCommodities.REFINED_METAL);

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
        if(market.getIndustry(AoDIndustries.CRYSTALIZATOR)==null){
            return false;
        }
        if(market.getIndustry(AoDIndustries.CRYSTALIZATOR).getSpecialItem()==null ){
            return false;
        }
        return market.getIndustry(AoDIndustries.CRYSTALIZATOR).getSpecialItem().getId().equals(Items.CATALYTIC_CORE);
    }

    @Override
    public String getUnavailableReason() {
        return "Catalytic Core required to be installed in Crystalizator";

    }
}
