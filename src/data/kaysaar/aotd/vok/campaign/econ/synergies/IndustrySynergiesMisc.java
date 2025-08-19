package data.kaysaar.aotd.vok.campaign.econ.synergies;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import kaysaar.bmo.buildingmenu.BuildingMenuMisc;

import java.util.ArrayList;
import java.util.Arrays;

public class IndustrySynergiesMisc {
    public static boolean isIndustryFunctionalAndExisting(MarketAPI market, String... ids) {
        for (String id : ids) {
            if (market.hasIndustry(id)) {
                if (!market.getIndustry(id).isFunctional()) return false;
            } else {
                return false;
            }

        }
        return true;

    }
    public static boolean isAtLeastOneIndustryFunctionalFromList(MarketAPI market, String... ids) {
        for (String id : ids) {
            if (market.hasIndustry(id)) {
                if (market.getIndustry(id).isFunctional()) return true;
            }

        }
        return false;

    }
    public static boolean isAtLeastOneIndustryFunctionalFromListIncludingUpgrades(MarketAPI market, String... ids) {
        for (String id : ids) {
            if(isAtLeastOneIndustryFunctionalFromList(market, getIdsOfTreeFromIndustry(id).toArray(new String[0]))) return true;
        }
        return false;

    }
    public static ArrayList<String>getIdsOfTreeFromIndustry(String industryId){
        ArrayList<String>results =new ArrayList<>();
        results.add(industryId);
        BuildingMenuMisc.getIndustryTree(industryId).forEach(x->results.add(x.getId()));
        return results;
    }
    public static ArrayList<String>getIdsOfTreeFromIndustryTrimmed(String industryId,String... toTrim){
        ArrayList<String>results =new ArrayList<>();
        results.add(industryId);
        BuildingMenuMisc.getIndustryTree(industryId).forEach(x->results.add(x.getId()));
        Arrays.stream(toTrim).forEach(results::remove);
        return results;
    }
    public static String getIndustryName(MarketAPI market, String id) {
        if(market==null){
            return Global.getSettings().getIndustrySpec(id).getName();
        }
        return  Global.getSettings().getIndustrySpec(id).getNewPluginInstance(market).getCurrentName();

    }
    public static boolean didMarketMetTechCriteria(MarketAPI market, String... techs){
        for (String tech : techs) {
            if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(tech,market)){
                return false;
            }
        }
        return true;
    }
}
