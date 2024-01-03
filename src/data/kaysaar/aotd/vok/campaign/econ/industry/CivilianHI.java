package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class CivilianHI extends HeavyIndustry {

    @Override
    public void apply() {
        super.apply(true);
        int size = market.getSize();
        demand(Commodities.METALS, size);
        demand(Commodities.RARE_METALS, size - 2);
        supply(Commodities.HEAVY_MACHINERY, size);
        supply(Commodities.SUPPLIES, size+2);
        supply(Commodities.HAND_WEAPONS, size - 3);
        supply(Commodities.SHIPS, size  - 3);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.METALS,Commodities.RARE_ORE);
        int maxDeficit =  size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;

        applyDeficitToProduction(2, deficit,
                Commodities.HEAVY_MACHINERY,
                Commodities.SUPPLIES,
                Commodities.HAND_WEAPONS,
                Commodities.SHIPS);


//		if (market.getId().equals("chicomoztoc")) {
//			System.out.println("efwefwe");
//		}



        if (!isFunctional()) {
            supply.clear();
            unapply();
        }
        else{


        }
    }

    @Override
    public void unapply() {
        market.getStability().unmodifyFlat("waystation_synergy");
    }
    public boolean isDemandLegal(CommodityOnMarketAPI com) {
        return true;
    }

    public boolean isSupplyLegal(CommodityOnMarketAPI com) {
        return true;
    }

    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.STREAMLINED_PRODUCTION,market);
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.STREAMLINED_PRODUCTION,market);
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
