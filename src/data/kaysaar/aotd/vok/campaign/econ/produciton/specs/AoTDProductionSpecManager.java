package data.kaysaar.aotd.vok.campaign.econ.produciton.specs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDItems;
import data.kaysaar.aotd.vok.campaign.econ.produciton.models.AoTDProductionManData;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.ProjectReward;

import java.util.*;

public class AoTDProductionSpecManager {
    public static LinkedHashSet<WeaponAPI.WeaponType> blackListedTypes = new LinkedHashSet<>();
    public static boolean loaded = false;

    public static void makeSureBlackListInitialized() {
        blackListedTypes.add(WeaponAPI.WeaponType.LAUNCH_BAY);
        blackListedTypes.add(WeaponAPI.WeaponType.BUILT_IN);
        blackListedTypes.add(WeaponAPI.WeaponType.DECORATIVE);
        blackListedTypes.add(WeaponAPI.WeaponType.SYSTEM);
        blackListedTypes.add(WeaponAPI.WeaponType.STATION_MODULE);
    }

    public static LinkedHashMap<String, AoTDProductionSpec> shipProdSpecs = new LinkedHashMap<>();
    public static LinkedHashMap<String, AoTDProductionSpec> weaponProdSpecs = new LinkedHashMap<>();

    public static LinkedHashMap<String, AoTDProductionSpec> fighterProdSpecs = new LinkedHashMap<>();

    public static LinkedHashMap<String, AoTDProductionSpec> specialItemProdSpecs = new LinkedHashMap<>();
    public static ArrayList<AoTDProductionManData>manData = new ArrayList<>();
    public static LinkedHashSet<String> orderedItemsForUI = new LinkedHashSet<>();
    static {
        orderedItemsForUI.add(Commodities.SHIPS);
        orderedItemsForUI.add(Commodities.HAND_WEAPONS);
        orderedItemsForUI.add(AoTDCommodities.ADVANCED_COMPONENTS);
        orderedItemsForUI.add(AoTDCommodities.REFINED_METAL);
        orderedItemsForUI.add(AoTDCommodities.PURIFIED_TRANSPLUTONICS);
        orderedItemsForUI.add(AoTDCommodities.DOMAIN_GRADE_MACHINERY);
        orderedItemsForUI.add(AoTDItems.TENEBRIUM_CELL);



    }
    public static LinkedHashMap<String,AoTDProductionSpec>getSpecsBasedOnType(AoTDProductionSpec.AoTDProductionSpecType type) {
        return switch (type) {
            case SHIP -> shipProdSpecs;
            case WEAPON -> weaponProdSpecs;
            case FIGHTER -> fighterProdSpecs;
            case COMMODITY_ITEM -> specialItemProdSpecs;
            case SPECIAL_ITEM -> specialItemProdSpecs;
        };
    }

