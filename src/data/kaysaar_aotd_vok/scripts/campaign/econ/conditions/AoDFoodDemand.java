package data.kaysaar_aotd_vok.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;
import data.Ids.AodMemFlags;


public class AoDFoodDemand extends BaseMarketConditionPlugin {

    public static  String FoodCostDesc = "AoDFoodDemand";

    @Override
    public void apply(String id) {
        super.apply(id);
        if(!market.hasIndustry(Industries.POPULATION)) return;
        applyFoodRequirements(market.getIndustry(Industries.POPULATION));
        applyOtherDemmands((BaseIndustry) market.getIndustry(Industries.POPULATION));

    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        if(!market.hasIndustry(Industries.POPULATION)) return;
            Industry ind = market.getIndustry(Industries.POPULATION);
            resetDemmand(ind);
            unapplyOtherDemand((BaseIndustry) ind);

        }
    public void unapplyOtherDemand(BaseIndustry ind) {
        ind.supply(AodCommodities.RECITIFICATES, 0, "");
        ind.demand(AodCommodities.RECITIFICATES, 0, "");
        ind.supply(AodCommodities.BIOTICS, 0, "");
        ind.demand(AodCommodities.BIOTICS, 0, "");
        ind.supply(AodCommodities.WATER, 0, "");
        ind.demand(AodCommodities.WATER, 0, "");
    }

    public void applyFoodRequirements(Industry ind) {

        int marketSize = ind.getMarket().getSize();

        switch (marketSize){
            case 3:
            case 4:
                resetDemmand(ind);
                ind.getDemand(Commodities.FOOD).getQuantity().modifyFlat(AodMemFlags.POP_FOOD_DEMAND_3_AND_4, +1, FoodCostDesc);
                break;
            case 5:
                resetDemmand(ind);
                ind.getDemand(Commodities.FOOD).getQuantity().modifyFlat(AodMemFlags.POP_FOOD_DEMAND_5, +2, FoodCostDesc + marketSize);
                break;
            case 6:
            case 7:
                resetDemmand(ind);
                ind.getDemand(Commodities.FOOD).getQuantity().modifyFlat(AodMemFlags.POP_FOOD_DEMAND_6_AND_7, +1, FoodCostDesc + marketSize);
                break;
            case 8:
            case 9:
                resetDemmand(ind);
                ind.getDemand(Commodities.FOOD).getQuantity().modifyFlat(AodMemFlags.POP_FOOD_DEMAND_8_AND_9, +2, FoodCostDesc + marketSize);
                break;
            case 10:
                resetDemmand(ind);
                ind.getDemand(Commodities.FOOD).getQuantity().modifyFlat(AodMemFlags.POP_FOOD_DEMAND_10, +7, FoodCostDesc + marketSize);
                break;
        }
    }

    public void applyOtherDemmands(BaseIndustry ind) {

        int marketSize = ind.getMarket().getSize();
        ind.demand(AodCommodities.BIOTICS,marketSize-2);
        ind.getDemand(AodCommodities.BIOTICS).getQuantity().unmodify(getModId());
        ind.demand(AodCommodities.RECITIFICATES,marketSize-1);
        ind.getDemand(AodCommodities.RECITIFICATES).getQuantity().unmodify(getModId());
        ind.demand(AodCommodities.WATER,marketSize-4);
        ind.getDemand(AodCommodities.WATER).getQuantity().unmodify(getModId());
        Pair<String, Integer> deficit = ind.getMaxDeficit(AodCommodities.RECITIFICATES,AodCommodities.BIOTICS,AodCommodities.WATER);
        int maxDeficit = 2; // missing ship parts do not affect the output much, they just reduce quality.
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;

        applyDeficitToIndustry(ind, 4, deficit,
                Commodities.CREW,Commodities.ORGANS,Commodities.DRUGS);
    }
    public void applyDeficitToIndustry(Industry ind, int index, Pair<String, Integer> deficit, String... commodities){
        for (String commodity : commodities) {
            if (!ind.getSupply(commodity).getQuantity().isUnmodified()) {
                ind.supply(String.valueOf(index), commodity, -(Integer) deficit.two, BaseIndustry.getDeficitText((String) deficit.one));
            }
        }
    }
    @Override
    public boolean showIcon() {
        return false;
    }
    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition(FoodCostDesc)){
            marketAPI.addCondition(FoodCostDesc);
        }
    }
    public void resetDemmand(Industry ind){
        ind.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(AodMemFlags.POP_FOOD_DEMAND_3_AND_4);
        ind.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(AodMemFlags.POP_FOOD_DEMAND_5);
        ind.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(AodMemFlags.POP_FOOD_DEMAND_6_AND_7);
        ind.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(AodMemFlags.POP_FOOD_DEMAND_8_AND_9);
        ind.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(AodMemFlags.POP_FOOD_DEMAND_10);
    }
}
