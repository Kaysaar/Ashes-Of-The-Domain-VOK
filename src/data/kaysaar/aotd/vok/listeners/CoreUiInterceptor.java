package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.ui.ButtonAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.HolderDialog;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.CoreUITracker;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CoreUiInterceptor implements CoreUITabListener {
    @Override
    public void reportAboutToOpenCoreTab(CoreUITabId tab, Object param) {
        if(param instanceof  String){
            String s = (String) param;
            if(s.equals("income_report")){
                CoreUITracker.setMemFlag("income");
            }
        }
        if(param instanceof MarketAPI){
            CoreUITracker.setMemFlag("colonies");
        }

    }
}
