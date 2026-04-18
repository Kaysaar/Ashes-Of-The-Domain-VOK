package data.kaysaar.aotd.vok.campaign.econ.produciton;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpecManager;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

public final class AoTDProductionUIData {

    private AoTDProductionUIData() {} // static-only

    private static final LinkedHashMap<String, Integer> itemManInfo    = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> shipSizeInfo   = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> weaponSizeInfo = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> shipManInfo    = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> weaponManInfo  = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> fighterManInfo = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> shipTypeInfo   = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> weaponTypeInfo = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Integer> fighterTypeInfo= new LinkedHashMap<>();

    // ----- getters -----

    public static LinkedHashMap<String, Integer> getItemManInfo() { return itemManInfo; }

    public static LinkedHashMap<String, Integer> getShipSizeInfo() { return shipSizeInfo; }
    public static LinkedHashMap<String, Integer> getWeaponSizeInfo() { return weaponSizeInfo; }

    public static LinkedHashMap<String, Integer> getShipManInfo() { return shipManInfo; }
    public static LinkedHashMap<String, Integer> getWeaponManInfo() { return weaponManInfo; }
    public static LinkedHashMap<String, Integer> getFighterManInfo() { return fighterManInfo; }

    public static LinkedHashMap<String, Integer> getShipTypeInfo() { return shipTypeInfo; }
    public static LinkedHashMap<String, Integer> getWeaponTypeInfo() { return weaponTypeInfo; }
    public static LinkedHashMap<String, Integer> getFighterTypeInfo() { return fighterTypeInfo; }

    // ----- routing getters -----

    public static LinkedHashMap<String, Integer> getManInfoBasedOnType(AoTDProductionSpec.AoTDProductionSpecType type) {
        if (type == null) return null;
        switch (type) {
            case SHIP: return shipManInfo;
            case WEAPON: return weaponManInfo;
            case FIGHTER: return fighterManInfo;
            case SPECIAL_ITEM:
            case COMMODITY_ITEM: return itemManInfo; // grouped with special items in your logic
            default: return null;
        }
    }

    public static LinkedHashMap<String, Integer> getSizeInfoBasedOnType(AoTDProductionSpec.AoTDProductionSpecType type) {
        if (type == null) return null;
        switch (type) {
            case SHIP: return shipSizeInfo;
            case WEAPON: return weaponSizeInfo;
            default: return null;
        }
    }

    public static LinkedHashMap<String, Integer> getTypeInfoBasedOnType(AoTDProductionSpec.AoTDProductionSpecType type) {
        if (type == null) return null;
        switch (type) {
            case SHIP: return shipTypeInfo;
            case WEAPON: return weaponTypeInfo;
            case FIGHTER: return fighterTypeInfo;
            default: return null;
        }
    }

    // ----- populate methods -----

    public static void populateShipInfo() {
        countAndStore(shipManInfo,
                AoTDProductionSpec.AoTDProductionSpecType.SHIP,
                s -> normalize(s.getManufacturer(), "Unknown"),
                "All designs");
    }

    public static void populateWeaponInfo() {
        countAndStore(weaponManInfo,
                AoTDProductionSpec.AoTDProductionSpecType.WEAPON,
                s -> normalize(s.getManufacturer(), "Unknown"),
                "All designs");
    }

    public static void populateFighterInfo() {
        countAndStore(fighterManInfo,
                AoTDProductionSpec.AoTDProductionSpecType.FIGHTER,
                s -> normalize(s.getManufacturer(), "Unknown"),
                "All designs");
    }

    public static void populateShipSizeInfo() {
        countAndStore(shipSizeInfo,
                AoTDProductionSpec.AoTDProductionSpecType.SHIP,
                AoTDProductionSpec::getSize,
                "All sizes");
    }

    public static void populateWeaponSizeInfo() {
        countAndStore(weaponSizeInfo,
                AoTDProductionSpec.AoTDProductionSpecType.WEAPON,
                AoTDProductionSpec::getSize,
                "All sizes");
    }

    public static void populateShipTypeInfo() {
        countAndStore(shipTypeInfo,
                AoTDProductionSpec.AoTDProductionSpecType.SHIP,
                AoTDProductionSpec::getTypeString,
                "All types");
    }

