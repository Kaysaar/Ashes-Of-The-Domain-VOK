package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class Policrystalizator extends BaseIndustry {
    public void apply() {
        super.apply(true);

        int size = market.getSize()-2;
        if(size<=0){
            size=0;
        }
        demand(Commodities.ORE, 9 + size);
        demand(Commodities.RARE_ORE, 6 + size); // have to keep it low since it can be circular
        supply(AoTDCommodities.REFINED_METAL, market.getSize()+2); //1+1+1 3 at size 6
        supply(Commodities.METALS, market.getSize()+2);
        supply(Commodities.RARE_METALS, market.getSize()-2);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORE, Commodities.RARE_ORE);
        if(deficit.two>market.getSize()-4){
            deficit.two = market.getSize()-4;
            if(deficit.two<0){
                deficit.two=0;
            }
        }
        applyDeficitToProduction(2, deficit, AoTDCommodities.REFINED_METAL);

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