    public static void generateSpecsForAllStuff() {
        if (loaded) return;

        makeSureBlackListInitialized();
        shipProdSpecs.clear();
        weaponProdSpecs.clear();
        fighterProdSpecs.clear();
        specialItemProdSpecs.clear();
        manData.clear();
        manData.addAll(AoTDProductionManData.getManufacturerDataFromCSV());
        for (ShipHullSpecAPI allShipHullSpec : Global.getSettings().getAllShipHullSpecs()) {
            if (allShipHullSpec.getHints().contains(ShipHullSpecAPI.ShipTypeHints.STATION)) continue;
            if (allShipHullSpec.getHints().contains(ShipHullSpecAPI.ShipTypeHints.MODULE)) continue;
            if (allShipHullSpec.getHints().contains(ShipHullSpecAPI.ShipTypeHints.UNBOARDABLE)) continue;
            if (allShipHullSpec.getHints().contains(ShipHullSpecAPI.ShipTypeHints.UNDER_PARENT)) continue;
            if (allShipHullSpec.getHullSize().equals(ShipAPI.HullSize.FIGHTER)) continue;
            if (allShipHullSpec.hasTag(Tags.MODULE_HULL_BAR_ONLY)) continue;
            if (Global.getSettings().getHullIdToVariantListMap().get(allShipHullSpec.getHullId()).isEmpty()) {
                boolean found = false;
                for (String allVariantId : Global.getSettings().getAllVariantIds()) {
                    if (allVariantId.contains(allShipHullSpec.getHullId())) {
                        found = true;
                        break;
                    }

                }
                if (!found) continue;
            }
            if(hasSpecialProject(ProjectReward.ProjectRewardType.SHIP,allShipHullSpec.getHullId()))continue;
            shipProdSpecs.put(allShipHullSpec.getHullId(), new AoTDProductionSpec(allShipHullSpec.getHullId(), allShipHullSpec));

        }
        for (WeaponSpecAPI weaponSpecAPI : Global.getSettings().getAllWeaponSpecs()) {
            if (blackListedTypes.contains(weaponSpecAPI.getType())) continue;
            if(hasSpecialProject(ProjectReward.ProjectRewardType.WEAPON,weaponSpecAPI.getWeaponId()))continue;
            weaponProdSpecs.put(weaponSpecAPI.getWeaponId(), new AoTDProductionSpec(weaponSpecAPI.getWeaponId(), weaponSpecAPI));
        }
        for (FighterWingSpecAPI allFighterWingSpec : Global.getSettings().getAllFighterWingSpecs()) {
            if(hasSpecialProject(ProjectReward.ProjectRewardType.FIGHTER,allFighterWingSpec.getId()))continue;
            fighterProdSpecs.put(allFighterWingSpec.getId(), new AoTDProductionSpec(allFighterWingSpec.getId(), allFighterWingSpec));
        }
        for (SpecialItemSpecAPI allSpecialItemSpec : Global.getSettings().getAllSpecialItemSpecs()) {
            if (allSpecialItemSpec.hasTag(Tags.MISSION_ITEM)) continue;
            if (allSpecialItemSpec.hasTag("aotd_ignore_gp")) continue;
            if(!ItemEffectsRepo.ITEM_EFFECTS.containsKey(allSpecialItemSpec.getId()))continue;
            if(hasSpecialProject(ProjectReward.ProjectRewardType.ITEM,allSpecialItemSpec.getId()))continue;
            specialItemProdSpecs.put(allSpecialItemSpec.getId(), new AoTDProductionSpec(allSpecialItemSpec.getId(), allSpecialItemSpec));
        }
        for (CommoditySpecAPI s : Global.getSettings().getAllCommoditySpecs()) {
            if(hasSpecialProject(ProjectReward.ProjectRewardType.AICORE,s.getId()))continue;
            if (s.hasTag("ai_core") && !s.hasTag("no_drop") && !s.getId().equals("ai_cores") && s.hasTag("aotd_ai_core")) {
                specialItemProdSpecs.put(s.getId(), new AoTDProductionSpec(s.getId(), s));
            }
        }

        loaded = true;
    }
    public static AoTDProductionManData getManDataIfPresent(String manufacturer){
        for (AoTDProductionManData manDatum : manData) {
            if(manDatum.getManufacturerId().equalsIgnoreCase(manufacturer)){
                return manDatum;
            }
        }
        return null;
    }

    public static boolean hasSpecialProject(ProjectReward.ProjectRewardType type, String rewardId) {
        return !BlackSiteProjectManager.getInstance().getProjectMatchingReward(type, rewardId).isEmpty();
    }
    public static AoTDProductionSpec getShipSpec(String id) {
        return shipProdSpecs.get(id);
    }

    public static AoTDProductionSpec getWeaponSpec(String id) {
        return weaponProdSpecs.get(id);
    }

    public static AoTDProductionSpec getFighterSpec(String id) {
        return fighterProdSpecs.get(id);
    }

    public static AoTDProductionSpec getSpecialItemSpec(String id) {
        return specialItemProdSpecs.get(id);
    }

    public static AoTDProductionSpec getNormalItemSpec(String id) {
        return specialItemProdSpecs.get(id);
    }
    public static List<AoTDProductionSpec> getLearnedSpecsForFaction(AoTDProductionSpec.AoTDProductionSpecType type, FactionAPI faction) {
        return getSpecsBasedOnType(type).values().stream().filter(x->x.isLearnedByFaction(faction)).toList();
    }

}
