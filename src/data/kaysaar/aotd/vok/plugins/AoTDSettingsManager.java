package data.kaysaar.aotd.vok.plugins;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

public class AoTDSettingsManager {
    public static final String AOTD_EXPEDITION_THRESHOLD = "aotd_expedition_threshold";
    public static final String AOTD_EXPEDITION_BEGIN = "aotd_expedition_begin";
    public static final String AOTD_DEFENCE_MIN_VAL = "aotd_defence_min_val";
    public static final String AOTD_DEFENCE_MAX_VAL = "aotd_defence_max_val";
    public static final String AOTD_PCF_AMOUNT = "aotd_pcf_amount";
    public static final String AOTD_RESEARCH_SPEED_MULTIPLIER = "aotd_research_speed_multiplier";
    public static final String AOTD_TIER_1_UNLOCK = "aotd_tier_1_unlock";
    public static final String AOTD_TIER_2_UNLOCK = "aotd_tier_2_unlock";
    public static final String AOTD_TIER_3_UNLOCK = "aotd_tier_3_unlock";
    public static final String AOTD_TIER_4_UNLOCK = "aotd_tier_4_unlock";
    public static final String AOTD_TIER_5_UNLOCK = "aotd_tier_experimental_unlock";

    public static int getHighestTierEnabled() {
        int highestTierUnlock = 0;
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {

            if (Boolean.TRUE.equals(LunaSettings.getBoolean("aotd_vok", AOTD_TIER_1_UNLOCK))) {
                highestTierUnlock = 1;
            }
            if (Boolean.TRUE.equals(LunaSettings.getBoolean("aotd_vok", AOTD_TIER_2_UNLOCK))) {
                highestTierUnlock = 2;
            }
            if (Boolean.TRUE.equals(LunaSettings.getBoolean("aotd_vok", AOTD_TIER_3_UNLOCK))) {
                highestTierUnlock = 3;
            }
            if (Boolean.TRUE.equals(LunaSettings.getBoolean("aotd_vok", AOTD_TIER_4_UNLOCK))) {
                highestTierUnlock = 4;
            }
            if (Boolean.TRUE.equals(LunaSettings.getBoolean("aotd_vok", AOTD_TIER_5_UNLOCK))) {
                highestTierUnlock = 5;
            }
        } else {
            if (Boolean.TRUE.equals(Global.getSettings().getBoolean( AOTD_TIER_1_UNLOCK))) {
                highestTierUnlock = 1;
            }
            if (Boolean.TRUE.equals(Global.getSettings().getBoolean( AOTD_TIER_2_UNLOCK))) {
                highestTierUnlock = 2;
            }
            if (Boolean.TRUE.equals(Global.getSettings().getBoolean( AOTD_TIER_3_UNLOCK))) {
                highestTierUnlock = 3;
            }
            if (Boolean.TRUE.equals(Global.getSettings().getBoolean( AOTD_TIER_4_UNLOCK))) {
                highestTierUnlock = 4;
            }
            if (Boolean.TRUE.equals(Global.getSettings().getBoolean( AOTD_TIER_5_UNLOCK))) {
                highestTierUnlock = 5;
            }
        }
        return highestTierUnlock;
    }
    public static int getIntValue(String key){
        int val = 0;
        if(Global.getSettings().getModManager().isModEnabled("lunalib")){
            if(LunaSettings.getFloat("aotd_vok",key)!=null){
                val =LunaSettings.getInt("aotd_vok",key);
            }

        }
        else{
            val = Global.getSettings().getInt(key);
        }
        return val;
    }
    public static boolean getBooleanValue(String key){
        boolean val = false;
        if(Global.getSettings().getModManager().isModEnabled("lunalib")){
            if(LunaSettings.getBoolean("aotd_vok",key)!=null){
                val =LunaSettings.getBoolean("aotd_vok",key);
            }

        }
        else{
            val = Global.getSettings().getBoolean(key);
        }
        return val;
    }
    public static float getFloatValue(String key){
        float val = 1;
        if(Global.getSettings().getModManager().isModEnabled("lunalib")){
            if(LunaSettings.getDouble("aotd_vok",key)!=null){
                val = LunaSettings.getDouble("aotd_vok",key).floatValue();
            }

        }
        else{
            val = Global.getSettings().getFloat(key);
        }
        return val;
    }

}
