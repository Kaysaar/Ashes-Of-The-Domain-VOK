package data.kaysaar.aotd.vok.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.ui.ButtonAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelDelegate;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.HolderDialog.sendSignalForPressing;
import static data.kaysaar.aotd.vok.misc.AoTDMisc.tryToGetButtonProd;

public class CoreUITracker implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        if(Global.getSector().getCampaignUI().getCurrentInteractionDialog()==null)return;
        if(sendSignalForPressing){

            ButtonAPI button = AoTDMisc.tryToGetButtonProd("colonies");
            if(button!=null){

                try{
                    Object tab = ReflectionUtilis.invokeMethod("getCurrentTab", ProductionUtil.getCoreUI());
                    if(tab!=null){
                        ReflectionUtilis.invokeMethod("showReportsTab",tab);
                        sendSignalForPressing = false;
                    }
                } catch (Exception e) {

                }



            }
        }
        if(!NidavelirMainPanelPlugin.isShowingUI){
            ButtonAPI button = tryToGetButtonProd("custom production");
            if(button!=null){
                if(button.isChecked()){
                    ButtonAPI button2 = AoTDMisc.tryToGetButtonProd("colonies");
                    if(button2!=null){
                        button.setChecked(true);
                    }
                    button.setChecked(false);
                    CoreUITabId cur = Global.getSector().getCampaignUI().getCurrentCoreTab();
                    NidavelirMainPanelPlugin.isShowingUI = true;
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                    Global.getSector().getCampaignUI().getCurrentInteractionDialog().showCustomVisualDialog(UIData.WIDTH, UIData.HEIGHT, new NidavelirMainPanelDelegate(new NidavelirMainPanelPlugin(false, cur, null), Global.getSector().getCampaignUI().getCurrentInteractionDialog()));
                    try {
                        Object tab = ReflectionUtilis.invokeMethod("getCurrentTab", ProductionUtil.getCoreUI());
                        if (tab != null) {
                            ReflectionUtilis.invokeMethod("showReportsTab", tab);
                            sendSignalForPressing = false;
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }

    }
}
