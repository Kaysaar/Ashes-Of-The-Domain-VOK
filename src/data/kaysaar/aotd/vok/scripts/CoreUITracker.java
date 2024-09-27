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
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.ui.AoTDResearchUI;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.tryToGetButtonProd;

public class CoreUITracker implements EveryFrameScript {
    boolean inserted = false;
    boolean insertedOnce = false;
    boolean removed  = false;
    NidavelirMainPanelPlugin plugin = null;
    AoTDResearchUI pluginResearch = null;
    boolean pausedMusic = true;
    @Override
    public boolean isDone() {
        return false;
    }
    HashMap<ButtonAPI,Object>panelMap = null;
    ButtonAPI currentTab = null;
    String nameOfCurrentTab;
    public static final String memFlag  = "$aotd_outpost_state";
    public static void  setMemFlag(String value){
        Global.getSector().getMemory().set(memFlag, value);
    }
    public static String getMemFlag(){
        String s = null;
        try {
            s = Global.getSector().getMemory().getString(memFlag);

        }
        catch (Exception e){

        }
        return s;
    }
    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {

        if((Global.getSector().getCampaignUI().getCurrentCoreTab()==null||Global.getSector().getCampaignUI().getCurrentCoreTab()!= CoreUITabId.OUTPOSTS)){
            inserted = false;
            panelMap = null;
            currentTab = null;
            if(plugin!=null){
                plugin.clearUI();
                plugin=null;
            }

            if(pluginResearch!=null){
                pluginResearch.clearUI();
                pluginResearch=null;
            }
            removed = false;
            insertedOnce = false;
            return;
        }
        ButtonAPI button = tryToGetButtonProd("custom production");
        if(button==null)return;
        UIPanelAPI mainParent = ProductionUtil.getCurrentTab();
        if(mainParent==null)return;

        if(shouldHandleReset()){
            removed = false;
            if(panelMap!=null){
                panelMap.clear();
            }

            insertedOnce = false;
            currentTab = null;
            panelMap = null;
        }
        if(tryToGetButtonProd("research")==null){
            insertButton(button, mainParent);
        }
        if(panelMap==null){
            panelMap = new HashMap<>();
            panelMap.putAll(getPanelMap(mainParent));
        }
        if(panelMap==null){return;}
        if(currentTab==null&&getMemFlag()==null){
            for (ButtonAPI buttonAPI : panelMap.keySet()) {
                if(buttonAPI.isHighlighted()){
                    currentTab = buttonAPI;
                    setMemFlag(currentTab.getText().toLowerCase());
                    break;
                }
            }
        }

            float y = button.getPosition().getY();
            if(y<0){
                y*=-1;
            }

            if(!removed){
                removed = true;
                boolean found = false;
                if(!insertedOnce){
                    UIComponentAPI componentToReplace = (UIComponentAPI) panelMap.get(button);
                    UIData.WIDTH = Global.getSettings().getScreenWidth()-mainParent.getPosition().getX();
                    UIData.HEIGHT =componentToReplace.getPosition().getHeight();
                    UIData.recompute();
                    AoTDResearchUI.HEIGHT = componentToReplace.getPosition().getHeight();
                    AoTDResearchUI.WIDTH = Global.getSettings().getScreenWidth()-mainParent.getPosition().getX();
                    AoTDResearchUI.recompute();

                }
                removePanels((ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy(mainParent), mainParent,null);

                if(!insertedOnce){
                    insertedOnce =  true;
                    if(GPManager.isEnabled){
                        insertNewPanel(button);
                    }
                    insertNewResearchPanel(tryToGetButtonProd("research"));

                }


            }

        if(currentTab==null&&getMemFlag()!=null){
            for (ButtonAPI buttonAPI : panelMap.keySet()) {
                if(buttonAPI.getText().toLowerCase().contains(getMemFlag())){
                    currentTab = buttonAPI;
                }
            }
        }

        if(!hasComponentPresent((UIComponentAPI) panelMap.get(currentTab))){
            removePanels((ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy(mainParent), mainParent, null);

            mainParent.addComponent((UIComponentAPI) panelMap.get(currentTab));
            mainParent.bringComponentToTop((UIComponentAPI) panelMap.get(currentTab));
            setMemFlag(currentTab.getText().toLowerCase());
   ;
        }
        handleButtonsHighlight();
        handleButtons();







    }

    private static void removePanels(ArrayList<UIComponentAPI> componentAPIS, UIPanelAPI mainParent,UIComponentAPI panelToIgnore) {
        for (UIComponentAPI componentAPI : componentAPIS) {
            if(componentAPI instanceof ButtonAPI)continue;

            if(componentAPI.equals(panelToIgnore))continue;
            mainParent.removeComponent(componentAPI);
        }
    }

    private boolean hasComponentPresent(UIComponentAPI component){
    for (UIComponentAPI buttonAPI : ReflectionUtilis.getChildrenCopy(ProductionUtil.getCurrentTab())) {
        if(component.equals(buttonAPI)){
            return true;
        }
    }
    return false;
}
private boolean shouldHandleReset(){
    for (UIComponentAPI buttonAPI : ReflectionUtilis.getChildrenCopy(ProductionUtil.getCurrentTab())) {
        if(buttonAPI instanceof CustomProductionPanel){
            return true;
        }
    }
    return false;
}
private void handleButtonsHighlight(){
    for (ButtonAPI buttonAPI : panelMap.keySet()) {
        if(!buttonAPI.equals(currentTab)){
         buttonAPI.unhighlight();
        }
        else{
            buttonAPI.highlight();
        }
    }

}
private void handleButtons(){
    for (ButtonAPI buttonAPI : panelMap.keySet()) {
        if(buttonAPI.isChecked()){
            buttonAPI.setChecked(false);
            if(!currentTab.equals(buttonAPI)){
                ProductionUtil.getCurrentTab().removeComponent((UIComponentAPI) panelMap.get(currentTab));
                currentTab = buttonAPI;
                setMemFlag(currentTab.getText().toLowerCase());
                if(currentTab.getText().toLowerCase().contains("custom production")&& pausedMusic){
                    plugin.playSound();
                    pausedMusic = false;
                }
                else if (!pausedMusic){
                    plugin.pauseSound();
                    pausedMusic = true;
                }
            }


        }
    }
}
private HashMap<ButtonAPI,Object>getPanelMap(UIComponentAPI mainParent){
   HashMap<ButtonAPI,Object>map =  (HashMap<ButtonAPI, Object>) ReflectionUtilis.invokeMethod("getButtonToTab",mainParent);
   return map;
}
    private void insertButton(ButtonAPI button, UIPanelAPI mainParent) {
        ButtonAPI newButton = createPanelButton("Research", 120, button.getPosition().getHeight(), Keyboard.KEY_6, false, new TooltipMakerAPI.TooltipCreator() {
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
        ButtonAPI button2= tryToGetButtonProd("colonies");
        mainParent.addComponent(newButton).inTL(button.getPosition().getX()+ button.getPosition().getWidth()-button2.getPosition().getX()+1,0);
        mainParent.bringComponentToTop(newButton);
    }

    private void insertNewPanel(ButtonAPI tiedButton) {
        if(plugin==null){
            plugin  = new NidavelirMainPanelPlugin(false,Global.getSector().getCampaignUI().getCurrentCoreTab(),null);
            plugin.init(Global.getSettings().createCustom(UIData.WIDTH,UIData.HEIGHT,plugin),null,null);
        }

        panelMap.put(tiedButton,plugin.getPanel());
    }
    private void insertNewResearchPanel(ButtonAPI tiedButton) {
        if(pluginResearch==null){
            pluginResearch  = new AoTDResearchUI();
            pluginResearch.init(Global.getSettings().createCustom(AoTDResearchUI.WIDTH+6,AoTDResearchUI.HEIGHT,pluginResearch),null,null);
        }

        panelMap.put(tiedButton,pluginResearch.getPanel());
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
