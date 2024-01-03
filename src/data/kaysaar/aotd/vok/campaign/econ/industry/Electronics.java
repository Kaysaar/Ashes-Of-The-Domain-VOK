package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;


public class Electronics extends BaseIndustry {

    @Override
    public void apply() {

        super.apply(true);
        int size = market.getSize();
        demand(Commodities.HEAVY_MACHINERY, size - 2);
        demand(Commodities.METALS, 4 + size - 2);
        if(size<4){
            supply(AoTDCommodities.ELECTRONICS,2);
        }
        else{
            supply(AoTDCommodities.ELECTRONICS,market.getSize()-2);
        }
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.METALS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                AoTDCommodities.ELECTRONICS);
        if (!isFunctional()) {
            supply.clear();
            unapply();
        }
    }

    @Override
    public void unapply() {
        super.unapply();

    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }

    @Override
    public boolean isAvailableToBuild() {

        if (market.hasCondition(Conditions.EXTREME_WEATHER)){
            return false;
        }
        if (market.hasCondition(Conditions.TECTONIC_ACTIVITY)){
            return false;
        }
        if (market.hasCondition(Conditions.TOXIC_ATMOSPHERE)){
            return false;
        }


        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.SOPHISTICATED_ELECTRONIC_SYSTEMS,market);
    }




    @Override
    public String getUnavailableReason() {
        if (market.hasCondition(Conditions.EXTREME_WEATHER)){
            return "Cannot be builded on planet with extreme weather ";
        }
        if (market.hasCondition(Conditions.TECTONIC_ACTIVITY)){
            return "Cannot be builded on planet with tectonic activity ";
        }
        if (market.hasCondition(Conditions.TOXIC_ATMOSPHERE)){
            return "Cannot be builded on planet with toxic atmosphere ";
        }

        return"There is bug, please report it to mod author";

    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.SOPHISTICATED_ELECTRONIC_SYSTEMS,market);
    }

}
