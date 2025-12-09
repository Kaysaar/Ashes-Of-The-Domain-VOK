package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import java.util.LinkedHashMap;

public class ColonyDevelopmentManager {
    public static String permaDataKey = "$aotd_colony_development";
    private static void setInstance() {
        Global.getSector().getPersistentData().put(permaDataKey, new ColonyDevelopmentManager());
    }
    LinkedHashMap<String,BaseColonyDevelopment>developmentScripts;

    public static ColonyDevelopmentManager getInstance() {
        if (Global.getSector().getPersistentData().get(permaDataKey) == null) {
            setInstance();
        }
        ColonyDevelopmentManager manager = (ColonyDevelopmentManager) Global.getSector().getPersistentData().get(permaDataKey);
        manager.ensureListsAreInitalized();
        return manager;
    }
    public BaseColonyDevelopment getColonyDevelopment(String id) {
        return developmentScripts.get(id);
    }

    public LinkedHashMap<String, BaseColonyDevelopment> getDevelopmentScripts() {
        return developmentScripts;
    }

    public float getDaysOnMarket(MarketAPI market){
        if(!market.hasCondition(BaseColonyDevelopment.condIdApplier)){
            return 0f;
        }
        ColonyDevelopmentCondition cond = (ColonyDevelopmentCondition) market.getCondition(BaseColonyDevelopment.condIdApplier).getPlugin();
        return cond.getTooltipWidth();

    }
    protected void ensureListsAreInitalized(){
        if(developmentScripts==null)developmentScripts = new LinkedHashMap<>();
    }
    public void addDevelopmentScriptBase(String id, BaseColonyDevelopment script) {
        if(developmentScripts==null)developmentScripts = new LinkedHashMap<>();
        developmentScripts.put(id, script);
    }
}
