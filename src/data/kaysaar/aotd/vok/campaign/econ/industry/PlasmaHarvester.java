package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.ArrayList;

public class PlasmaHarvester extends BaseIndustry {
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
        demand(Commodities.HEAVY_MACHINERY, size - 2);
        demand(Commodities.DRUGS,  size - 2);
        if(AoDUtilis.getVolatilesAmount(market)>=-1){
            supply(Commodities.VOLATILES,AoDUtilis.getVolatilesAmount(market)+(market.getSize()+3));
        }
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.DRUGS, Commodities.HEAVY_MACHINERY);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                Commodities.ORE,Commodities.ORGANICS,Commodities.RARE_ORE,Commodities.VOLATILES);
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
        boolean gasGiantOrStation = (this.market.getPlanetEntity()!=null&&this.getMarket().getPlanetEntity().isGasGiant())||(this.getMarket().getPrimaryEntity()!=null&&this.getMarket().getPrimaryEntity().hasTag(Tags.STATION));
            return  (AoDUtilis.getVolatilesAmount(market)>=-1)
                    && AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DEEP_MINING_METHODS,market)&&gasGiantOrStation;

    }

    @Override
    public String getUnavailableReason() {
        ArrayList<String> reasons = new ArrayList<>();
        boolean isGasGiantOrStation = (this.market.getPlanetEntity()!=null && this.getMarket().getPlanetEntity().isGasGiant()) ||
                (this.getMarket().getPrimaryEntity()!=null && this.getMarket().getPrimaryEntity().hasTag(Tags.STATION));

        if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DEEP_MINING_METHODS,market)){
            reasons.add(AoTDMainResearchManager.getInstance().getNameForResearchBd(AoTDTechIds.DEEP_MINING_METHODS));

        }
        if(!isGasGiantOrStation){
            reasons.add("Planet must be a gas giant or a siphon station.");
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

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DEEP_MINING_METHODS,market)&&(AoDUtilis.getOrganicsAmount(market)>=-1 || AoDUtilis.getNormalOreAmount(market) >=-1 || AoDUtilis.getRareOreAmount(market) >= -1);
    }


}
