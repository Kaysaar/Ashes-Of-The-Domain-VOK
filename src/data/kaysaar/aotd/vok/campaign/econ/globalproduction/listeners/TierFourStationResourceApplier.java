package data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.models.AoTDResourceListener;
import data.kaysaar.aotd.vok.campaign.econ.industry.TierFourStation;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.util.HashMap;

public class TierFourStationResourceApplier implements AoTDResourceListener {
    @Override
    public HashMap<String,Integer> increaseProductionCapacity(Object toIgnore) {
//        HashMap<String,Integer>map = new HashMap<>();
//        for (GPBaseMegastructure megastructure : GPManager.getInstance().getMegastructures()) {
//            HashMap<String,Integer> megaMap = megastructure.getProduction();
//            HashMap<String,Integer>demandMap = megastructure.getDemand();
//            for (Map.Entry<String, Integer> entry : megaMap.entrySet()) {
//                AoTDMisc.putCommoditiesIntoMap(map,entry.getKey(),entry.getValue());
//            }
//        }
//        return map;
        return new HashMap<>();
    }

    @Override
    public HashMap<String, Integer> increaseDemand() {
        HashMap<String,Integer>demand = new HashMap<>(TierFourStation.costMap);
        int amount =0;
        for (MarketAPI playerFactionMarket : AoTDMisc.getPlayerFactionMarkets()) {
            if(playerFactionMarket.hasIndustry("starfortress")){
                if(playerFactionMarket.getIndustry("starfortress").isUpgrading()){
                    amount++;
                }
            }
        }
        float percent = 1f;
        if(AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.SUPERTENCILES)){
            percent =0.8f;
        }
        for (String entry : demand.keySet()) {
            demand.put(entry, (int) (demand.get(entry)*amount*percent));
        }
        return demand;
    }
}
