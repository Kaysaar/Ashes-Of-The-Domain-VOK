package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;


public class ExtractiveOperation extends BaseIndustry {
    public  int cos =1;

    @Override
    public void apply() {

        super.apply(true);

        int size = market.getSize();
        demand(Commodities.HEAVY_MACHINERY, size - 2);
        demand(Commodities.DRUGS,  size - 2);
        if(AoDUtilis.getOrganicsAmount(market)>=-1){
            supply(Commodities.ORGANICS,(AoDUtilis.getOrganicsAmount(market)+(market.getSize()-2)));
        }
        if(AoDUtilis.getNormalOreAmount(market)>=-1){
            supply(Commodities.ORE,(AoDUtilis.getNormalOreAmount(market)+(market.getSize()-2)));
        }
        if(AoDUtilis.getRareOreAmount(market)>=0){
            supply(Commodities.RARE_ORE,(AoDUtilis.getRareOreAmount(market)));
        }

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.DRUGS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                Commodities.ORE,Commodities.ORGANICS);
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

            return (AoDUtilis.getNormalOreAmount(market)>=-1 || AoDUtilis.getOrganicsAmount(market) >=-1
                    ||AoDUtilis.getVolatilesAmount(market)>=-1 ||AoDUtilis.getRareOreAmount(market)>=-1)
                    &&super.isAvailableToBuild();

    }

    @Override
    public String getUnavailableReason() {
        return super.getUnavailableReason();
    }

    @Override
    public boolean showWhenUnavailable() {
         return (AoDUtilis.getNormalOreAmount(market)>=-1 || AoDUtilis.getOrganicsAmount(market) >=-1 ||AoDUtilis.getVolatilesAmount(market)>=-1)&&!AoDUtilis.checkForFamilyIndustryInstance(market, Industries.MINING,Industries.MINING,this.id,this.currTooltipMode);
    }

}
