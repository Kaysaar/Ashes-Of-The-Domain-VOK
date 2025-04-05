package data.kaysaar.aotd.vok.scripts.misc.purplestuff;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;

public class CoreUIListener implements CoreUITabListener {
    @Override
    public void reportAboutToOpenCoreTab(CoreUITabId tab, Object param) {
        if(tab.equals(CoreUITabId.FLEET)){
            Global.getSector().addTransientScript(new InsertTalkToOfficerButton());
        }
    }
}
