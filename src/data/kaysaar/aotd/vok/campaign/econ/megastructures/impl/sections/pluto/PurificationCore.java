package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.pluto;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.util.LinkedHashMap;

public class PurificationCore extends PlutoForgeSection {
    @Override
    public LinkedHashMap<String, Integer> getProductionMap() {
        LinkedHashMap<String,Integer>res = new LinkedHashMap<>();
        res.put(AoTDCommodities.REFINED_METAL,2);
        res.put(AoTDCommodities.PURIFIED_TRANSPLUTONICS,1);
        return res;
    }
    public LinkedHashMap<String,String>getProdMapForOre(){
        LinkedHashMap<String,String>res = new LinkedHashMap<>();
        res.put(AoTDCommodities.REFINED_METAL,Commodities.ORE);
        res.put(AoTDCommodities.PURIFIED_TRANSPLUTONICS,Commodities.RARE_ORE);
        return res;
    }
}
