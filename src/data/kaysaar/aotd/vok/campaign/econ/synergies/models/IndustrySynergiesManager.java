package data.kaysaar.aotd.vok.campaign.econ.synergies.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import java.util.LinkedHashMap;
import java.util.List;

public class IndustrySynergiesManager {
    public static String permaDataKey = "$aotd_industry_synergies";

    public static IndustrySynergiesManager getInstance() {
        if (Global.getSector().getPersistentData().get(permaDataKey) == null) {
            setInstance();
        }
        return (IndustrySynergiesManager) Global.getSector().getPersistentData().get(permaDataKey);
    }

    public LinkedHashMap<String, BaseIndustrySynergy> synergyScripts = new LinkedHashMap<>();

    public List<BaseIndustrySynergy> getSynergyScripts() {
        return synergyScripts.values().stream().toList();
    }
    public List<BaseIndustrySynergy> getSynergyScriptsValidForMarket(MarketAPI market) {

        if(market==null)return getSynergyScripts();
        return synergyScripts.values().stream().filter(x->x.doesSynergyMetReq(market)).toList();
    }
    public List<BaseIndustrySynergy> getSynergiesNotValidForMarket(MarketAPI market) {

        if(market==null)return getSynergyScripts();
        return synergyScripts.values().stream().filter(x->!x.doesSynergyMetReq(market)).toList();
    }
    public List<BaseIndustrySynergy> getSynergyScriptsValidForMarketInUI(MarketAPI market) {

        if(market==null)return getSynergyScripts();
        return synergyScripts.values().stream().filter(x->x.doesSynergyMetReq(market)&&x.canShowSynergyInUI(market)).toList();
    }
    public List<BaseIndustrySynergy> getSynergiesNotValidForMarketInUI(MarketAPI market) {

        if(market==null)return getSynergyScripts();
        return synergyScripts.values().stream().filter(x->!x.doesSynergyMetReq(market)&&x.canShowSynergyInUI(market)).toList();
    }
    public static void setInstance() {
        Global.getSector().getPersistentData().put(permaDataKey, new IndustrySynergiesManager());
    }


    public boolean hasSynergy(String id){
        return synergyScripts.containsKey(id);
    }

    public void addSynergy(String id, BaseIndustrySynergy script) {
        synergyScripts.put(id, script);
    }

}
