package data.kaysaar.aotd.vok.campaign.econ.growingdemand;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.models.GrowingDemandScript;

import java.util.LinkedHashMap;

public class SpaceDrugsDemand extends GrowingDemandScript {
    public SpaceDrugsDemand(String idOfCommodity, LinkedHashMap<String, Integer> commoditiesReplacementRatio) {
        super(idOfCommodity, commoditiesReplacementRatio);
        setMaxMagnitude(0.5f);
        setCanDecrease(false);
        setGrowthAdditionalRate(0.08f);
        this.shouldReplaceCommodity = false;
    }

    @Override
    public void applyEffectToEntireMarket(MarketAPI market) {
        if(magnitiude>0.3f){
            FactionAPI faction = market.getFaction();
            if(!faction.isPlayerFaction()&&faction.isIllegal(Commodities.DRUGS)){
                faction.getIllegalCommodities().add("wwlb_cerulean_vapors");
            }
        }
    }
}
