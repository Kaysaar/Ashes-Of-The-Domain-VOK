package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.LinkedHashMap;

public class GPUIData {
    LinkedHashMap<String, Integer> itemManInffo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> shipSizeInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> weaponSizeInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> weaponManInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> fighterManInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> getShipSizeInfo() {
        return shipSizeInfo;
    }

    public LinkedHashMap<String, Integer> getWeaponSizeInfo() {
        return weaponSizeInfo;
    }

    public LinkedHashMap<String, Integer> getShipManInfo() {
        return shipManInfo;
    }

    public LinkedHashMap<String, Integer> getWeaponManInfo() {
        return weaponManInfo;
    }

    public LinkedHashMap<String, Integer> getFighterManInfo() {
        return fighterManInfo;
    }

    public LinkedHashMap<String, Integer> shipTypeInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> weaponTypeInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> fighterTypeInfo = new LinkedHashMap<>();

    public LinkedHashMap<String, Integer> getShipTypeInfo() {
        return shipTypeInfo;
    }

    public LinkedHashMap<String, Integer> getWeaponTypeInfo() {
        return weaponTypeInfo;
    }
    public LinkedHashMap<String, Integer> getFighterTypeInfo() {
        return fighterTypeInfo;
    }
    public void populateShipInfo() {
        this.shipManInfo.clear();
        LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
        for (GPOption learnedShipPackage : GPManager.getInstance().getLearnedShipPackages()) {
            String man = learnedShipPackage.getSpec().getShipHullSpecAPI().getManufacturer();
            if (man == null) {
                man = "Unknown";
            }
            if (shipManInfo.get(man) == null) {
                shipManInfo.put(man, 1);
            } else {
                int amount = shipManInfo.get(man);
                shipManInfo.put(man, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : shipManInfo.values()) {
            val += value;
        }
        shipManInfo.put("All designs", val);

        this.shipManInfo.putAll(AoTDMisc.sortByValueDescending(shipManInfo));
    }

    public void populateWeaponInfo() {
        if (this.weaponManInfo == null) this.weaponManInfo = new LinkedHashMap<>();
        this.weaponManInfo.clear();
        LinkedHashMap<String, Integer> weaponManInfo = new LinkedHashMap<>();
        for (GPOption learnedShipPackage : GPManager.getInstance().getLearnedWeapons()) {
            String man = learnedShipPackage.getSpec().getWeaponSpec().getManufacturer();
            if (man == null) {
                man = "Unknown";
            }
            if (weaponManInfo.get(man) == null) {
                weaponManInfo.put(man, 1);
            } else {
                int amount = weaponManInfo.get(man);
                weaponManInfo.put(man, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : weaponManInfo.values()) {
            val += value;
        }
        weaponManInfo.put("All designs", val);

        this.weaponManInfo.putAll(AoTDMisc.sortByValueDescending(weaponManInfo));
    }

    public void populateFighterInfo() {
        if (this.fighterManInfo == null) this.fighterManInfo = new LinkedHashMap<>();
        this.fighterManInfo.clear();
        LinkedHashMap<String, Integer> fighterManInfo = new LinkedHashMap<>();
        for (GPOption learnedShipPackage : GPManager.getInstance().getLearnedFighters()) {
            String man = learnedShipPackage.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer();
            if (man == null) {
                man = "Unknown";
            }
            if (fighterManInfo.get(man) == null) {
                fighterManInfo.put(man, 1);
            } else {
                int amount = fighterManInfo.get(man);
                fighterManInfo.put(man, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : fighterManInfo.values()) {
            val += value;
        }
        fighterManInfo.put("All designs", val);

        this.fighterManInfo.putAll(AoTDMisc.sortByValueDescending(fighterManInfo));
    }

    public void populateShipSizeInfo() {
        if (this.shipSizeInfo == null) this.shipSizeInfo = new LinkedHashMap<>();
        this.shipSizeInfo.clear();
        LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
        for (GPOption learnedShipPackage : GPManager.getInstance().getLearnedShipPackages()) {
            String indicator = Misc.getHullSizeStr(learnedShipPackage.getSpec().getShipHullSpecAPI().getHullSize());
            if (shipManInfo.get(indicator) == null) {
                shipManInfo.put(indicator, 1);
            } else {
                int amount = shipManInfo.get(indicator);
                shipManInfo.put(indicator, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : shipManInfo.values()) {
            val += value;
        }
        shipManInfo.put("All sizes", val);
        this.shipSizeInfo.putAll(AoTDMisc.sortByValueDescending(shipManInfo));
    }

    public void populateWeaponSizeInfo() {
        if (this.weaponSizeInfo == null) this.weaponSizeInfo = new LinkedHashMap<>();
        this.weaponSizeInfo.clear();
        LinkedHashMap<String, Integer> weaponInfo = new LinkedHashMap<>();
        for (GPOption learnedWeapon : GPManager.getInstance().getLearnedWeapons()) {
            String indicator = learnedWeapon.getSpec().getWeaponSpec().getSize().getDisplayName();
            if (weaponInfo.get(indicator) == null) {
                weaponInfo.put(indicator, 1);
            } else {
                int amount = weaponInfo.get(indicator);
                weaponInfo.put(indicator, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : weaponInfo.values()) {
            val += value;
        }
        weaponInfo.put("All sizes", val);
        this.weaponSizeInfo.putAll(AoTDMisc.sortByValueDescending(weaponInfo));
    }
    public void populateShipTypeInfo() {
        if (this.shipTypeInfo == null) this.shipTypeInfo = new LinkedHashMap<>();
        this.shipTypeInfo.clear();
        LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
        for (GPOption learnedShipPackage : GPManager.getInstance().getLearnedShipPackages()) {
            String indicator = AoTDMisc.getType(learnedShipPackage.getSpec().shipHullSpecAPI);
            if (shipManInfo.get(indicator) == null) {
                shipManInfo.put(indicator, 1);
            } else {
                int amount = shipManInfo.get(indicator);
                shipManInfo.put(indicator, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : shipManInfo.values()) {
            val += value;
        }
        shipManInfo.put("All types", val);
        this.shipTypeInfo.putAll(AoTDMisc.sortByValueDescending(shipManInfo));
    }

    public void populateWeaponTypeInfo() {
        if (this.weaponTypeInfo == null) this.weaponTypeInfo = new LinkedHashMap<>();
        this.weaponTypeInfo.clear();
        LinkedHashMap<String, Integer> weaponInfo = new LinkedHashMap<>();
        for (GPOption learnedWeapons : GPManager.getInstance().getLearnedWeapons()) {
            String indicator = learnedWeapons.getSpec().getWeaponSpec().getType().getDisplayName();
            if (weaponInfo.get(indicator) == null) {
                weaponInfo.put(indicator, 1);
            } else {
                int amount = weaponInfo.get(indicator);
                weaponInfo.put(indicator, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : weaponInfo.values()) {
            val += value;
        }
        weaponInfo.put("All types", val);
        this.weaponTypeInfo.putAll(AoTDMisc.sortByValueDescending(weaponInfo));
    }

    public void populateFighterTypeInfo() {
        if (this.fighterTypeInfo == null) this.fighterTypeInfo = new LinkedHashMap<>();
        this.fighterTypeInfo.clear();
        LinkedHashMap<String, Integer> weaponInfo = new LinkedHashMap<>();
        for (GPOption learnedWeapons : GPManager.getInstance().getLearnedFighters()) {
            String indicator = AoTDMisc.getType(learnedWeapons.getSpec().getWingSpecAPI());
            if (weaponInfo.get(indicator) == null) {
                weaponInfo.put(indicator, 1);
            } else {
                int amount = weaponInfo.get(indicator);
                weaponInfo.put(indicator, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : weaponInfo.values()) {
            val += value;
        }
        weaponInfo.put("All types", val);
        this.fighterTypeInfo.putAll(AoTDMisc.sortByValueDescending(weaponInfo));
    }

    public LinkedHashMap<String, Integer> getItemManInffo() {
        return itemManInffo;
    }

    public void populateItemInfo() {
        if (this.itemManInffo == null) itemManInffo = new LinkedHashMap<>();
        this.itemManInffo.clear();
        LinkedHashMap<String, Integer> itemManInffo = new LinkedHashMap<>();
        for (GPOption learnedItem : GPManager.getInstance().getLearnedItems()) {
            SpecialItemSpecAPI spec = learnedItem.getSpec().getItemSpecAPI();
            if(spec==null)continue;
            if (itemManInffo.get(spec.getManufacturer()) == null) {
                itemManInffo.put(spec.getManufacturer(), 1);
            } else {
                int amount = itemManInffo.get(spec.getManufacturer());
                itemManInffo.put(spec.getManufacturer(), amount + 1);
            }
        }
        if( !GPManager.getInstance().getAICores().isEmpty()){
            itemManInffo.put("AI Cores",GPManager.getInstance().getAICores().size());
        }

        int val = 0;
        for (Integer value : itemManInffo.values()) {
            val += value;
        }
        itemManInffo.put("All designs", val);

        this.itemManInffo.putAll(AoTDMisc.sortByValueDescending(itemManInffo));
    }
}
