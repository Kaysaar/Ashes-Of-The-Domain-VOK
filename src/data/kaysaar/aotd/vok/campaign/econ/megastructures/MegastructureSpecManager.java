package data.kaysaar.aotd.vok.campaign.econ.megastructures;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectStageWindow;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import static data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProjectStageSpec.getCoordinates;
import static data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProjectStageSpec.getUiCordsOnHologram;

public class MegastructureSpecManager {
    public static LinkedHashMap<String, MegastructureSpec> megastructuresSpec = new LinkedHashMap<>();
    public static LinkedHashMap<String, MegastructureSectionSpec> megastructuresSectionSpec = new LinkedHashMap<>();





    public static void init() {
        try {
            for (JSONObject jsonObject : AoTDMisc.getObjectListFromArray(Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/campaign/aotd_megastructure_sections.csv", "aotd_vok"))) {
                String id = jsonObject.getString("id");
                if(!AoTDMisc.isStringValid(id))continue;
                String name = jsonObject.getString("name");
                String script = jsonObject.getString("script");
                int baseUpkeepCredits = jsonObject.getInt("baseUpkeepAfterRestoration");
                int renovationCost= jsonObject.getInt("renovationCost");
                HashMap<String, Integer> gpResorationCost = new HashMap<>();
                gpResorationCost = AoTDMisc.loadCostMap(jsonObject.getString("gPRestorationCost"));
                HashMap<String, Integer> gpAfterRestoration = new HashMap<>();
                gpAfterRestoration = AoTDMisc.loadCostMap(jsonObject.getString("gPRestoredCost"));
                String iconId = jsonObject.getString("icon");
                String description = jsonObject.getString("description");
                int daysTillRestored = jsonObject.getInt("daysTillRestored");

                MegastructureSectionSpec spec = new MegastructureSectionSpec();
                spec.setId(id);
                spec.setName(name);
                spec.setMonthlyRenovationCost(renovationCost);
                spec.setDaysNeededForRestoration(daysTillRestored);
                spec.setIconId(iconId);
                spec.setDescription(description);
                spec.setBaseUpkeepAfterRestoration(baseUpkeepCredits);
                spec.setResourceRestorationCost(AoTDMisc.getOrderedResourceMap(gpResorationCost));
                spec.setResourceUpkeepAfterRestoration(AoTDMisc.getOrderedResourceMap(gpAfterRestoration));
                spec.setPluginPath(script);
                spec.setUiCordsOfBox(getCoordinates(jsonObject.getString("uiCordOfStageComponent")));
                spec.setUiCordsOnHologram(getUiCordsOnHologram(jsonObject.getString("uiCordsOfPointsOnHologram")));
                spec.setOriginMode(SpecialProjectStageWindow.OriginMode.valueOf(jsonObject.getString("lineOriginMode")));
                spec.setMode(SpecialProjectStageWindow.RenderingMode.valueOf(jsonObject.getString("lineRenderMode")));
                megastructuresSectionSpec.put(id, spec);
            }


            for (JSONObject jsonObject : AoTDMisc.getObjectListFromArray(Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/campaign/aotd_megastructure.csv", "aotd_vok"))) {
                String id = jsonObject.getString("id");
                if(!AoTDMisc.isStringValid(id))continue;
                String name = jsonObject.getString("name");
                String script = jsonObject.getString("script");
                int baseUpkeepCredits = jsonObject.getInt("baseUpkeepCredits");
                HashMap<String, Integer> baseGPCost = new LinkedHashMap<>();
                baseGPCost = AoTDMisc.loadCostMap(jsonObject.getString("baseGPCost"));
                String iconId = jsonObject.getString("icon");
                String sectorEntityTokenId = jsonObject.getString("entityId");
                LinkedHashSet<String> sectionIds = new LinkedHashSet<>(AoTDMisc.loadEntries(jsonObject.getString("sections"),","));
                ArrayList<String>tags = AoTDMisc.loadEntries(jsonObject.getString("tags"),",");
                HashSet<String> te = new HashSet<>(tags);
                String description = jsonObject.getString("description");
                String image = jsonObject.getString("image");
                for (String sectionId : sectionIds) {
                    if(getSpecForSection(sectionId)==null){
                        throw new RuntimeException("Section spec of id "+sectionId+" not found, check aotd_megastructure_sections.csv ");
                    }
                }
                MegastructureSpec spec = new MegastructureSpec();
                spec.setMegastructureID(id);
                spec.setName(name);
                spec.setTags(te);
                spec.setScript(script);
                spec.setBaseUpkeepCredits(baseUpkeepCredits);;
                spec.setIconId(iconId);
                spec.setSectorEntityTokenId(sectorEntityTokenId);
                spec.setSectionIds(sectionIds);
                spec.setDescription(description);
                spec.setImageForMegastructure(image);
                megastructuresSpec.put(id, spec);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static MegastructureSpec getSpecForMegastructure(String id) {
        return megastructuresSpec.get(id);
    }

    public static MegastructureSectionSpec getSpecForSection(String id) {
        return megastructuresSectionSpec.get(id);
    }


}
