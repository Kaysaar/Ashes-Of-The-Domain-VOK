package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.HolderDialog;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CoreUiInterceptor implements CoreUITabListener {
    @Override
    public void reportAboutToOpenCoreTab(CoreUITabId tab, Object param) {
        if(!GPManager.isEnabled)return;
        if(tab==CoreUITabId.OUTPOSTS){
            Robot robot = null;
            try {
                robot = new Robot();
                // Simulate pressing the '1' key
                robot.keyPress(KeyEvent.VK_1);  // Press the '1' key
                robot.keyRelease(KeyEvent.VK_1);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }
        if(Global.getSector().getCampaignUI().getCurrentInteractionDialog()==null){

        }
    }
}
