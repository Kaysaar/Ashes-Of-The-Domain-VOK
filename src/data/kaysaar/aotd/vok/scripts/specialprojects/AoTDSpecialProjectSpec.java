package data.kaysaar.aotd.vok.scripts.specialprojects;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.loadEntries;

public class AoTDSpecialProjectSpec {
    String id;
    String name;
    String description;
    LinkedHashMap<String, Integer> stageMap = new LinkedHashMap<>();
    String pluginName;
    SpecialProjectIconData iconData;
    Set<String> tags = new HashSet<>();

    public Set<String> getTags() {
        return tags;
    }
    public void addTag(String tag) {
        tags.add(tag);
    }
    public void removeTag(String tag) {
        tags.remove(tag);
    }
    public ArrayList<AoTDSpecialProjectStageSpec>getStagesSpecs(){
        ArrayList<AoTDSpecialProjectStageSpec> stages = new ArrayList<>();
        for (String s : stageMap.keySet()) {
            stages.add(SpecialProjectSpecManager.getStageSpec(s));

        }
        return stages;
    }
    public void setPlugin(String pluginPath) {
        this.pluginName = pluginPath;

    }

    public AoTDSpecialProjectSpec(String id) {
        this.id = id;
    }

    public AoTDSpecialProject getPlugin() {
        try {
            AoTDSpecialProject proj =(AoTDSpecialProject) Global.getSettings().getScriptClassLoader().loadClass(pluginName).newInstance();
            proj.setSpecID(this.getId());
            return proj;

        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public LinkedHashMap<String, Integer> getStageMap() {
        return stageMap;
    }

    public void setStageMap(LinkedHashMap<String, Integer> stageMap) {
        this.stageMap = stageMap;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpecialProjectIconData getIconData() {
        if(iconData == null) {
            iconData = new SpecialProjectIconData(null, SpecialProjectIconData.IconType.SHIP, 1f);
        }
        return iconData;
    }
    public void setIconSize(float size) {
        getIconData().setSize(size);
    }
    public void setIconId(String iconId) {
        getIconData().setIconId(iconId);
    }
    public void setIconType(SpecialProjectIconData.IconType type) {
        getIconData().setType(type);
    }

    public void setIconData(SpecialProjectIconData iconData) {
        this.iconData = iconData;
    }




    public static AoTDSpecialProjectSpec initSpecFromJson(JSONObject obj) throws JSONException {
        String id = obj.getString("id");
        if (id == null || id.isEmpty()) return null;
        String name = obj.getString("name");
        String des = obj.getString("description");
        String plugin = obj.getString("plugin");
        if(!AshMisc.isStringValid(plugin)){
            plugin = "data.kaysaar.aotd.vok.scripts.specialprojects.AoTDSpecialProject";
        }
        AoTDSpecialProjectSpec aoTDSpecialProjectSpec = new AoTDSpecialProjectSpec(id);
        aoTDSpecialProjectSpec.setName(name);
        aoTDSpecialProjectSpec.setDescription(des);
        aoTDSpecialProjectSpec.setIconData(getDataFromEntry(obj.getString("iconData")));

        LinkedHashMap<String, Integer> stageMap = AoTDMisc.loadCostMap(obj.getString("stageWeight"));
        for (String s : stageMap.keySet()) {
            if(SpecialProjectSpecManager.getStageSpec(s)==null){
                throw new RuntimeException("Stage spec of "+s+" not found, check "+ SpecialProjectSpecManager.specFileNameStages);
            }
        }

        aoTDSpecialProjectSpec.setStageMap(stageMap);
        aoTDSpecialProjectSpec.setPlugin(plugin);
        aoTDSpecialProjectSpec.getPlugin();
        return aoTDSpecialProjectSpec;

    }

    public static SpecialProjectIconData getDataFromEntry(String rawMap) {
        String[] extracted = rawMap.split(":");
        return  new SpecialProjectIconData(extracted[0],SpecialProjectIconData.IconType.valueOf(extracted[1]),Integer.valueOf(extracted[2]));
    }
}
