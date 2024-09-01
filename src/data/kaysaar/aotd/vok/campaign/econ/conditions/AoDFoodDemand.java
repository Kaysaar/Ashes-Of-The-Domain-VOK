package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDMemFlags;


public class AoDFoodDemand extends BaseMarketConditionPlugin {

    public static String FoodCostDesc = "AoDFoodDemand";

    @Override
    public void apply(String id) {
        super.apply(id);
        if (!market.hasIndustry(Industries.POPULATION)) return;
        applyFoodRequirements(market.getIndustry(Industries.POPULATION));
        applyOtherDemmands((BaseIndustry) market.getIndustry(Industries.POPULATION));

    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        if (!market.hasIndustry(Industries.POPULATION)) return;
        Industry ind = market.getIndustry(Industries.POPULATION);
        resetDemmand(ind);
        unapplyOtherDemand((BaseIndustry) ind);
        market.getStability().unmodifyFlat("aotd_water");
        market.getIncoming().set("aotd_water", 0);
        market.getAccessibilityMod().unmodifyFlat("aotd_water");
    }

    public void unapplyOtherDemand(BaseIndustry ind) {
        ind.supply(AoTDCommodities.WATER, 0, "");
        ind.demand(AoTDCommodities.WATER, 0, "");

    }

    public void applyFoodRequirements(Industry ind) {

        int marketSize = ind.getMarket().getSize();

        switch (marketSize) {
            case 3:
            case 4:
                resetDemmand(ind);
                ind.getDemand(Commodities.FOOD).getQuantity().modifyFlat(AoTDMemFlags.POP_FOOD_DEMAND_3_AND_4, +1, FoodCostDesc);
                break;
            case 5:
                resetDemmand(ind);
                ind.getDemand(Commodities.FOOD).getQuantity().modifyFlat(AoTDMemFlags.POP_FOOD_DEMAND_5, +2, FoodCostDesc + marketSize);
                break;
            case 6:
            case 7:
                resetDemmand(ind);
                ind.getDemand(Commodities.FOOD).getQuantity().modifyFlat(AoTDMemFlags.POP_FOOD_DEMAND_6_AND_7, +1, FoodCostDesc + marketSize);
                break;
            case 8:
            case 9:
                resetDemmand(ind);
                ind.getDemand(Commodities.FOOD).getQuantity().modifyFlat(AoTDMemFlags.POP_FOOD_DEMAND_8_AND_9, +2, FoodCostDesc + marketSize);
                break;
            case 10:
                resetDemmand(ind);
                ind.getDemand(Commodities.FOOD).getQuantity().modifyFlat(AoTDMemFlags.POP_FOOD_DEMAND_10, +7, FoodCostDesc + marketSize);
                break;
        }
    }

    public int calcuateWaterDemand(MarketAPI market) {
        if (market.getPlanetEntity() == null) {
            return market.getSize();
        }
        String planetType = market.getPlanetEntity().getTypeId();
        if (planetType.contains("arid")) {
            return market.getSize() + 1;
        }
        if (planetType.contains("barren")) {
            return market.getSize() + 2;
        }
        if (planetType.contains("barren_desert")) {
            return market.getSize() + 4;
        }
        if (planetType.contains("cryovolcanic")) {
            return market.getSize();
        }
        if (planetType.contains("desert")) {
            return market.getSize() + 4;
        }
        if (planetType.contains("frozen")) {
            return market.getSize() - 2;
        }
        if (planetType.contains("gas_giant")) {
            return market.getSize();
        }
        if (planetType.contains("ice_giant")) {
            return market.getSize() - 2;
        }
        if (planetType.contains("irradiated")) {
            return market.getSize() + 2;
        }
        if (planetType.contains("jungle")) {
            return market.getSize() - 2;
        }
        if (planetType.contains("rocky_ice")) {
            return market.getSize() - 2;
        }
        if (planetType.contains("rocky_metalic")) {
            return market.getSize() + 2;
        }
        if (planetType.contains("rocky_unstable")) {
            return market.getSize() + 2;
        }
        if (planetType.contains("terran")) {
            return market.getSize() - 4;
        }
        if (planetType.contains("terran_eccentric")) {
            return market.getSize() - 2;
        }
        if (planetType.contains("toxic")) {
            return market.getSize() + 2;
        }
        if (planetType.contains("tundra")) {
            return market.getSize();
        }
        if (planetType.contains("volcanic")) {
            return market.getSize() + 6;
        }
        if (planetType.contains("water")) {
            return 0;
        }
        if (planetType.contains("archipelago")) {
            return 0;
        }
        return market.getSize();
    }

    public int calcuateWaterSupply(MarketAPI market) {
        if (market.getPlanetEntity() == null) {
            return 0;
        }
        String planetType = market.getPlanetEntity().getTypeId();
        if (planetType.contains("frozen")) {
            return market.getSize() + 2;
        }
        if (planetType.contains("ice_giant")) {
            return market.getSize();
        }
        if (planetType.contains("jungle")) {
            return market.getSize() + 4;
        }
        if (planetType.contains("rocky_ice")) {
            return market.getSize() + 3;
        }
        if (planetType.contains("terran")) {
            return market.getSize();
        }
        if (planetType.contains("terran_eccentric")) {
            return market.getSize() - 2;
        }
        if (planetType.contains("water")) {
            return  market.getSize() + 6;
        }
        if (planetType.contains("archipelago")) {
            return market.getSize() + 6;
        }
        return 0;
    }

    public void applyOtherDemmands(BaseIndustry ind) {
        ind.demand(AoTDCommodities.WATER, calcuateWaterDemand(ind.getMarket()));
        ind.getDemand(AoTDCommodities.WATER).getQuantity().unmodify(getModId());
        ind.supply(AoTDCommodities.WATER, calcuateWaterSupply(ind.getMarket()));
        ind.getSupply(AoTDCommodities.WATER).getQuantity().unmodify(getModId());
        Pair<String, Integer> deficit = ind.getMaxDeficit(AoTDCommodities.WATER);
        applyDeficitToIndustry(ind, 4, deficit,
                Commodities.CREW, Commodities.ORGANS, Commodities.DRUGS);
        if (deficit.two > 0) {
            market.getStability().modifyFlat("aotd_water", -deficit.two, "Water shortages");
            market.getIncoming().add("water", -10f * deficit.two);
        }
        if(!market.isPlayerOwned()&&!market.getFaction().isPlayerFaction()){
            market.getAccessibilityMod().modifyFlat("aotd_water",0.2f);
        }


    }

    public void applyDeficitToIndustry(Industry ind, int index, Pair<String, Integer> deficit, String... commodities) {
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
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition(FoodCostDesc)) {
            marketAPI.addCondition(FoodCostDesc);
        }
    }

    public void resetDemmand(Industry ind) {
        ind.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(AoTDMemFlags.POP_FOOD_DEMAND_3_AND_4);
        ind.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(AoTDMemFlags.POP_FOOD_DEMAND_5);
        ind.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(AoTDMemFlags.POP_FOOD_DEMAND_6_AND_7);
        ind.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(AoTDMemFlags.POP_FOOD_DEMAND_8_AND_9);
        ind.getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(AoTDMemFlags.POP_FOOD_DEMAND_10);
    }
}
