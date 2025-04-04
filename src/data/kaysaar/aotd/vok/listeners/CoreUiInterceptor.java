package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;
import data.kaysaar.aotd.vok.scripts.CoreUITracker;
import data.kaysaar.aotd.vok.scripts.CoreUITracker2;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIInMarketScript;

public class CoreUiInterceptor implements CoreUITabListener {
    @Override
    public void reportAboutToOpenCoreTab(CoreUITabId tab, Object param) {
        if(tab.equals(CoreUITabId.CARGO)){
            CoreUITracker2.didIt = false;
            AoTDCompoundUIInMarketScript.didIt = false;
        }
        if(param instanceof  String){
            String s = (String) param;
            if(s.equals("income_report")){
                CoreUITracker.setMemFlag("income");
            }
        }
        if(param instanceof MarketAPI){
            CoreUITracker.setMemFlag("colonies");
        }
        CoreUITracker.sendSignalToOpenCore = true;
    }
}
