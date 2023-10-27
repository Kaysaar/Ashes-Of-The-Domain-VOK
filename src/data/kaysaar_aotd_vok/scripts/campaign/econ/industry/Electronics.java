package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;


public class Electronics extends BaseIndustry {

    @Override
    public void apply() {

        super.apply(true);
        int size = market.getSize();
        demand(Commodities.HEAVY_MACHINERY, size - 2);
        demand(Commodities.METALS, 4 + size - 2);
        if(size<5){
            supply(AodCommodities.ELECTRONICS,1);
        }
        else{
            supply(AodCommodities.ELECTRONICS,market.getSize()-3);
        }
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.METALS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                AodCommodities.ELECTRONICS);
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

      boolean toReturn = AoDUtilis.isResearched(this.getId());

        if (market.hasCondition(Conditions.EXTREME_WEATHER)){
            return false;
        }
        if (market.hasCondition(Conditions.TECTONIC_ACTIVITY)){
            return false;
        }
        if (market.hasCondition(Conditions.TOXIC_ATMOSPHERE)){
            return false;
        }


        return toReturn;
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
        if(!AoDUtilis.isResearched(this.getId())) return false;
        return true;

    }

}
