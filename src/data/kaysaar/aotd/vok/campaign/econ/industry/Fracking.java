package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;


import java.util.HashMap;
import java.util.Map;

public class Fracking extends BaseIndustry {
    private boolean isCryovolcanicOrFrozen() {
        boolean isCryovolcanicOrFrozen = false;
        if(market.getPlanetEntity()!=null){
            if(market.getPlanetEntity().getTypeId().equals("frozen") ||market.getPlanetEntity().getTypeId().equals("cryovolcanic")||market.getPlanetEntity().getTypeId().equals("frozen1")){
                isCryovolcanicOrFrozen= true;
            }
        }
        return isCryovolcanicOrFrozen;
    }
    @Override
    public void apply() {

        super.apply(true);
        int size = market.getSize();
        if(this.special!=null){
            Misc.getStorageCargo(this.getMarket()).addSpecial(this.special, 1);
            this.special=null;
        }
        demand(Commodities.HEAVY_MACHINERY, size - 2);
        demand(Commodities.DRUGS,  size - 2);
        demand(AoTDCommodities.WATER,size-1);

        if(AoDUtilis.getOrganicsAmount(market)>=-1){
            supply(Commodities.ORGANICS,AoDUtilis.getOrganicsAmount(market)+(market.getSize()+2));
        }
        if(AoDUtilis.getNormalOreAmount(market)>=-1){
            supply(Commodities.ORE,AoDUtilis.getNormalOreAmount(market)+(market.getSize()+2));
        }
        if(AoDUtilis.getRareOreAmount(market)>=-1){
            supply(Commodities.RARE_ORE,AoDUtilis.getRareOreAmount(market)+(market.getSize()));
        }
        if(AoDUtilis.getVolatilesAmount(market)>=-1){
            supply(Commodities.VOLATILES,AoDUtilis.getVolatilesAmount(market)+(market.getSize()+2));
        }
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.DRUGS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                Commodities.ORE,Commodities.ORGANICS, AoTDCommodities.WATER,Commodities.RARE_ORE,Commodities.VOLATILES);
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
            return  (AoDUtilis.getOrganicsAmount(market)>=-1 || AoDUtilis.getNormalOreAmount(market) >=-1 || AoDUtilis.getRareOreAmount(market) >= -1 ||AoDUtilis.getVolatilesAmount(market)>=-1)
                    && AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.GEOTHERMAL_FRACKING,market);

    }

    @Override
    public String getUnavailableReason() {
        return "There are no ore deposits present on this planet.";

    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.GEOTHERMAL_FRACKING,market)&&(AoDUtilis.getOrganicsAmount(market)>=-1 || AoDUtilis.getNormalOreAmount(market) >=-1 || AoDUtilis.getRareOreAmount(market) >= -1);
    }


}
