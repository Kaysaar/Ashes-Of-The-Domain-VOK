package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.ArrayList;

public class Policrystalizator extends BaseIndustry {
    public void apply() {
        super.apply(true);

        int size = market.getSize()-2;
        if(size<=0){
            size=0;
        }
        demand(Commodities.ORE, 9 + size);
        demand(Commodities.RARE_ORE, 6 + size); // have to keep it low since it can be circular
            if(AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ALLOY_PURIFICATION,this.getMarket())){
                supply(AoTDCommodities.REFINED_METAL, market.getSize()+2);
            }
            else{
                supply(AoTDCommodities.REFINED_METAL, market.getSize()-2);
            }

        supply(Commodities.METALS, market.getSize()+2);
        supply(Commodities.RARE_METALS, market.getSize()-2);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORE, Commodities.RARE_ORE);
        applyDeficitToProduction(3, deficit, AoTDCommodities.REFINED_METAL,Commodities.METALS,Commodities.RARE_METALS);

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
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ALLOY_PRODUCTION_MATRIX,market);

    }
    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ALLOY_PRODUCTION_MATRIX,market);
    }

    @Override
    public String getUnavailableReason() {
        ArrayList<String> reasons = new ArrayList<>();
        if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ALLOY_PRODUCTION_MATRIX,market)){
            reasons.add(AoTDMainResearchManager.getInstance().getNameForResearchBd(AoTDTechIds.ALLOY_PRODUCTION_MATRIX));

        }
        StringBuilder bd = new StringBuilder();
        boolean insert = false;
        for (String reason : reasons) {
            if(insert){
                bd.append("\n");
            }
            bd.append(reason);

            insert = true;
        }

        return bd.toString();

    }

}
