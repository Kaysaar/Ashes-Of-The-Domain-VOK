package data.kaysaar.aotd.vok.scripts.specialprojects;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProjectSpec;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProjectStageSpec;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecializationSpec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SpecialProjectSpecManager {
    public static HashMap<String,AoTDSpecialProjectSpec>specs = new HashMap<>();
    public static HashMap<String,AoTDSpecialProjectStageSpec>stagSpecs = new HashMap<>();
    public static String specFileNameProj ="data/campaign/aotd_projects.csv";
    public static String specFileNameStages ="data/campaign/aotd_project_stages.csv";
    public static String specFileNameTypes="data/campaign/aotd_research_types.csv";
    public static AoTDSpecialProjectStageSpec getStageSpec(String specId) {
        return stagSpecs.get(specId);
    }
    public static AoTDSpecialProjectSpec getSpec(String specName) {
        if(specs.isEmpty()){
            reLoad();
        }
        return specs.get(specName);
    }
    public static ArrayList<AoTDSpecializationSpec>specializations = new ArrayList<>();
    public static void reLoad(){
        specs.clear();
        stagSpecs.clear();
        specializations.clear();
        loadCsvEntries();
    }

    public static HashMap<String, AoTDSpecialProjectSpec> getSpecs() {
        return specs;
    }

    public static void loadCsvEntries(){
        HashMap<String, AoTDSpecialProjectStageSpec> stageSpecs = new HashMap<>();
        HashMap<String, AoTDSpecialProjectSpec> projSpecs = new HashMap<>();

        try {
            JSONArray resArray = Global.getSettings().getMergedSpreadsheetDataForMod("id", specFileNameStages, "aotd_vok");
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject obj = resArray.getJSONObject(i);
                AoTDSpecialProjectStageSpec spec = AoTDSpecialProjectStageSpec.initSpecFromJson(obj);
                if (spec != null) {
                    stageSpecs.put(spec.getId(), spec);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        stagSpecs.putAll(stageSpecs);
        try {
            JSONArray resArray = Global.getSettings().getMergedSpreadsheetDataForMod("id", specFileNameProj, "aotd_vok");
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject obj = resArray.getJSONObject(i);
                AoTDSpecialProjectSpec spec = AoTDSpecialProjectSpec.initSpecFromJson(obj);
                if (spec != null) {
                    projSpecs.put(spec.getId(), spec);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        specs.putAll(projSpecs);
        try {
            JSONArray resArray = Global.getSettings().getMergedSpreadsheetDataForMod("id", specFileNameTypes, "aotd_vok");
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject obj = resArray.getJSONObject(i);
                AoTDSpecializationSpec spec = AoTDSpecializationSpec.getSpecFromJson(obj);
                if (spec != null) {
                    specializations.add(spec);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public AoTDSpecializationSpec getSpecializationSpec(String specId) {
        return specializations.stream().filter(s -> s.getId().equals(specId)).findFirst().orElse(null);
    }
    public static ArrayList<AoTDSpecializationSpec> getSpecializations() {
        return specializations;
    }
}
