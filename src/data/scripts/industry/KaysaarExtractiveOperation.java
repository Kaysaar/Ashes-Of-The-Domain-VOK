package data.scripts.industry;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;
import data.plugins.AoDUtilis;

public class KaysaarExtractiveOperation extends BaseIndustry {


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


        if(market.getPlanetEntity()!=null){
            if(market.getPlanetEntity().getTypeId().equals("frozen") ||market.getPlanetEntity().getTypeId().equals("cryovolcanic")){
             supply(AodCommodities.WATER,market.getSize()-4,"Frozen Water");
            }
        }
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.DRUGS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                Commodities.ORE,Commodities.ORGANICS,AodCommodities.WATER);
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

            return (AoDUtilis.getNormalOreAmount(market)>=-1 || AoDUtilis.getOrganicsAmount(market) >=-1 ||AoDUtilis.getVolatilesAmount(market)>=-1 ||AoDUtilis.getRareOreAmount(market)>=-1)&&!AoDUtilis.checkForFamilyIndustryInstance(market, Industries.MINING,Industries.MINING,this.id);

    }

    @Override
    public String getUnavailableReason() {
        if(AoDUtilis.checkForFamilyIndustryInstance(market, Industries.MINING, Industries.MINING,this.id)){
            return AoDUtilis.reason;
        }
        return null;
    }

    @Override
    public boolean showWhenUnavailable() {
         return (AoDUtilis.getNormalOreAmount(market)>=-1 || AoDUtilis.getOrganicsAmount(market) >=-1 ||AoDUtilis.getVolatilesAmount(market)>=-1)&&!AoDUtilis.checkForFamilyIndustryInstance(market, Industries.MINING,Industries.MINING,this.id);
    }

}
