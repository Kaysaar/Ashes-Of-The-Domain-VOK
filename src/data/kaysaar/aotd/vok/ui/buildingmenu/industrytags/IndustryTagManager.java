package data.kaysaar.aotd.vok.ui.buildingmenu.industrytags;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ModSpecAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class IndustryTagManager {
    public static LinkedHashMap<String,IndustryTagSpec>tagsLinked = new LinkedHashMap<>();
    private static final Logger logger = Global.getLogger(IndustryTagManager.class);
    public static String specsFilename = "data/campaign/industries.csv";
    public static ArrayList<String> vanillaIndustries = new ArrayList<>();
    static {
        vanillaIndustries.add("population");
        vanillaIndustries.add("farming");
        vanillaIndustries.add("aquaculture");
        vanillaIndustries.add("mining");
        vanillaIndustries.add("techmining");
        vanillaIndustries.add("refining");
        vanillaIndustries.add("spaceport");
        vanillaIndustries.add("megaport");
        vanillaIndustries.add("lightindustry");
        vanillaIndustries.add("heavyindustry");
        vanillaIndustries.add("orbitalworks");
        vanillaIndustries.add("fuelprod");
        vanillaIndustries.add("commerce");
        vanillaIndustries.add("orbitalstation");
        vanillaIndustries.add("battlestation");
        vanillaIndustries.add("starfortress");
        vanillaIndustries.add("orbitalstation_mid");
        vanillaIndustries.add("battlestation_mid");
        vanillaIndustries.add("starfortress_mid");
        vanillaIndustries.add("orbitalstation_high");
        vanillaIndustries.add("battlestation_high");
        vanillaIndustries.add("starfortress_high");
        vanillaIndustries.add("grounddefenses");
        vanillaIndustries.add("heavybatteries");
        vanillaIndustries.add("patrolhq");
        vanillaIndustries.add("militarybase");
        vanillaIndustries.add("highcommand");
        vanillaIndustries.add("lionsguard");
        vanillaIndustries.add("planetaryshield");
        vanillaIndustries.add("waystation");
        vanillaIndustries.add("cryosanctum");
        vanillaIndustries.add("cryorevival");
    }

    public static void addNewTag(IndustryTagSpec tagSpec){
        tagsLinked.put(tagSpec.tag,tagSpec);
    }
    public static void removeTag(String tag){
        tagsLinked.remove(tag);
    }
    public static IndustryTagSpec getTag(String tag){
        return tagsLinked.get(tag);
    }
    public static ArrayList<IndustryTagSpec>getTagsSpecBasedOnType(IndustryTagType type){
        ArrayList<IndustryTagSpec>tags = new ArrayList<>();
        for (Map.Entry<String, IndustryTagSpec> entry : tagsLinked.entrySet()) {
            if(entry.getValue().type.equals(type)){
                tags.add(entry.getValue());
            }
        }
        return tags;
    }
    public static ArrayList<IndustryTagSpec>getTagsSpecBasedOnType(IndustryTagType type,LinkedHashMap<String,IndustryTagSpec> tg){
        ArrayList<IndustryTagSpec>tags = new ArrayList<>();
        for (Map.Entry<String, IndustryTagSpec> entry : tg.entrySet()) {
            if(entry.getValue().type.equals(type)){
                tags.add(entry.getValue());
            }
        }
        return tags;
    }
    public static void loadModdedTags(){
        for (ModSpecAPI mod : Global.getSettings().getModManager().getEnabledModsCopy()) {
            JSONArray modCsv = null;
            try {
                modCsv = Global.getSettings().loadCSV(specsFilename, mod.getId());
            } catch (Exception e) {
                if (!(e instanceof RuntimeException) || !e.getMessage().contains("not found in")) {
                    logger.warn("This mod does not have industries");
                }
            }
            if (modCsv != null) {
                ArrayList<String>specs = new ArrayList<>();
                for(int i = 0; i < modCsv.length(); ++i) {
                    JSONObject item = null;

                    String id;
                    try {
                        item = modCsv.getJSONObject(i);
                        id = item.getString("id");
                    } catch (JSONException var8) {
                        JSONException e = var8;
                        throw new RuntimeException(e);
                    }
                    if(vanillaIndustries.contains(id))continue;
                    if(!AoTDMisc.isStringValid(id))continue;
                    specs.add(id);
                    logger.info("Industry of id " + id + " has been added to list");
                }
                if(specs.isEmpty())continue;
                IndustryTagSpec spec = new IndustryTagSpec(mod.getId(),mod.getName(),specs,IndustryTagType.MOD);
                addNewTag(spec);
            }
        }
    }
    public static void  loadDefaultTags(){
        IndustryTagSpec available = new AvailableIndustryTagSpec("available","Available to build",convertSpecListToIdList(),IndustryTagType.GENERIC);

        IndustryTagSpec specHeavy = new IndustryTagSpec("heavyindustry","Heavy Industry",searchForTag("heavyindustry"),IndustryTagType.GENERIC);
        IndustryTagSpec specLight= new IndustryTagSpec("lightindustry","Light Industry",searchForTag("lightindustry"),IndustryTagType.GENERIC);
        IndustryTagSpec rural = new IndustryTagSpec("rural","Rural",searchForTag("rural"),IndustryTagType.GENERIC);
        IndustryTagSpec structure = new IndustryTagSpec("structure","Structure",searchForTag("structure"),IndustryTagType.GENERIC);
        IndustryTagSpec industry = new IndustryTagSpec("industry","Industry",searchForTag("industry"),IndustryTagType.GENERIC);
        addNewTag(available);
        addNewTag(industry);
        addNewTag(structure);
        addNewTag(rural);
        addNewTag(specHeavy);
        addNewTag(specLight);
    }
    public static ArrayList<String>convertSpecListToIdList(){
        ArrayList<String>ids = new ArrayList<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            ids.add(allIndustrySpec.getId());
        }
        return ids;
    }
    public static String getModNameForInd(String ind){
        if(vanillaIndustries.contains(ind)){
            return "Vanilla";
        }
        for (IndustryTagSpec tagSpec : getTagsSpecBasedOnType(IndustryTagType.MOD)) {
            if(tagSpec.specs.contains(ind)){
                return tagSpec.tagName;
            }
        }
        return "";
    }
    public static ArrayList<String> searchForTag(String tag){
        ArrayList<String>industries = new ArrayList<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if(allIndustrySpec.hasTag(tag)){
                industries.add(allIndustrySpec.getId());
            }
        }
        return industries;
    }
}
