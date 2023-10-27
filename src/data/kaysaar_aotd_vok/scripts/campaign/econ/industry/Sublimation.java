package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;

public class Sublimation extends BaseIndustry {
    @Override
    public void apply() {

        super.apply(true);
        int size = market.getSize();
        if(this.special!=null){
            Misc.getStorageCargo(this.getMarket()).addSpecial(this.special, 1);
            this.special=null;
        }
        demand(Commodities.HEAVY_MACHINERY, size);
        demand(AodCommodities.BIOTICS,  size);
        demand(Commodities.ORGANICS,  size);
        if(AoDUtilis.getOrganicsAmount(market)>=0){
            supply(AodCommodities.POLYMERS,AoDUtilis.getOrganicsAmount(market)+(market.getSize()-4));
        }
        if(AoDUtilis.getVolatilesAmount(market)>=0){
            supply(AodCommodities.COMPOUNDS,AoDUtilis.getVolatilesAmount(market)+(market.getSize()-4));
        }
        Pair<String, Integer> deficit = getMaxDeficit(AodCommodities.BIOTICS, Commodities.HEAVY_MACHINERY,Commodities.ORGANICS);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                AodCommodities.POLYMERS,AodCommodities.COMPOUNDS);
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
        return AoDUtilis.getOrganicsAmount(market)>=-0 || AoDUtilis.getVolatilesAmount(market) >= 0;

    }

    @Override
    public String getUnavailableReason() {
        String reasoning= null;
        if( AoDUtilis.getOrganicsAmount(market)<=-1){
            reasoning = "There are no organics on that planet large enough to support that industry ";
        }
        if(AoDUtilis.getVolatilesAmount(market)<=-1){
            if(reasoning!=null){
                reasoning = "There are neither organics nor volatiles on the planet large enough to support that industry ";
            }
            else{
                reasoning = "There are no volatiles on the planet large enough to support that industry ";
            }

        }
        return reasoning;

    }

    @Override
    public boolean showWhenUnavailable() {
        return AoDUtilis.getOrganicsAmount(market)<=-1 || AoDUtilis.getVolatilesAmount(market) <= -1;
    }

}
