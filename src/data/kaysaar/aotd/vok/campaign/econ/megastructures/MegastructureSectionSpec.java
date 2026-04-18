package data.kaysaar.aotd.vok.campaign.econ.megastructures;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectStageWindow;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MegastructureSectionSpec {
    String id;
    String name;
    int baseUpkeepAfterRestoration;
    LinkedHashMap<String,Integer>resourceRestorationCost = new LinkedHashMap<>();
    LinkedHashMap<String,Integer>resourceUpkeepAfterRestoration = new LinkedHashMap<>();
    int daysNeededForRestoration;
    int renovationCost;
    String pluginPath;
    String iconId;
    String description;
    int monthlyRenovationCost;
    Vector2f uiCordsOfBox;
    ArrayList<Vector2f> uiCordsOnHologram = new ArrayList<>();
    SpecialProjectStageWindow.RenderingMode mode;
    SpecialProjectStageWindow.OriginMode originMode;

    public SpecialProjectStageWindow.RenderingMode getMode() {
        return mode;
    }

    public SpecialProjectStageWindow.OriginMode getOriginMode() {
        return originMode;
    }

    public void setOriginMode(SpecialProjectStageWindow.OriginMode originMode) {
        this.originMode = originMode;
    }

    public void setMode(SpecialProjectStageWindow.RenderingMode mode) {
        this.mode = mode;
    }

    public Vector2f getUiCordsOfBox() {
        return uiCordsOfBox;
    }

    public ArrayList<Vector2f> getUiCordsOnHologram() {
        return uiCordsOnHologram;
    }

    public void setUiCordsOnHologram(ArrayList<Vector2f> uiCordsOnHologram) {
        this.uiCordsOnHologram = uiCordsOnHologram;
    }

    public void setUiCordsOfBox(Vector2f uiCordsOfBox) {
        this.uiCordsOfBox = uiCordsOfBox;
    }

    public void setMonthlyRenovationCost(int monthlyRenovationCost) {
        this.monthlyRenovationCost = monthlyRenovationCost;
    }

    public int getMonthlyRenovationCost() {
        return monthlyRenovationCost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBaseUpkeepAfterRestoration() {
        return baseUpkeepAfterRestoration;
    }

    public void setBaseUpkeepAfterRestoration(int baseUpkeepAfterRestoration) {
        this.baseUpkeepAfterRestoration = baseUpkeepAfterRestoration;
    }

    public LinkedHashMap<String, Integer> getResourceRestorationCost() {
        return resourceRestorationCost;
    }

    public void setResourceRestorationCost(LinkedHashMap<String, Integer> resourceRestorationCost) {
        this.resourceRestorationCost = resourceRestorationCost;
    }

    public LinkedHashMap<String, Integer> getResourceUpkeepAfterRestoration() {
        return resourceUpkeepAfterRestoration;
    }

    public void setResourceUpkeepAfterRestoration(LinkedHashMap<String, Integer> resourceUpkeepAfterRestoration) {
        this.resourceUpkeepAfterRestoration = resourceUpkeepAfterRestoration;
    }

    public int getDaysNeededForRestoration() {
        return daysNeededForRestoration;
    }

    public void setDaysNeededForRestoration(int daysNeededForRestoration) {
        this.daysNeededForRestoration = daysNeededForRestoration;
    }

    public int getRenovationCost() {
        return renovationCost;
    }

    public void setRenovationCost(int renovationCost) {
        this.renovationCost = renovationCost;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }



    public String getId() {
        return id;
    }

    public BaseMegastructureSection getScript() {
        try {
            BaseMegastructureSection megastructure = (BaseMegastructureSection) Global.getSettings().getScriptClassLoader().loadClass(pluginPath).newInstance();
            megastructure.specID = this.id;
            return megastructure;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
