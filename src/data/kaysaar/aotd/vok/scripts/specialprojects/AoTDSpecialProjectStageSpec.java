package data.kaysaar.aotd.vok.scripts.specialprojects;

import ashlib.data.plugins.misc.AshMisc;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectStageWindow;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.loadEntries;

public class AoTDSpecialProjectStageSpec {
    public String id;
    public String name;
    float days;
    public HashMap<String, Integer> gpCost = new HashMap<>();
    public float creditCosts;
    public ArrayList<OtherCostData> otherCosts = new ArrayList<>();
    Vector2f uiCordsOfBox;
    ArrayList<Vector2f>uiCordsOnHologram = new ArrayList<>();
    SpecialProjectStageWindow.RenderingMode mode;
    SpecialProjectStageWindow.OriginMode originMode;

    public AoTDSpecialProjectStageSpec(String id) {
        this.id = id;

    }
    public ArrayList<Vector2f> getUiCordsOnHologram() {
        return uiCordsOnHologram;
    }

    public Vector2f getUiCordsOfBox() {
        return uiCordsOfBox;
    }
    public void setUiCordsOfBox(Vector2f uiCordsOfBox) {
        this.uiCordsOfBox = uiCordsOfBox;
    }

    public void setUiCordsOnHologram(ArrayList<Vector2f> uiCordsOnHologram) {
        this.uiCordsOnHologram = uiCordsOnHologram;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDays() {
        return days;
    }

    public void setDays(float days) {
        this.days = days;
    }

    public HashMap<String, Integer> getGpCost() {
        return gpCost;
    }

    public void setGpCost(HashMap<String, Integer> gpCost) {
        this.gpCost = gpCost;
    }

    public String getId() {
        return id;
    }

    public float getCreditCosts() {
        return creditCosts;
    }

    public void setCreditCosts(float creditCosts) {
        this.creditCosts = creditCosts;
    }

    public void addOtherCost(String id, String type, int amount) {
        boolean found = false;
        for (OtherCostData otherCost : otherCosts) {
            if (otherCost.getId().equals(id)) {
                otherCost.addAmount(amount);
                found = true;
                break;
            }
        }
        if (!found) {
            otherCosts.add(new OtherCostData(id, type, amount));
        }
    }

    public ArrayList<OtherCostData> getOtherCosts() {
        return otherCosts;
    }

    public void setMode(SpecialProjectStageWindow.RenderingMode mode) {
        this.mode = mode;
    }

    public void setOriginMode(SpecialProjectStageWindow.OriginMode originMode) {
        this.originMode = originMode;
    }

    public SpecialProjectStageWindow.OriginMode getOriginMode() {
        return originMode;
    }

    public SpecialProjectStageWindow.RenderingMode getMode() {
        return mode;
    }

    public static AoTDSpecialProjectStageSpec initSpecFromJson(JSONObject obj) throws JSONException {
        String id = obj.getString("id");
        if (id == null || id.isEmpty()) return null;
        String name = obj.getString("name");
        AoTDSpecialProjectStageSpec spec = new AoTDSpecialProjectStageSpec(id);
        spec.setName(name);
        int days = obj.getInt("stageDays");
        spec.setGpCost(AoTDMisc.loadCostMap(obj.getString("stageCostGP")));
        int credits = obj.getInt("stageCostCredits");
        spec.setCreditCosts(credits);
        spec.setDays(days);
        spec.addOtherCostEntry(obj.getString("stageCostOther"),spec);
        spec.setUiCordsOfBox(getCoordinates(obj.getString("uiCordOfStageComponent")));
        spec.setUiCordsOnHologram(getUiCordsOnHologram(obj.getString("uiCordsOfPointsOnHologram")));
        spec.setOriginMode(SpecialProjectStageWindow.OriginMode.valueOf(obj.getString("lineOriginMode")));
        spec.setMode(SpecialProjectStageWindow.RenderingMode.valueOf(obj.getString("lineRenderMode")));
        return spec;

    }
    public  void  addOtherCostEntry(String rawMap, AoTDSpecialProjectStageSpec spec) {
        for (String s : loadEntries(rawMap, ",")) {
            String[] extracted = s.split(":");
            spec.addOtherCost(extracted[0],extracted[1],Integer.valueOf(extracted[2]));
        }
    }

    public static ArrayList<Vector2f> getUiCordsOnHologram(String rawMap) {
        ArrayList<Vector2f>vectors = new ArrayList<>();
        for (String entry : AshMisc.loadEntries(rawMap,";")) {
            vectors.add(getCoordinates(entry));
        }
        return vectors;
    }
    public static Vector2f getCoordinates(String rawMap) {
        if(!AshMisc.isStringValid(rawMap)){
            return new Vector2f(0,0);
        }
        String[] extracted = rawMap.split(",");
        return new Vector2f(Integer.valueOf(extracted[0]),Integer.valueOf(extracted[1]));

    }
}
