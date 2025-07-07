package data.kaysaar.aotd.vok.campaign.econ.growingdemand.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.FleetSizeMultiplier;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.SpaceDrugsDemand;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.UpkeepReductionDemand;

import java.util.LinkedHashMap;
import java.util.List;

public class GrowingDemandManager {
    public static String permaDataKey = "$aotd_growing_demand_mana";

    public static GrowingDemandManager getInstance() {
        if (Global.getSector().getPersistentData().get(permaDataKey) == null) {
            setInstance();
        }
        return (GrowingDemandManager) Global.getSector().getPersistentData().get(permaDataKey);
    }

    public LinkedHashMap<String, GrowingDemandScript> demandScripts;

    public List<GrowingDemandScript> getDemandScripts() {
        return demandScripts.values().stream().toList();
    }

    public static void applyHiddenCondition() {
        Global.getSector().getEconomy().getMarketsCopy().stream().filter(MarketAPI::isInEconomy).filter(x -> !x.hasCondition("aotd_growing_demand_applier")).forEach(x -> x.addCondition("aotd_growing_demand_applier"));

    }

    public static void setInstance() {
        Global.getSector().getPersistentData().put(permaDataKey, new GrowingDemandManager());
    }

    public GrowingDemandManager() {
        demandScripts = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> mapOfReplacement = new LinkedHashMap<>();
        mapOfReplacement.put(Commodities.HEAVY_MACHINERY, 1);

        UpkeepReductionDemand demand = new UpkeepReductionDemand(AoTDCommodities.DOMAIN_GRADE_MACHINERY, mapOfReplacement, 0.4f, 1.2f);
        demand.setCanDecrease(true);
        demand.setDecreaseRate(0.002f);
        demand.setGrowthAdditionalRate(0.02f);
        demand.setMaxMagnitude(0.5f);
        addDemand(AoTDCommodities.DOMAIN_GRADE_MACHINERY, demand);

        mapOfReplacement = new LinkedHashMap<>();
        mapOfReplacement.put(Commodities.FUEL, 1);

        FleetSizeMultiplier demand2 = new FleetSizeMultiplier("compound", mapOfReplacement, 0, 0.3f);
        demand2.setCanDecrease(true);
        demand2.setDecreaseRate(0.002f);
        demand2.setGrowthAdditionalRate(0.05f);
        demand2.setMaxMagnitude(0.5f);

        addDemand("compound", demand2);
        mapOfReplacement = new LinkedHashMap<>();
        mapOfReplacement.put(Commodities.DRUGS, 1);
        addDemand("wwlb_cerulean_vapors", new SpaceDrugsDemand("wwlb_cerulean_vapors", mapOfReplacement));

    }
    public boolean hasDemand(String id){
        return demandScripts.containsKey(id);
    }

    public void addDemand(String id, GrowingDemandScript script) {
        demandScripts.put(id, script);
    }

    public void advance(float amount) {
        demandScripts.values().forEach(script -> script.advance(amount));
        applyHiddenCondition();
    }
}
