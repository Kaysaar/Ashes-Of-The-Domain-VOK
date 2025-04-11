package data.kaysaar.aotd.vok.scripts.specialprojects;

import com.fs.starfarer.api.Global;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpecialProjectManager {
    public LinkedHashMap<String,AoTDSpecialProject> projects = new LinkedHashMap<>();
    public AoTDSpecialProject currentlyOnGoingProject;
    public static String memflag = "$aotd_special_proj_manager";
    public static SpecialProjectManager getInstance(){
        if(Global.getSector().getMemory().get(memflag)==null){
            setInstance();
        }
        return (SpecialProjectManager) Global.getSector().getMemory().get(memflag);
    }
    public static void setInstance(){

        Global.getSector().getMemory().set(memflag,new SpecialProjectManager());
    }
    public SpecialProjectManager(){
        projects = new LinkedHashMap<>();
        loadAdditionalData();
    }
    public LinkedHashMap<String, AoTDSpecialProject> getProjects() {
        return projects;
    }
    public AoTDSpecialProject getProject(String id ){
        return projects.get(id);
    }

    public void loadAdditionalData(){
        for (Map.Entry<String, AoTDSpecialProjectSpec> entry : SpecialProjectSpecManager.getSpecs().entrySet()) {
            if(!projects.containsKey(entry.getKey())){
                AoTDSpecialProject project = entry.getValue().getPlugin();
                project.init();
                projects.put(entry.getKey(),project);
            }
        }
    }
    public void advance(float amount){

    }
}
