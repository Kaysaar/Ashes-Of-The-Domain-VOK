package data.kaysaar.aotd.vok.campaign.econ.synergies;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import kaysaar.bmo.buildingmenu.BuildingMenuMisc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fs.starfarer.api.impl.campaign.econ.impl.TechMining.TECH_MINING_MULT;

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
    public static boolean isIndustryFunctionalAndExistingIncludingUpgrades(MarketAPI market, String... ids) {
        for (String id : ids) {
            if(!isAtLeastOneIndustryFunctionalFromList(market, getIdsOfTreeFromIndustry(id).toArray(new String[0]))) return false;
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
    public static float getTechMiningMult(MarketAPI market) {
        MemoryAPI mem = market.getMemoryWithoutUpdate();
        if (mem.contains(TECH_MINING_MULT)) {
            return mem.getFloat(TECH_MINING_MULT);
        }
        mem.set(TECH_MINING_MULT, 1f);
        return 1f;
    }
    public static float getEffectivenessMult(MarketAPI market) {
        float mult = market.getStats().getDynamic().getStat(Stats.TECH_MINING_MULT).getModifiedValue();
        return mult;
    }

    public static String getIndustriesListed(List<String> ids , MarketAPI marketAPI){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            builder.append(getIndustryName(marketAPI,ids.get(i)));
            if (i < ids.size() - 1) {
                builder.append(" / ");
            }
        }
        return builder.toString();
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
    public static Industry getIndustryFromUpgradeTree(String industryId, MarketAPI market){
        for (String id : getIdsOfTreeFromIndustry(industryId)) {
            if(market.hasIndustry(id)){
                return market.getIndustry(id);
            }
        }
        return null;
    }
    public static ArrayList<String>getIdsOfIndustryWithSameTag(String tag,MarketAPI market){
        ArrayList<String>results =new ArrayList<>();
        Global.getSettings().getAllIndustrySpecs().stream().filter(x->x.hasTag(tag)&&!x.hasTag(Tags.INDUSTRY_DO_NOT_SHOW_IN_BUILD_DIALOG)&&(getProperlyInstance(market,x.getId()).isAvailableToBuild())).forEach(x->results.add(x.getId()));
        return results;
    }
    public static Industry getProperlyInstance(MarketAPI market,String id){
        Industry ind = Global.getSettings().getIndustrySpec(id).getNewPluginInstance(market);
        return ind;
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
