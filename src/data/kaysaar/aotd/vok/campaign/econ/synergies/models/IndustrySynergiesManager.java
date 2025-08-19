package data.kaysaar.aotd.vok.campaign.econ.synergies.models;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import java.util.LinkedHashMap;
import java.util.List;

public class IndustrySynergiesManager {
    public static String permaDataKey = "$aotd_industry_synergies";
    public class IndustrySynergiesManagerAdvancer implements EveryFrameScript {
        public IntervalUtil util = new IntervalUtil(2.5f,2.5f);
        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean runWhilePaused() {
            return false;
        }

        @Override
        public void advance(float amount) {
            util.advance(amount);
            if(util.intervalElapsed()){
                IndustrySynergiesManager.getInstance().advanceImpl(util.getElapsed());
            }

        }
    }
    public static IndustrySynergiesManager getInstance() {
        if (Global.getSector().getPersistentData().get(permaDataKey) == null) {
            setInstance();
        }
        return (IndustrySynergiesManager) Global.getSector().getPersistentData().get(permaDataKey);
    }
    public void ensureHasMoverScript(){
        if(!Global.getSector().hasTransientScript(IndustrySynergiesManagerAdvancer.class)){
            Global.getSector().addTransientScript(new IndustrySynergiesManagerAdvancer());
        }
    }

    public LinkedHashMap<String, BaseIndustrySynergy> synergyScripts = new LinkedHashMap<>();

    public List<BaseIndustrySynergy> getSynergyScripts() {
        return synergyScripts.values().stream().toList();
    }
    public List<BaseIndustrySynergy> getSynergyScriptsValidForMarket(MarketAPI market) {

        if(market==null)return getSynergyScripts();
        return synergyScripts.values().stream().filter(x->x.doesSynergyMetTotalReq(market)&&!x.runsInEveryFrameScript()).toList();
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
    public void advanceImpl(float amount){

        synergyScripts.values().stream().filter(BaseIndustrySynergy::runsInEveryFrameScript).forEach(x->x.advanceInEveryFrameScript(amount));
    }


    public boolean hasSynergy(String id){
        return synergyScripts.containsKey(id);
    }

    public void addSynergy(String id, BaseIndustrySynergy script) {
        synergyScripts.put(id, script);
    }

}
