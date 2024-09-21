package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.ui.ButtonAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.HolderDialog;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CoreUiInterceptor implements CoreUITabListener {
    @Override
    public void reportAboutToOpenCoreTab(CoreUITabId tab, Object param) {
        if(!GPManager.isEnabled)return;

        if(Global.getSector().getCampaignUI().getCurrentInteractionDialog()==null){
            Global.getSector().getCampaignUI().showInteractionDialog(new HolderDialog(tab,param),null);
        }
       if(tab==CoreUITabId.OUTPOSTS){
           HolderDialog.sendSignalForPressing= true;
       }
    }
}
