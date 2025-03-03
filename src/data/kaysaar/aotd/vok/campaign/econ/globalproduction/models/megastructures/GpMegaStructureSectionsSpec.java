package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GpMegaStructureSectionsSpec {

    public String sectionID;
    public HashMap<String,Integer> gpUpkeepOfSection;
    public HashMap<String,Integer>gpRestorationCost;
    public HashMap<String,Integer>gpAfterRestorationCost;
    public float daysForRenovation;
    public float runningCost;
    public float runningCostBeforeRepair;
    public float renovationCost;
    public String icon;
    public String description;
    public String name;
    public String script;

    public float getRunningCostBeforeRepair() {
        return runningCostBeforeRepair;
    }

    public void setRunningCostBeforeRepair(float runningCostBeforeRepair) {
        this.runningCostBeforeRepair = runningCostBeforeRepair;
    }

    public float getRenovationCost() {
        return renovationCost;
    }

    public void setRenovationCost(float renovationCost) {
        this.renovationCost = renovationCost;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public HashMap<String, Integer> getGpAfterRestorationCost() {
        return gpAfterRestorationCost;
    }

    public void setGpAfterRestorationCost(HashMap<String, Integer> gpAfterRestorationCost) {
        this.gpAfterRestorationCost = gpAfterRestorationCost;
    }

    public GPMegaStructureSection getScript() {
        try {
            GPMegaStructureSection section = (GPMegaStructureSection) Global.getSettings().getScriptClassLoader().loadClass(script).newInstance();
            section.specID = sectionID;

            return section;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public HashMap<String, Integer> getGpUpkeepOfSection() {
        return gpUpkeepOfSection;
    }

    public void setGpUpkeepOfSection(HashMap<String, Integer> gpUpkeepOfSection) {
        this.gpUpkeepOfSection = gpUpkeepOfSection;
    }

    public HashMap<String, Integer> getGpRestorationCost() {
        return gpRestorationCost;
    }

    public void setGpRestorationCost(HashMap<String, Integer> gpRestorationCost) {
        this.gpRestorationCost = gpRestorationCost;
    }

    public float getDaysForRenovation() {
        return daysForRenovation;
    }

    public void setDaysForRenovation(float daysForRenovation) {
        this.daysForRenovation = daysForRenovation;
    }

    public float getRunningCost() {
        return runningCost;
    }

    public void setRunningCost(float runningCost) {
        this.runningCost = runningCost;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public static ArrayList<GpMegaStructureSectionsSpec> getSpecFromFiles(){
        ArrayList<GpMegaStructureSectionsSpec> specs = new ArrayList<GpMegaStructureSectionsSpec>();
        try {
            for (JSONObject jsonObject : AoTDMisc.getObjectListFromArray( Global.getSettings().getMergedSpreadsheetDataForMod("id","data/campaign/aotd_megastructure_sections.csv","aotd_vok"))) {
                String id = jsonObject.getString("id");
                if(!AoTDMisc.isStringValid(id))continue;
                String name = jsonObject.getString("name");
                String script = jsonObject.getString("script");
                int baseUpkeepCredits = jsonObject.getInt("baseUpkeepAfterRestoration");
                int baseUpkeepCreditsBefore = jsonObject.getInt("baseUpkeepBeforeRestoration");
                int renovationCost= jsonObject.getInt("renovationCost");
                HashMap<String, Integer> baseGPCost = new HashMap<>();
                baseGPCost = AoTDMisc.loadCostMap(jsonObject.getString("baseGPUpkeep"));
                HashMap<String, Integer> gpResorationCost = new HashMap<>();
                gpResorationCost = AoTDMisc.loadCostMap(jsonObject.getString("gPRestorationCost"));
                HashMap<String, Integer> gpAfterRestoration = new HashMap<>();
                gpAfterRestoration = AoTDMisc.loadCostMap(jsonObject.getString("gPRestoredCost"));
                String iconId = jsonObject.getString("icon");
                String description = jsonObject.getString("description");
                int daysTillRestored = jsonObject.getInt("daysTillRestored");
                GpMegaStructureSectionsSpec spec = new GpMegaStructureSectionsSpec();
                spec.setSectionID(id);
                spec.setName(name);
                spec.setScript(script);
                spec.setRunningCost(baseUpkeepCredits);
                spec.setRunningCostBeforeRepair(baseUpkeepCreditsBefore);
                spec.setRenovationCost(renovationCost);
                spec.setGpRestorationCost(gpResorationCost);
                spec.setGpUpkeepOfSection(baseGPCost);
                spec.setIcon(iconId);
                spec.setDescription(description);
                spec.setDaysForRenovation(daysTillRestored);
                spec.setGpAfterRestorationCost(gpAfterRestoration);
                specs.add(spec);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return specs;
    }
}
