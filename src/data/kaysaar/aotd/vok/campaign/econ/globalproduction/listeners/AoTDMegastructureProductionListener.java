package data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.models.AoTDResourceListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.HashMap;
import java.util.Map;

public class AoTDMegastructureProductionListener implements AoTDResourceListener {
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
}
