package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class HighTechIndustry extends BaseIndustry {
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        demand(Commodities.ORGANICS, size+2);
        demand(Commodities.HEAVY_MACHINERY,size);
        supply(AoTDCommodities.ADVANCED_COMPONENTS, getAdvancedComponents()+1);
        //supply(Commodities.SUPPLIES, size - 3);

        //if (!market.getFaction().isIllegal(Commodities.LUXURY_GOODS)) {
        if (!market.isIllegal(Commodities.LUXURY_GOODS)) {
            supply(Commodities.LUXURY_GOODS, size+2);
        } else {
            supply(Commodities.LUXURY_GOODS, 0);
        }
        //if (!market.getFaction().isIllegal(Commodities.DRUGS)) {

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS,Commodities.HEAVY_MACHINERY);
        applyDeficitToProduction(2, deficit,
                Commodities.DOMESTIC_GOODS,
                Commodities.LUXURY_GOODS,
                //Commodities.SUPPLIES,
                AoTDCommodities.ADVANCED_COMPONENTS);

        if (!isFunctional()) {
            supply.clear();
        }
    }

    public int getAdvancedComponents(){
        if(this.market.getSize()<5){
            return 0;
        }
        if(this.market.getSize()>=5&&market.getSize()<=8){
            return market.getSize()-2;
        }
        if(market.getSize()>8){
            return market.getSize()-1;
        }
        return 0;
    }
    @Override
    public void unapply() {
        super.unapply();
    }

    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.SOPHISTICATED_ELECTRONIC_SYSTEMS,market);
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.SOPHISTICATED_ELECTRONIC_SYSTEMS,market);
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }

}
