package data.kaysaar.aotd.vok.misc.fighterinfo;

import java.util.HashMap;

public class FighterInfo {
    HashMap<String, Integer> weaponMap;
    String fighterWingID;

    public FighterInfo(String id, HashMap<String, Integer> weaponMap) {
        this.weaponMap = weaponMap;
        this.fighterWingID = id;
    }

    public HashMap<String, Integer> getWeaponMap() {
        return weaponMap;
    }

    public String getFighterWingID() {
        return fighterWingID;
    }
}