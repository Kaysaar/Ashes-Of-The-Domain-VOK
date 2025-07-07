package data.kaysaar.aotd.vok.campaign.econ.growingdemand;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.models.GrowingDemandScript;

import java.util.LinkedHashMap;

public class UpkeepReductionDemand extends GrowingDemandScript {
    public float maxUpkeepReduction;
    public float maxUpkeepPenalty;
    public UpkeepReductionDemand(String idOfCommodity, LinkedHashMap<String, Integer> commoditiesReplacementRatio,float maxUpkeepReduction,float maxUpkeepPenalty) {
        super(idOfCommodity, commoditiesReplacementRatio);
        this.maxUpkeepReduction = maxUpkeepReduction;
        this.maxUpkeepPenalty = maxUpkeepPenalty;
    }

    @Override
    public void applyEffectsOnIndustry(Industry industry, int demanded, int deficit) {
        String memkey = idOfCommodity + "_demand_reduce_upkeep";

        if (demanded == 0) {
            industry.getUpkeep().unmodifyFlat(memkey);
            return;
        }

        float demandMet = (float)(demanded - deficit) / demanded; // fraction demand met [0..1]
        demandMet = Math.max(0f, Math.min(1f, demandMet)); // clamp just in case

        float progressForDemand = getPercentageOfDemandGrowth(); // [0..1]

        // Interpolate between penalty and reduction based on demand met
        float interpolated = maxUpkeepPenalty + demandMet * (maxUpkeepReduction - maxUpkeepPenalty);

        // Now scale how much effect applies based on progressForDemand
        // Blend between neutral (1) and interpolated multiplier
        float finalMultiplier = 1f + progressForDemand * (interpolated - 1f);

        industry.getUpkeep().modifyMult(memkey, finalMultiplier,
                Global.getSettings().getCommoditySpec(idOfCommodity).getName());
    }




    @Override
    public void unapplyEffectsOnIndustry(Industry industry) {
        String memkey = idOfCommodity+"_demand_reduce_upkeep";
        industry.getUpkeep().unmodify(memkey);
    }
}
