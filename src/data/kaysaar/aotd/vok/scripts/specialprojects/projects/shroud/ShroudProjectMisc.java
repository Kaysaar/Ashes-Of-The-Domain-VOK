package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.kaysaar.aotd.vok.Ids.AoTDItems;

import java.util.HashMap;
import java.util.Map;

public class ShroudProjectMisc implements EveryFrameScript {
    public static String shroudUnderstandingLevel = "$aotd_shroud_understand";
    public static String cooldownBetweenProjects = "$aotd_shroud_cooldown";
    public static String hasAbilityToSummonGreatFleet = "$aotd_shroud_great_fleet";
    public static String hasBetterContainmentMethods = "$aotd_shroud_better_containment_methods";
    public static String memFlagForAmountOfProjectsDoneInLevel = "$aotd_shroud_proj_count_";
    public static Map<Integer, Integer> projectThresholds = new HashMap<>();

    static {
        // Example thresholds
        projectThresholds.put(2, 2); // Level 2 requires 2 projects
        projectThresholds.put(3, 1); // Level 3 requires 1 projects
        // Add more levels as needed
    }
    public IntervalUtil util = new IntervalUtil(2.5f,2.5f);
    public static void setBoolean(String key,boolean val){
        Global.getSector().getPlayerMemoryWithoutUpdate().set(key,val);
    }
    public static int getAmountOfProjectsOnLevel(int level){
        return Global.getSector().getPlayerMemoryWithoutUpdate().getInt(memFlagForAmountOfProjectsDoneInLevel+level);
    }
    public static void increaseAmountOfProjectsOnLevel(int level){
        int curr = getAmountOfProjectsOnLevel(level);
        Global.getSector().getPlayerMemoryWithoutUpdate().set(memFlagForAmountOfProjectsDoneInLevel+level, curr+1);
    }
    public static boolean getBoolean(String memflag){
        return Global.getSector().getPlayerMemoryWithoutUpdate().getBoolean(memflag);
    }
    public static int getLevelOfUnderstanding(){
        if(Global.CODEX_TOOLTIP_MODE){
            return Integer.MAX_VALUE;
        }
       return Global.getSector().getPlayerMemoryWithoutUpdate().getInt(shroudUnderstandingLevel);
    }
    public static void setLevelOfUnderstanding(int level){
        Global.getSector().getPlayerMemoryWithoutUpdate().set(shroudUnderstandingLevel, level);
    }
    public static void setCooldownBetweenProjects(int days){
        Global.getSector().getPlayerMemoryWithoutUpdate().set(cooldownBetweenProjects, true,days);
    }
    public static boolean isCooldownBetweenProject(){
        return Global.getSector().getPlayerMemoryWithoutUpdate().getBoolean(cooldownBetweenProjects);
    }
    public static void updateCommodityInfo(){
        SpecialItemSpecAPI specAPI = Global.getSettings().getSpecialItemSpec(AoTDItems.SHROUDED_SUBSTRATE);
        int levelOfUnderstanding = getLevelOfUnderstanding();
        if(levelOfUnderstanding ==0){
            specAPI.setIconName("graphics/icons/cargo/shrouded_lens.png");
            specAPI.setName("Class-13 Exotic Residue");
            specAPI.setManufacturer("?????");
        }
        if(levelOfUnderstanding ==1){
            if(getBoolean(hasBetterContainmentMethods)){
                specAPI.setIconName("graphics/icons/cargo/shrouded_substrate.png");
                specAPI.setDesc("Refined containment methods have stabilized the exotic matter, allowing for safer handling and improved storage efficiency. Energy surges and spatial distortions are significantly reduced, though caution is still advised when transporting or interacting with the substrate.");
            } else {
                specAPI.setIconName("graphics/icons/cargo/shrouded_lens.png");
                specAPI.setDesc("Anomalous exotic matter extracted from collapsed Dweller entities, suspended in a volatile quasi-stable state using improvised containment fields. Exhibits unpredictable energy surges and localized spatial distortions. Strict adherence to hazardous material handling protocols class 13 is mandatory to prevent structural compromise.");
            }

            specAPI.setName("Shrouded Substrate");
            specAPI.setManufacturer("Shrouded Dweller");
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }


    @Override
    public void advance(float amount) {
        util.advance(amount);
        if (util.intervalElapsed()) {
            int currentLevel = getLevelOfUnderstanding();
            int currentProjects = getAmountOfProjectsOnLevel(currentLevel);
            int requiredProjects = projectThresholds.getOrDefault(currentLevel+1, Integer.MAX_VALUE);
            if (currentProjects >= requiredProjects) {
                // You can increase level, trigger events, etc.
                setLevelOfUnderstanding(currentLevel + 1);
                // Reset projects count if you want
                // Maybe notify player
                updateCommodityInfo();
            }
        }
    }

}
