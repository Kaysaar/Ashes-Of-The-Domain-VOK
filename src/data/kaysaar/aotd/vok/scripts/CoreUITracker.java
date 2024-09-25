package data.kaysaar.aotd.vok.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.campaign.command.CustomProductionPanel;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelDelegate;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.HolderDialog.sendSignalForPressing;
import static data.kaysaar.aotd.vok.misc.AoTDMisc.tryToGetButtonProd;

public class CoreUITracker implements EveryFrameScript {
    boolean inserted = false;
    boolean insertedOnce = false;
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
            float y = button.getPosition().getY();
            if(y<0){
                y*=-1;
            }

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
                if(!found&&plugin==null){
                    insertNewPanel(mainParent);
                }

            }
            else if (!button.isHighlighted()&&plugin!=null){
                inserted = false;
                plugin.clearUI();
                plugin=null;
            }
            if(!insertedOnce){
                insertedOnce = true;
                insertButton(button, mainParent);
            }
            if(tryToGetButtonProd("research")==null){
                insertedOnce = false;
            }
        }



    }

    private void insertButton(ButtonAPI button, UIPanelAPI mainParent) {
        ButtonAPI newButton = createPanelButton("Research", 120, button.getPosition().getHeight(), Keyboard.KEY_6, true, new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 400;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("This is where your faction can conduct all research projects!", Misc.getTooltipTitleAndLightHighlightColor(),5f);
                tooltip.addPara("This UI will be accessible from here in next major Vaults of Knowledge patch!", Color.ORANGE,5f);

            }
        }).two;
        mainParent.addComponent(newButton).inTL(button.getPosition().getX()+ button.getPosition().getWidth()-9,0);
        mainParent.bringComponentToTop(newButton);
    }

    private void insertNewPanel(UIPanelAPI mainParent) {
        plugin  = new NidavelirMainPanelPlugin(false,Global.getSector().getCampaignUI().getCurrentCoreTab(),null);
        plugin.init(Global.getSettings().createCustom(UIData.WIDTH,UIData.HEIGHT,plugin),null,null);
        mainParent.addComponent(plugin.getPanel());
        mainParent.bringComponentToTop(plugin.getPanel());
        inserted= true;
    }
    private Pair<CustomPanelAPI,ButtonAPI> createPanelButton(String buttonName, float width, float height, int bindingValue, boolean dissabled, TooltipMakerAPI.TooltipCreator onHoverTooltip){
        CustomPanelAPI panel = Global.getSettings().createCustom(width,height,null);
        TooltipMakerAPI tooltipMakerAPI = panel.createUIElement(width,height,false);
       ButtonAPI button =  tooltipMakerAPI.addButton(buttonName,null,NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg,Alignment.MID,CutStyle.TOP,width,height,0f);
       button.setShortcut(bindingValue,false);
       button.setEnabled(!dissabled);
       if(onHoverTooltip!=null){
           tooltipMakerAPI.addTooltipToPrevious(onHoverTooltip, TooltipMakerAPI.TooltipLocation.BELOW);

       }
       panel.addUIElement(tooltipMakerAPI).inTL(0,0);
        return new Pair(panel,button);
    }
}
