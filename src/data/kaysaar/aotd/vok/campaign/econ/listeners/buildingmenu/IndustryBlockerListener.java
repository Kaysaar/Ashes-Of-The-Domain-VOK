package data.kaysaar.aotd.vok.campaign.econ.listeners.buildingmenu;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import kaysaar.bmo.listeners.BuildingMenuListener;

import java.util.HashSet;

public class IndustryBlockerListener implements BuildingMenuListener {
    @Override
    public HashSet<String> addBuildingsToBeHidden(MarketAPI marketAPI) {
        HashSet<String> hiddenBuildings = new HashSet<>();
        if(marketAPI.hasIndustry(AoTDIndustries.UNDERWORLD)){
            hiddenBuildings.add(Industries.COMMERCE);
        }
        if(marketAPI.hasIndustry("nidavelir_complex")){
            hiddenBuildings.add(Industries.HEAVYINDUSTRY);
        }
        if(marketAPI.hasIndustry("pluto_station")){
            hiddenBuildings.add(AoTDIndustries.SMELTING);
            hiddenBuildings.add(AoTDIndustries.EXTRACTIVE_OPERATION);
        }
        if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.BASE_SHIP_HULL_ASSEMBLY,marketAPI)){
            hiddenBuildings.add(Industries.HEAVYINDUSTRY);
        }
        if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket("aotd_tech_antimater_production",marketAPI)){
            hiddenBuildings.add(Industries.FUELPROD);
        }
        return hiddenBuildings;
    }
}
