package data.kaysaar.aotd.vok.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.SMSpecialItem;
import data.kaysaar.aotd.vok.campaign.econ.items.ModularConstructorPlugin;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class AoDUtilis {
    public static final String reason = "This industry cannot be downgraded due to starsector API limitations.";

    public static boolean checkIfResearched(String id) {
        Map<String, Boolean> researchSaved = (HashMap<String, Boolean>) Global.getSector().getPersistentData().get("researchsaved");
        return researchSaved != null ? researchSaved.get(id) : false;
    }

    public static String consumeReq(String industryId) {
        return ModularConstructorPlugin.retrieveNameForReq(industryId) + " is required to be installed in " + Global.getSettings().getIndustrySpec(industryId).getName();

    }

    public static boolean canInstallItem(Industry industry, String itemID) {
        if (industry == null || itemID == null) {
            return false;
        }
        SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(itemID);
        //Also from SirHatley this has been taken

        //check if it's applicable to the industry
        boolean isApplicableToIndustry = Arrays.asList(spec.getParams().replaceAll("\\s", "").split(",")).contains(industry.getSpec().getId());
        //check if it has unmet requirements on the market
        return isApplicableToIndustry && ItemEffectsRepo.ITEM_EFFECTS.get(itemID).getUnmetRequirements(industry).isEmpty();

    }

    public static float calculatePercentOfProgression(ResearchOption option) {


        if(option.isResearched)return 1f;
        return (float) (option.daysSpentOnResearching / (getDaysFromResearch(option)));
    }
    public static float getDaysFromResearch(ResearchOption option){
        float multiplier = AoTDSettingsManager.getFloatValue(AoTDSettingsManager.AOTD_RESEARCH_SPEED_MULTIPLIER);
        float defProgression = 1f;
        float actualProgression = defProgression+(defProgression*AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchSpeedBonus().getModifiedValue());
        float days = option.getSpec().getTimeToResearch();
        days*=multiplier;
        days/=actualProgression;
        return days;
    }
    public static boolean checkForFamilyIndustryInstance(MarketAPI marketAPI, String industryIdToIgnoire, String category, String Base, Industry.IndustryTooltipMode mode) {

        for (Industry industry : marketAPI.getIndustries()) {
            if (industry.getId().equals(industryIdToIgnoire)) {
                if (Global.getSettings().getIndustrySpec(Base).getUpgrade() != null) continue;
            }
            for (String tag : industry.getSpec().getTags()) {
                if (tag.equals(AoTDDataInserter.AOTD + category) && !industry.getId().equals(Base)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getNormalOreAmount(MarketAPI market) {
        int howMuchToProduce = -15;
        if (market.hasCondition(Conditions.ORE_SPARSE)) {
            howMuchToProduce = -1;
        }
        if (market.hasCondition(Conditions.ORE_MODERATE)) {
            howMuchToProduce = 0;
        }
        if (market.hasCondition(Conditions.ORE_ABUNDANT)) {
            howMuchToProduce = 1;
        }
        if (market.hasCondition(Conditions.ORE_RICH)) {
            howMuchToProduce = 2;
        }
        if (market.hasCondition(Conditions.ORE_ULTRARICH)) {
            howMuchToProduce = 3;
        }

        return howMuchToProduce;
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

    public static int getOrganicsAmount(MarketAPI market) {
        int howMuchToProduce = -15;
        if (market.hasCondition(Conditions.ORGANICS_TRACE)) {
            howMuchToProduce = -1;
        }
        if (market.hasCondition(Conditions.ORGANICS_COMMON)) {
            howMuchToProduce = 0;
        }
        if (market.hasCondition(Conditions.ORGANICS_ABUNDANT)) {
            howMuchToProduce = 1;
        }
        if (market.hasCondition(Conditions.ORGANICS_PLENTIFUL)) {
            howMuchToProduce = 2;
        }


        return howMuchToProduce;
    }
    public static boolean isMiningAvailable(MarketAPI marketAPI){
        return AoDUtilis.getOrganicsAmount(marketAPI)>=-1 || AoDUtilis.getNormalOreAmount(marketAPI) >=-1 || AoDUtilis.getRareOreAmount(marketAPI) >= -1 ||AoDUtilis.getVolatilesAmount(marketAPI)>=-1;
    }

    public static boolean checkForItemBeingInstalled(MarketAPI market, String industryId, String itemId) {
        if (AoTDMainResearchManager.getInstance().getSpecificFactionManager(market.getFaction()).haveResearched(AoTDTechIds.MEGA_ASSEMBLY_SYSTEMS)) {
            return true;
        }
        if (market.getIndustry(industryId) == null) {
            return false;
        }
        if (market.getIndustry(industryId).getSpecialItem() == null) {
            return false;
        }
        if (market.getIndustry(industryId).getSpecialItem().getId().equals(itemId)) {
            return true;
        }
        return false;
    }

    public static void ensureIndustryHasNoItem(Industry ind) {
        if (ind.getSpecialItem() != null) {
            Misc.getStorageCargo(ind.getMarket()).addSpecial(ind.getSpecialItem(), 1);
            ind.setSpecialItem(null);
        }
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static int getRareOreAmount(MarketAPI market) {
        int howMuchToProduce = -10;
        if (market.hasCondition(Conditions.RARE_ORE_SPARSE)) {
            howMuchToProduce = -1;
        }
        if (market.hasCondition(Conditions.RARE_ORE_MODERATE)) {
            howMuchToProduce = 0;
        }
        if (market.hasCondition(Conditions.RARE_ORE_ABUNDANT)) {
            howMuchToProduce = 1;
        }
        if (market.hasCondition(Conditions.RARE_ORE_RICH)) {
            howMuchToProduce = 2;
        }
        if (market.hasCondition(Conditions.RARE_ORE_ULTRARICH)) {
            howMuchToProduce = 3;
        }


        return howMuchToProduce;
    }

    public static int getFoodQuantityBonus(MarketAPI market) {
        int quantity = -20;

        if (market.hasCondition(Conditions.FARMLAND_POOR)) {
            if (quantity == -20) {
                quantity = 0;
            }
            quantity -= 1;
        }
        if (market.hasCondition(Conditions.FARMLAND_ADEQUATE)) {
            if (quantity == -20) {
                quantity = 0;
            }
        }
        if (market.hasCondition(Conditions.FARMLAND_RICH)) {
            if (quantity == -20) {
                quantity = 0;
            }
            quantity += 1;
        }
        if (market.hasCondition(Conditions.FARMLAND_BOUNTIFUL)) {
            if (quantity == -20) {
                quantity = 0;
            }
            quantity += 2;
        }
        if (market.hasCondition(Conditions.SOLAR_ARRAY)) {
            quantity += 2;
        }
        return quantity;
    }

    public static Pair<String, Integer> getProductionBonusFromCondition(MarketAPI market, String conditionId, int bonusProduction) {
        if (market.hasCondition(conditionId)) {
            Pair<String, Integer> bonus = new Pair<>(Global.getSettings().getMarketConditionSpec(conditionId).getName(), bonusProduction);
            return bonus;
        }
        return null;
    }

    public static ArrayList<SMSpecialItem> getSpecItemsForManufactoriumData() {
        return (ArrayList<SMSpecialItem>) Global.getSector().getPersistentData().get("$stella_manufactorium_items");
    }

    public static BaseIndustry getTTShipyard(FactionAPI factionAPI) {
        for (MarketAPI factionMarket : Misc.getFactionMarkets(factionAPI)) {
            if (factionMarket.hasIndustry(AoTDIndustries.ORBITAL_SKUNKWORK) && factionMarket.getIndustry(AoTDIndustries.ORBITAL_SKUNKWORK).isFunctional()) {
               return (BaseIndustry) factionMarket.getIndustry(AoTDIndustries.ORBITAL_SKUNKWORK);
            }
        }
        return null;
    }
}
