package data.kaysaar.aotd.vok.scripts.misc.purplestuff;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

public class InsertTalkToOfficerButton implements EveryFrameScript {

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }
    public boolean buttonInserted = false;
    @Override
    public void advance(float amount) {
         if (CoreUITabId.FLEET.equals(Global.getSector().getCampaignUI().getCurrentCoreTab())&&!buttonInserted){
            UIPanelAPI currentTab= ProductionUtil.getCurrentTab();
            UIPanelAPI savedMain = null;
            for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(currentTab)) {
                if(ReflectionUtilis.hasMethodOfName("getSubmarket",componentAPI))continue;
                if(componentAPI instanceof ButtonAPI)continue;
                if(ReflectionUtilis.hasMethodOfName("showSubmarket",componentAPI))continue;
                if(ReflectionUtilis.hasMethodOfName("showSubmarketTextDialog",componentAPI))continue;
                savedMain = (UIPanelAPI) componentAPI;
                break;

            }
            if(savedMain!=null){
                ButtonAPI buttonIdleOfficers = null;
                for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(savedMain)) {
                    if(componentAPI instanceof ButtonAPI){
                      if(AshMisc.isStringValid(((ButtonAPI) componentAPI).getText())){
                          if(((ButtonAPI) componentAPI).getText().contains("   Auto-assign idle officers")){
                              buttonIdleOfficers = (ButtonAPI) componentAPI;
                              break;
                          }
                      }

                    }
                }
                if(buttonIdleOfficers!=null){
                    TalkToOfficerDialogButton panelButton = new TalkToOfficerDialogButton(buttonIdleOfficers.getPosition().getWidth(),buttonIdleOfficers.getPosition().getHeight());
                    savedMain.addComponent(panelButton.getMainPanel()).inTL(0,Global.getSettings().getScreenHeightPixels()-buttonIdleOfficers.getPosition().getY()+100);
                    buttonInserted = true;
                    Global.getSector().removeTransientScriptsOfClass(this.getClass());
                }
            }
        }
    }
}
