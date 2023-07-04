package data.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.util.Misc;
import data.Ids.AoDConditions;
import data.Ids.AoDIndustries;
import data.Ids.AodMemFlags;
import data.scripts.research.ResearchAPI;
import org.jetbrains.annotations.NotNull;
import org.magiclib.util.MagicSettings;

import java.util.HashMap;
import java.util.Map;


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
                if(tag.contains(category)&&!industry.getId().equals(Base)){
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
     public static void reapplyIndustry(@NotNull MarketAPI marketAPI, String industryId) {
        if (marketAPI.hasIndustry(industryId)) {
            float curr_upgrade = 0;

            IndustrySpecAPI industrySpecAPI = Global.getSettings().getIndustrySpec(marketAPI.getIndustry(industryId).getSpec().getUpgrade());
            boolean isUpgrading = marketAPI.getIndustry(industryId).isUpgrading();
            if (isUpgrading) {
                String[] testBuildtime = marketAPI.getIndustry(industryId).getBuildOrUpgradeDaysText().split(" ");
                curr_upgrade = Integer.parseInt(testBuildtime[0]);
            }
            Industry ind = marketAPI.getIndustry(industryId);
            SpecialItemData specialItemData = ind.getSpecialItem();
            String aiCore = ind.getAICoreId();
            boolean improved = ind.isImproved();
            marketAPI.removeIndustry(industryId, null, false);
            marketAPI.addIndustry(industryId);
            marketAPI.getIndustry(industryId).setSpecialItem(specialItemData);
            marketAPI.getIndustry(industryId).setImproved(improved);
            marketAPI.getIndustry(industryId).setAICoreId(aiCore);
            if (isUpgrading) {
                float default_time = industrySpecAPI.getBuildTime();
                industrySpecAPI.setBuildTime(curr_upgrade);
                marketAPI.getIndustry(industryId).startUpgrading();
                industrySpecAPI.setBuildTime(default_time);
            }
        }

    }

}
