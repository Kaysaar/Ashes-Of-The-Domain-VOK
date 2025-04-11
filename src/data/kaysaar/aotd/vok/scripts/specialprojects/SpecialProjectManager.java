package data.kaysaar.aotd.vok.scripts.specialprojects;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.BaseImageHologram;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.ShipHologram;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.WeaponHologram;

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
            else{
                projects.get(entry.getKey()).update();
            }
        }
    }
    public void advance(float amount){

    }
    public static HologramViewer createHologramViewer(AoTDSpecialProjectSpec spec,boolean isForButton){
        float iconSize = 100;
        if(!isForButton){
            iconSize = spec.getIconData().getSize();
        }
        SpecialProjectIconData data = spec.getIconData();
        HologramViewer viewer = null;
        if(data.getType().equals(SpecialProjectIconData.IconType.COMMODITY)){
            viewer = new HologramViewer(iconSize,iconSize,new BaseImageHologram(Global.getSettings().getSprite(Global.getSettings().getCommoditySpec(data.getIconId()).getIconName())));

        }
        if(data.getType().equals(SpecialProjectIconData.IconType.SHIP)){
            viewer = new HologramViewer(iconSize,iconSize,new ShipHologram(data.getIconId()));
        }
        if(data.getType().equals(SpecialProjectIconData.IconType.ITEM)){
            viewer = new HologramViewer(iconSize,iconSize,new BaseImageHologram(Global.getSettings().getSprite(Global.getSettings().getSpecialItemSpec(data.getIconId()).getIconName())));

        }
        if(data.getType().equals(SpecialProjectIconData.IconType.WEAPON)){
            viewer = new HologramViewer(iconSize,iconSize,new WeaponHologram(data.getIconId()));

        }
        return viewer;
    }
}
