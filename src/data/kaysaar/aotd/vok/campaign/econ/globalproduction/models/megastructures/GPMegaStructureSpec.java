package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GPMegaStructureSpec {
    public String megastructureID;
    public String name;
    public String script;
    public int baseUpkeepCredits;
    public HashMap<String,Integer> baseGPCost;
    public String iconId;
    public String sectorEntityTokenId;
    public ArrayList<String>sectionIds;
    public String description;

    public String getMegastructureID() {
        return megastructureID;
    }

    public void setMegastructureID(String megastructureID) {
        this.megastructureID = megastructureID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GPBaseMegastructure getScript() {
        try {
            GPBaseMegastructure megastructure = (GPBaseMegastructure) Global.getSettings().getScriptClassLoader().loadClass(script).getConstructor(String.class).newInstance(this.megastructureID);
            return megastructure;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setScript(String script) {
        this.script = script;
    }

    public int getBaseUpkeepCredits() {
        return baseUpkeepCredits;
    }

    public void setBaseUpkeepCredits(int baseUpkeepCredits) {
        this.baseUpkeepCredits = baseUpkeepCredits;
    }

    public HashMap<String, Integer> getBaseGPCost() {
        return baseGPCost;
    }

    public void setBaseGPCost(HashMap<String, Integer> baseGPCost) {
        this.baseGPCost = baseGPCost;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getSectorEntityTokenId() {
        return sectorEntityTokenId;
    }

    public void setSectorEntityTokenId(String sectorEntityTokenId) {
        this.sectorEntityTokenId = sectorEntityTokenId;
    }

    public ArrayList<String> getSectionIds() {
        return sectionIds;
    }

    public void setSectionIds(ArrayList<String> sectionIds) {
        this.sectionIds = sectionIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public static ArrayList<GPMegaStructureSpec>getSpecFromFiles(){
        ArrayList<GPMegaStructureSpec> specs = new ArrayList<GPMegaStructureSpec>();
        try {
            for (JSONObject jsonObject : AoTDMisc.getObjectListFromArray( Global.getSettings().getMergedSpreadsheetDataForMod("id","data/campaign/aotd_megastructure.csv","aotd_vok"))) {
                String id = jsonObject.getString("id");
                if(!AoTDMisc.isStringValid(id))continue;
                String name = jsonObject.getString("name");
                String script = jsonObject.getString("script");
                int baseUpkeepCredits = jsonObject.getInt("baseUpkeepCredits");
                HashMap<String, Integer> baseGPCost = new LinkedHashMap<>();
                baseGPCost = AoTDMisc.loadCostMap(jsonObject.getString("baseGPCost"));
                String iconId = jsonObject.getString("icon");
                String sectorEntityTokenId = jsonObject.getString("entityId");
                ArrayList<String>sectionIds = AoTDMisc.loadEntries(jsonObject.getString("sections"),",");
                String description = jsonObject.getString("description");
                for (String sectionId : sectionIds) {
                    if(GPManager.getInstance().getMegaSectionSpecFromList(sectionId)==null){
                        throw new RuntimeException("Section spec of id "+sectionId+" not found, check  aotd_megastructure_sections.csv ");
                    }
                }
                GPMegaStructureSpec spec = new GPMegaStructureSpec();
                spec.setMegastructureID(id);
                spec.setName(name);
                spec.setScript(script);
                spec.setBaseUpkeepCredits(baseUpkeepCredits);
                spec.setBaseGPCost(baseGPCost);
                spec.setIconId(iconId);
                spec.setSectorEntityTokenId(sectorEntityTokenId);
                spec.setSectionIds(sectionIds);
                spec.setDescription(description);
                specs.add(spec);

            }

        } catch (Exception  e) {
            throw new RuntimeException(e);
        }
        return specs;
    }

}
