package data.kaysaar.aotd.vok.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.campaign.command.CustomProductionPanel;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelDelegate;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.util.ArrayList;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.HolderDialog.sendSignalForPressing;
import static data.kaysaar.aotd.vok.misc.AoTDMisc.tryToGetButtonProd;

public class CoreUITracker implements EveryFrameScript {
    boolean inserted = false;
    NidavelirMainPanelPlugin plugin = null;
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
        if(!GPManager.isEnabled)return;
        if(Global.getSector().getCampaignUI().getCurrentCoreTab()==null){
            inserted = false;
            return;
        }
        ButtonAPI button = tryToGetButtonProd("custom production");

        if(button!=null){
            UIPanelAPI mainParent = (UIPanelAPI) ReflectionUtilis.invokeMethod("getCurrentTab",ProductionUtil.getCoreUI());
            if(button.isHighlighted()){
                ArrayList<UIComponentAPI> componentAPIS  = (ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy(mainParent);
                boolean found = false;
                for (UIComponentAPI componentAPI : componentAPIS) {
                    if(componentAPI instanceof CustomProductionPanel){
                        found = true;
                        UIData.WIDTH = componentAPI.getPosition().getWidth();
                        UIData.HEIGHT = componentAPI.getPosition().getHeight();
                        UIData.recompute();
                        mainParent.removeComponent(componentAPI);
                        insertNewPanel(mainParent);
                    }
                }
                if(!found&&!inserted){
                    insertNewPanel(mainParent);
                }
            }
            else if (!button.isHighlighted()&&plugin!=null){
                inserted = false;
                plugin.clearUI();
                plugin=null;
            }
        }

    }

    private void insertNewPanel(UIPanelAPI mainParent) {
        plugin  = new NidavelirMainPanelPlugin(false,Global.getSector().getCampaignUI().getCurrentCoreTab(),null);
        plugin.init(Global.getSettings().createCustom(UIData.WIDTH,UIData.HEIGHT,plugin),null,null);
        mainParent.addComponent(plugin.getPanel());
        mainParent.bringComponentToTop(plugin.getPanel());
        inserted= true;
    }
}
