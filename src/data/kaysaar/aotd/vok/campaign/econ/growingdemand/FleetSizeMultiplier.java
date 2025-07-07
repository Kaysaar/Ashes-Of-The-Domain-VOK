package data.kaysaar.aotd.vok.campaign.econ.growingdemand;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.models.GrowingDemandScript;

import java.util.LinkedHashMap;

public class FleetSizeMultiplier extends GrowingDemandScript {
    public float minFleetSize;
    public float maxFleetSize;
    public FleetSizeMultiplier(String idOfCommodity, LinkedHashMap<String, Integer> commoditiesReplacementRatio,float minFleetSize,float maxFleetSize) {
        super(idOfCommodity, commoditiesReplacementRatio);
        this.minFleetSize = minFleetSize;
        this.maxFleetSize = maxFleetSize;
    }

    @Override
    public void applyEffectToEntireMarket(MarketAPI market) {
        String id = idOfCommodity + "_modifier";

        int maxDemand = market.getCommodityData(idOfCommodity).getMaxDemand();
        int deficit = market.getCommodityData(idOfCommodity).getDeficitQuantity();

        if (maxDemand <= 0) return;

        int left = maxDemand - deficit;
        float demandMet = (float) left / maxDemand;
        demandMet = Math.max(0f, Math.min(1f, demandMet)); // Clamp to [0, 1]

        float progressForDemand = getPercentageOfDemandGrowth(); // [0, 1]

        // Normalize demandMet in range [0, 0.3] → [0, 1]
        float normalized = Math.min(demandMet / 0.3f, 1f);

        // Interpolate final value between 0 and maxFleetSize (e.g., 0.3)
        float rawMultiplier = normalized * maxFleetSize;

        // Scale effect by demand growth progress (also 0 to 1)
        float finalMultiplier = rawMultiplier * progressForDemand;

        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT)
                .modifyFlat(id, finalMultiplier, Global.getSettings().getCommoditySpec(idOfCommodity).getName());
    }


    @Override
    public void unapplyEffectToEntireMarket(MarketAPI market) {
        String id = idOfCommodity+"_modifier";
        market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT)
                .unmodifyFlat(id);
    }
}