    public static void populateWeaponTypeInfo() {
        countAndStore(weaponTypeInfo,
                AoTDProductionSpec.AoTDProductionSpecType.WEAPON,
                AoTDProductionSpec::getTypeString,
                "All types");
    }

    public static void populateFighterTypeInfo() {
        countAndStore(fighterTypeInfo,
                AoTDProductionSpec.AoTDProductionSpecType.FIGHTER,
                AoTDProductionSpec::getTypeString,
                "All types");
    }

    public static void populateItemInfo() {
        // Count manufacturers from SPECIAL_ITEM underlying spec
        LinkedHashMap<String, Integer> tmp = countSpecs(
                AoTDProductionSpec.AoTDProductionSpecType.SPECIAL_ITEM,
                spec -> {
                    Object underlying = spec.getUnderlyingSpec();
                    if ((underlying instanceof SpecialItemSpecAPI item)){
                        return normalize(item.getManufacturer(), "Unknown");
                    }
                    if((underlying instanceof CommoditySpecAPI specialItem)){
                        return normalize("AI Cores", "Unknown");
                    }
                    return null;

                }
        );


        finalizeAndStore(itemManInfo, tmp, "All designs");
    }

    /** Convenience: rebuild everything you currently track. */
    public static void populateAll() {
        populateShipInfo();
        populateWeaponInfo();
        populateFighterInfo();

        populateShipSizeInfo();
        populateWeaponSizeInfo();

        populateShipTypeInfo();
        populateWeaponTypeInfo();
        populateFighterTypeInfo();

        populateItemInfo();
    }
    public static void populateShip() {
        populateShipInfo();
        populateShipSizeInfo();
        populateShipTypeInfo();
    }

    public static void populateWeapon() {
        populateWeaponInfo();
        populateWeaponSizeInfo();
        populateWeaponTypeInfo();
    }

    public static void populateFighter() {
        populateFighterInfo();
        populateFighterTypeInfo();
    }

    public static void populateItem() {
        populateItemInfo();
    }
    // ----- generic machinery -----
    public static void populateByType(AoTDProductionSpec.AoTDProductionSpecType type) {
        if (type == null) return;

        switch (type) {
            case SHIP:
                populateShip();
                break;

            case WEAPON:
                populateWeapon();
                break;

            case FIGHTER:
                populateFighter();
                break;

            case SPECIAL_ITEM:
            case COMMODITY_ITEM:
                populateItem();
                break;

            default:
                break;
        }
    }
    private static void countAndStore(
            LinkedHashMap<String, Integer> out,
            AoTDProductionSpec.AoTDProductionSpecType type,
            Function<AoTDProductionSpec, String> keyFn,
            String allLabel
    ) {
        LinkedHashMap<String, Integer> tmp = countSpecs(type, keyFn);
        finalizeAndStore(out, tmp, allLabel);
    }

    private static LinkedHashMap<String, Integer> countSpecs(
            AoTDProductionSpec.AoTDProductionSpecType type,
            Function<AoTDProductionSpec, String> keyFn
    ) {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();

        List<AoTDProductionSpec> learned = AoTDProductionSpecManager.getLearnedSpecsForFaction(
                type, Global.getSector().getPlayerFaction()
        );
        if (learned == null || learned.isEmpty()) return counts;

        for (AoTDProductionSpec spec : learned) {
            if (spec == null) continue;

            String key = keyFn.apply(spec);
            if (key == null || key.isEmpty()) continue;

            counts.merge(key, 1, Integer::sum);
        }

        return counts;
    }

    private static void finalizeAndStore(
            LinkedHashMap<String, Integer> out,
            LinkedHashMap<String, Integer> counts,
            String allLabel
    ) {
        out.clear();

        int total = 0;
        for (Integer v : counts.values()) {
            if (v != null) total += v;
        }
        counts.put(allLabel, total);

        out.putAll(AoTDMisc.sortByValueDescending(counts));
    }

    private static String normalize(String s, String fallback) {
        if (s == null) return fallback;
        s = s.trim();
        return s.isEmpty() ? fallback : s;
    }
}