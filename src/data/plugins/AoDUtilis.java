package data.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AoDConditions;
import data.Ids.AoDIndustries;
import data.Ids.AodMemFlags;
import data.Ids.AodResearcherSkills;
import data.scripts.campaign.econ.SMSpecialItem;
import data.scripts.research.ResearchAPI;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.magiclib.util.MagicSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data.plugins.AoDCoreModPlugin.aotdDatabankRepo;


public class AoDUtilis {
    public static int MIN_SIZE = MagicSettings.getInteger("aod_core","CAPITAL_MIN_FACTION_SIZE");
    public static final String reason= "We can't downgrade this industry safely without researching it first";
    public static  boolean checkIfResearched(String id) {
        Map<String,Boolean> researchSaved = (HashMap<String, Boolean>) Global.getSector().getPersistentData().get("researchsaved");
        return researchSaved != null ? researchSaved.get(id) : false;
    }

    public static  float researchBonusCurrent(){
        float toReturn = 0f;
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(false)) {
            for (Industry industry : playerMarket.getIndustries()) {
                if(industry.isFunctional()&&industry.getId().equals(AoDIndustries.RESEARCH_CENTER)){
                    if(toReturn==0){
                        toReturn=1f;
                        continue;
                    }
                    toReturn+=0.1f;

                }
            }
        }
        return  toReturn;
    }
    public static int getNormalOreAmount(MarketAPI market){
        int howMuchToProduce = -15;
        if(market.hasCondition(Conditions.ORE_SPARSE)){
            howMuchToProduce = -1;
        }
        if(market.hasCondition(Conditions.ORE_MODERATE)){
            howMuchToProduce = 0;
        }
        if(market.hasCondition(Conditions.ORE_ABUNDANT)){
            howMuchToProduce = 1;
        }
        if(market.hasCondition(Conditions.ORE_RICH)){
            howMuchToProduce = 2;
        }
        if(market.hasCondition(Conditions.ORE_ULTRARICH)){
            howMuchToProduce = 3;
        }

        return  howMuchToProduce;
    }
    public static int getVolatilesAmount(MarketAPI market) {
        int howMuchToProduce = -15;
        if (market.hasCondition(Conditions.VOLATILES_TRACE)) {
            howMuchToProduce = -1;
        }
        if (market.hasCondition(Conditions.VOLATILES_DIFFUSE)) {
            howMuchToProduce = 0;
        }
        if (market.hasCondition(Conditions.VOLATILES_ABUNDANT)) {
            howMuchToProduce = 1;
        }
        if (market.hasCondition(Conditions.VOLATILES_PLENTIFUL)) {
            howMuchToProduce = 2;
        }

        return howMuchToProduce;
    }
    public static int getOrganicsAmount(MarketAPI market){
        int howMuchToProduce = -15;
        if(market.hasCondition(Conditions.ORGANICS_TRACE)){
            howMuchToProduce = -1;
        }
        if(market.hasCondition(Conditions.ORGANICS_COMMON)){
            howMuchToProduce = 0;
        }
        if(market.hasCondition(Conditions.ORGANICS_ABUNDANT)){
            howMuchToProduce = 1;
        }
        if(market.hasCondition(Conditions.ORGANICS_PLENTIFUL)){
            howMuchToProduce = 2;
        }


        return  howMuchToProduce;
    }
    public static  int getRareOreAmount(MarketAPI market){
        int howMuchToProduce = -10;
        if(market.hasCondition(Conditions.RARE_ORE_SPARSE)){
            howMuchToProduce = -1;
        }
        if(market.hasCondition(Conditions.RARE_ORE_MODERATE)){
            howMuchToProduce = 0;
        }
        if(market.hasCondition(Conditions.RARE_ORE_ABUNDANT)){
            howMuchToProduce = 1;
        }
        if(market.hasCondition(Conditions.RARE_ORE_RICH)){
            howMuchToProduce = 2;
        }
        if(market.hasCondition(Conditions.RARE_ORE_ULTRARICH)){
            howMuchToProduce = 3;
        }


        return  howMuchToProduce;
    }

    public static  int getFoodQuantityBonus(MarketAPI market) {
        int quantity = -20;

        if(market.hasCondition(Conditions.FARMLAND_POOR)){
            if(quantity==-20){
                quantity=0;
            }
            quantity -=1;
        }
        if(market.hasCondition(Conditions.FARMLAND_ADEQUATE)){
            if(quantity==-20){
                quantity=0;
            }
        }
        if(market.hasCondition(Conditions.FARMLAND_RICH)){
            if(quantity==-20){
                quantity=0;
            }
            quantity +=1;
        }
        if(market.hasCondition(Conditions.FARMLAND_BOUNTIFUL)){
            if(quantity==-20){
                quantity=0;
            }
            quantity +=2;
        }
        if(market.hasCondition(Conditions.SOLAR_ARRAY)){
            quantity +=2;
        }
        return quantity;
    }
    public static Pair<String,Integer> getProductionBonusFromCondition(MarketAPI market,String conditionId, int bonusProduction){
        if(market.hasCondition(conditionId)){
            Pair<String,Integer> bonus = new Pair<>(Global.getSettings().getMarketConditionSpec(conditionId).getName(),bonusProduction);
            return bonus;
        }
      return null;
    }
    public static boolean isFactionPossesingTriTachyonShipyards(FactionAPI factionAPI){
        for (MarketAPI factionMarket : Misc.getFactionMarkets(factionAPI)) {
            if(factionMarket.hasIndustry(AoDIndustries.TRI_TACHYON_HEAVY)&&factionMarket.getIndustry(AoDIndustries.TRI_TACHYON_HEAVY).isFunctional()){

                return true;

            }

        }
        return false;
    }
    public static float productionQuality(FactionAPI factionAPI){
        float beginer =0;
        for (MarketAPI factionMarket : Misc.getFactionMarkets(factionAPI)) {
            if (factionMarket.getStats().getDynamic().getStat(Stats.PRODUCTION_QUALITY_MOD).getModifiedValue()>=beginer) {
                beginer = factionMarket.getStats().getDynamic().getStat(Stats.PRODUCTION_QUALITY_MOD).getModifiedValue();
            }
        }
        return beginer;
    }
    public static boolean checkForFamilyIndustryInstance(MarketAPI marketAPI, String industryIdToIgnoire,String category,String Base){
        for (Industry industry : marketAPI.getIndustries()) {

            if(category.equals(Industries.HEAVYINDUSTRY)&&industry.getId().equals("yunru_heavyindustry")) return false;
            if(industry.getId().equals(category)){
                if(Global.getSettings().getIndustrySpec(industry.getSpec().getDowngrade())==null){
                    return true;
                }
               else if(Global.getSettings().getIndustrySpec(industry.getSpec().getDowngrade()).getUpgrade()==null){
                    return true;
                }
               else if(!Global.getSettings().getIndustrySpec(industry.getSpec().getDowngrade()).getUpgrade().equals(industryIdToIgnoire)){
                    return true;
                }
                else{
                    continue;
                }
            }
            if(industry.getId().equals(industryIdToIgnoire))continue;

            for (String tag : industry.getSpec().getTags()) {
                if(tag.equals(category)&&!industry.getId().equals(Base)){
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isResearched(String id) {
        Map<String,Boolean> researchSaved = (HashMap<String, Boolean>) Global.getSector().getPersistentData().get("researchsaved");
        return researchSaved != null ? researchSaved.get(id) : false;
    }
    public static ResearchAPI getResearchAPI(){
        return (ResearchAPI) Global.getSector().getPersistentData().get(AoDCoreModPlugin.aodTech);
    }
//     public static void reapplyIndustry(@NotNull MarketAPI marketAPI, String industryId) {
//        if (marketAPI.hasIndustry(industryId)) {
//            float curr_upgrade = 0;
//
//            IndustrySpecAPI industrySpecAPI = Global.getSettings().getIndustrySpec(marketAPI.getIndustry(industryId).getSpec().getUpgrade());
//            boolean isUpgrading = marketAPI.getIndustry(industryId).isUpgrading();
//            if (isUpgrading) {
//                String[] testBuildtime = marketAPI.getIndustry(industryId).getBuildOrUpgradeDaysText().split(" ");
//                curr_upgrade = Integer.parseInt(testBuildtime[0]);
//            }
//            Industry ind = marketAPI.getIndustry(industryId);
//            SpecialItemData specialItemData = ind.getSpecialItem();
//            String aiCore = ind.getAICoreId();
//            boolean improved = ind.isImproved();
//            marketAPI.removeIndustry(industryId, null, false);
//            marketAPI.addIndustry(industryId);
//            marketAPI.getIndustry(industryId).setSpecialItem(specialItemData);
//            marketAPI.getIndustry(industryId).setImproved(improved);
//            marketAPI.getIndustry(industryId).setAICoreId(aiCore);
//            if (isUpgrading) {
//                float default_time = industrySpecAPI.getBuildTime();
//                industrySpecAPI.setBuildTime(curr_upgrade);
//                marketAPI.getIndustry(industryId).startUpgrading();
//                industrySpecAPI.setBuildTime(default_time);
//            }
//        }
//
//    }
    public static void insertOPScientist(PersonAPI person) {
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        person.setId(AoDCoreModPlugin.opScientist);
        person.setVoice(Voices.SCIENTIST);
        person.getTags().add("aotd_researcher");
        Global.getSector().getMemory().set("$aotd_researcher_done",true);
        Global.getSector().getMemory().set("$aotd_researcher_name",person.getName().getFirst());
        person.getTags().add(AodResearcherSkills.SEEKER_OF_KNOWLEDGE);
        if (!ip.containsPerson(person)) {
            ip.addPerson(person);
        }
    }
    public static void insertGalatiaScientist() {
        PersonAPI person = Global.getSector().getFaction(Factions.INDEPENDENT).createRandomPerson();
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        person.setId(AoDCoreModPlugin.galatiaScientist);
        if (!ip.containsPerson(person)) {
            ip.addPerson(person);
        }
    }
    public static ArrayList<SMSpecialItem> getSpecItemsForManufactoriumData(){
        return (ArrayList<SMSpecialItem>) Global.getSector().getPersistentData().get("$stella_manufactorium_items");
    }
    public static void InsertSpecItemsForManufactoriumData() throws JSONException, IOException {
        JSONArray json =Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/campaign/stella_manufactorium.csv", "aod_core");
        ArrayList<SMSpecialItem> insertedSpecItemForManufactorium = new ArrayList<SMSpecialItem>();
        for(int i=0;i<json.length();i++){
            JSONObject obj = json.getJSONObject(i);
            String id = obj.getString("id");
            if(id==null||id.isEmpty()) continue;

            String itemCostRaw = obj.getString("resoruces_to_make_one");
            float dayCost = Float.parseFloat(obj.getString("time_to_make_one"));
            HashMap<String,Integer> itemCost = getItemCost(itemCostRaw);
            insertedSpecItemForManufactorium.add(new SMSpecialItem(itemCost,id,dayCost));

        }
        Global.getSector().getPersistentData().put("$stella_manufactorium_items",insertedSpecItemForManufactorium);
    }
    public static HashMap<String,Integer>getItemCost(String reqItems){
        String[] splitedAll = reqItems.split(",");
        HashMap<String, Integer> itemsReq = new HashMap<>();
        for (String s : splitedAll) {
            String[] splitedInstance = s.split(":");
            if (Integer.parseInt(splitedInstance[1]) > 0) {
                itemsReq.put(splitedInstance[0], Integer.parseInt(splitedInstance[1]));
            }

        }

        return itemsReq;
    }
    public static ArrayList<SectorEntityToken> getAllBifrostGates(){
        ArrayList<SectorEntityToken> toReturn = new ArrayList<>();
        for (MarketAPI factionMarket : Misc.getFactionMarkets(Global.getSector().getPlayerFaction())) {
            for (SectorEntityToken connectedEntity : factionMarket.getConnectedEntities()) {
                if(connectedEntity.hasTag("bifrost")){
                    toReturn.add(connectedEntity);
                }
            }
        }
        if(toReturn.isEmpty()){
            return null;
        }
        return toReturn;
    }
    public static boolean canExperimental(){
        for (MarketAPI factionMarket : Misc.getFactionMarkets(Global.getSector().getPlayerFaction())) {
            if(!factionMarket.hasIndustry(AoDIndustries.RESEARCH_CENTER))continue;
            if(factionMarket.getIndustry(AoDIndustries.RESEARCH_CENTER).getSpecialItem()!=null){
                return true;
            }
        }
        return false;
    }
    public static ArrayList<String> getDatabankRepo(){
        return (ArrayList<String>) Global.getSector().getPersistentData().get(aotdDatabankRepo);
    }

}
