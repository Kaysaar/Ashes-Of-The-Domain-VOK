package data.kaysaar.aotd.vok.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResearchProjectSpec {
    public String id;
    public String nameOfProject;
    public ArrayList<SpecialProjectStage>stages;

    public String getId() {
        return id;
    }

    public String getNameOfProject() {
        return nameOfProject;
    }

    public ArrayList<SpecialProjectStage> getStages() {
        return stages;
    }

    public boolean isRepeatable() {
        return isRepeatable;
    }

    public boolean isDiscoverable() {
        return isDiscoverable;
    }

    public String getPlugin() {
        return plugin;
    }

    public String getModId() {
        return modId;
    }

    public boolean isRepeatable;
    public  boolean isDiscoverable;

    public String plugin;
    public String modId;
    public String projectDescription;

    public ResearchProjectSpec(String ProjectId, String Name, boolean IsDiscoverable, boolean IsRepeatable, ArrayList<SpecialProjectStage> stages, String Plugin ,
                               String ModId, String ProjectDescription){
        this.id = ProjectId;
        this.nameOfProject = Name;
        this.isDiscoverable = IsDiscoverable;
        this.isRepeatable = IsRepeatable;
        this.stages = stages;
        this.plugin = Plugin;
        this.modId = ModId;
        this.projectDescription = ProjectDescription;
    }

    public static ResearchProjectSpec initSpecFromJson(JSONObject object)throws  JSONException{
        String projectId = object.getString("id");
        String name= object.getString("name");
        boolean isDiscoverable = object.getBoolean("isDiscoverable");
        boolean isRepeatable = object.getBoolean("isRepeatable");
        String rawMap = object.getString("stageMap");
        String rawDurationMap = object.getString("stageDuration");
        ArrayList<SpecialProjectStage>projectStages = createStages(rawMap,rawDurationMap);
        String plugin = object.getString("plugin");
        String modId = object.getString("modId");
        String description = object.getString("projectDescription");
        return new ResearchProjectSpec(projectId,name,isDiscoverable,isRepeatable,projectStages,plugin,modId,description);


    }
    public static  ArrayList<SpecialProjectStage>createStages(String rawMap, String durationMap){
        ArrayList<SpecialProjectStage>stages = new ArrayList<>();
        String[]splitedMap = rawMap.split("\n");
        int biggestCount = 0;
        for (String s : splitedMap) {
            String[] splited  = s.split(":");
            int index = Integer.parseInt(splited[0]);
            if(index>biggestCount){
                biggestCount = index;
            }
        }
        for(int i=0;i<=biggestCount;i++){
            SpecialProjectStage stage = new SpecialProjectStage();
            stage.numberOfStage  = i;
            int duration = Integer.parseInt(durationMap.split("\n")[i].split(":")[1]);
            for (String s : splitedMap) {
                String[] splited  = s.split(":");
                if(Integer.parseInt(splited[0].trim())==i){
                    stage.optionsForStage.put(splited[1].trim(),Integer.parseInt(splited[3].trim()));
                    stage.optionsNameMap.put(splited[1].trim(),splited[2].trim());
                    stage.durationOfStage = duration;
                }
            }
            stages.add(stage);
        }
        return stages;

    }


}
