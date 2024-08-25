package data.kaysaar.aotd.vok.misc.fighterinfo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FighterInfoRepo {

    public static ArrayList<FighterInfo> fighterRepo = new ArrayList<>();

    public static FighterInfo getFromRepo(String id) {
        for (FighterInfo fighterInfo : fighterRepo) {
            if (fighterInfo.getFighterWingID().equals(id)) {
                return fighterInfo;
            }
        }
        return null;
    }

    public static void initializeRepo() {
        for (FighterWingSpecAPI wingSpec : Global.getSettings().getAllFighterWingSpecs()) {
            try {
                // Get the variant's file path
                String filepath = Global.getSettings().getVariant(wingSpec.getVariantId()).getVariantFilePath();
                filepath = AoTDMisc.cleanPath(filepath);
                filepath = filepath.replace("\\", "/");

                // Load the JSON object for the variant
                JSONObject variantJson = Global.getSettings().loadJSON(filepath);

                // Initialize a map to store weapon counts
                HashMap<String, Integer> weaponMap = new HashMap<String, Integer>();

                // Extract weapon information from the JSON
                if (variantJson.has("weaponGroups")) {
                    JSONArray weaponGroups = variantJson.getJSONArray("weaponGroups");
                    for (int i = 0; i < weaponGroups.length(); i++) {
                        JSONObject groupJson = weaponGroups.getJSONObject(i);

                        JSONObject weapons = groupJson.getJSONObject("weapons");
                        JSONArray slots = weapons.names(); // Get all keys (slot names)
                        if (slots != null) {
                            for (int j = 0; j < slots.length(); j++) {
                                String slot = slots.getString(j);
                                String weaponId = weapons.getString(slot);

                                // Manually handle getOrDefault
                                if (weaponMap.containsKey(weaponId)) {
                                    weaponMap.put(weaponId, weaponMap.get(weaponId) + 1);
                                } else {
                                    weaponMap.put(weaponId, 1);
                                }
                            }
                        }
                    }
                }

                // Create FighterInfo object and add to repository
                FighterInfo info = new FighterInfo(wingSpec.getVariant().getHullSpec().getHullId(), weaponMap);
                fighterRepo.add(info);

            } catch (Exception e) {

            }
        }
    }
}
