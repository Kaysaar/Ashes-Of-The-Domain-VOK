package data.kaysaar.aotd.vok.scripts;


import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.campaign.command.CustomProductionPanel;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.ui.TechnologyCoreUI;
import data.kaysaar.aotd.vok.ui.AoTDResearchUI;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.tryToGetButtonProd;

public class CoreUITracker implements EveryFrameScript {
    boolean inserted = false;
    boolean insertedOnce = false;
    boolean removed = false;
    NidavelirMainPanelPlugin plugin = null;
    AoTDResearchUI pluginResearch = null;
    GPMegasturcutreMenu pluginMenu = null;
    TechnologyCoreUI coreUiTech = null;
    boolean pausedMusic = true;

    @Override
    public boolean isDone() {
        return false;
    }

    HashMap<ButtonAPI, Object> panelMap = null;
    ButtonAPI currentTab = null;
    String nameOfCurrentTab;
    public static boolean sendSignalToOpenCore = false;
    public static final String memFlag = "$aotd_outpost_state";
    public static final String memFlag2 = "$aotd_technology_tab_state";
    public static void setMemFlag(String value) {
        Global.getSector().getMemory().set(memFlag, value);
    }
    public static void setMemFlagForTechTab(String value) {
        Global.getSector().getMemory().set(memFlag2, value);
    }
    public static String getMemFlagForTechTab(){
        String s = null;
        try {
            s = Global.getSector().getMemory().getString(memFlag2);

        } catch (Exception e) {

        }
        return s;
    }
    public static String getMemFlag() {
        String s = null;
        try {
            s = Global.getSector().getMemory().getString(memFlag);

        } catch (Exception e) {

        }
        return s;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {

        if ((Global.getSector().getCampaignUI().getCurrentCoreTab() == null || Global.getSector().getCampaignUI().getCurrentCoreTab() != CoreUITabId.OUTPOSTS)) {
            inserted = false;
            panelMap = null;
            currentTab = null;
            if(coreUiTech!=null){
                coreUiTech.clearUI();
                coreUiTech = null;

            }
            removed = false;
            insertedOnce = false;
            return;
        }
        if (Global.getSector().getCampaignUI().getCurrentCoreTab() != null) {
            sendSignalToOpenCore = false;
        }
        UIPanelAPI mainParent = ProductionUtil.getCurrentTab();
        if (mainParent == null) return;
        ButtonAPI button = tryToGetButtonProd("doctrine & blueprints");
        ButtonAPI toRemove = tryToGetButtonProd("custom production");

        if (button == null){
            return;
        }
        if(toRemove!=null) {
            mainParent.removeComponent(toRemove);
        }
        if (tryToGetButtonProd(getStringForCoreTab()) == null) {
            insertButton(button, mainParent, "Research & Development", new TooltipMakerAPI.TooltipCreator() {
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
                    tooltip.addSectionHeading("Ashes of the Domain : Vaults of Knowledge",Alignment.MID,0f);
                    tooltip.addPara("%s, %s and %s can be found here",5f,Color.ORANGE,"Research","Custom production","Megastructures");
                }
            }, tryToGetButtonProd("colonies"), 250, Keyboard.KEY_5, false);
        }

        if (shouldHandleReset()) {
            removed = false;
            if (panelMap != null) {
                panelMap.clear();
            }

            insertedOnce = false;
            currentTab = null;
            panelMap = null;
        }

        if (panelMap == null) {
            panelMap = new HashMap<>();
            panelMap.putAll(getPanelMap(mainParent));
        }
        if (panelMap == null) {
            return;
        }


        float y = button.getPosition().getY();
        float x = button.getPosition().getX();
        if (y < 0) {
            y *= -1;
        }

        if (!removed) {
            removed = true;
            boolean found = false;
            if (!insertedOnce) {
                UIComponentAPI componentToReplace = (UIComponentAPI) panelMap.get(button);
                UIData.WIDTH = Global.getSettings().getScreenWidth() - mainParent.getPosition().getX();
                UIData.HEIGHT = componentToReplace.getPosition().getHeight();
                UIData.recompute();
                AoTDResearchUI.HEIGHT = componentToReplace.getPosition().getHeight();
                AoTDResearchUI.WIDTH = Global.getSettings().getScreenWidth() - mainParent.getPosition().getX();
                AoTDResearchUI.recompute();

            }
            removePanels((ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy(mainParent), mainParent, null);
            insertNewPanel(tryToGetButtonProd(getStringForCoreTab()));


        }
        if (currentTab == null && getMemFlag() == null) {
            for (ButtonAPI buttonAPI : panelMap.keySet()) {
                if (buttonAPI.isHighlighted()) {
                    currentTab = buttonAPI;
                    setMemFlag(currentTab.getText().toLowerCase());
                    break;
                }
            }
        }
        if (currentTab == null && getMemFlag() != null) {
            for (ButtonAPI buttonAPI : panelMap.keySet()) {
                if (buttonAPI.getText().toLowerCase().contains(getMemFlag())) {
                    currentTab = buttonAPI;
                }
            }
        }

        if (!hasComponentPresent((UIComponentAPI) panelMap.get(currentTab))) {
            removePanels((ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy(mainParent), mainParent, null);
            mainParent.addComponent((UIComponentAPI) panelMap.get(currentTab));
                setMemFlag(currentTab.getText().toLowerCase());


            ;
        }
        handleButtonsHighlight();
        handleButtons();


    }

    public static @NotNull String getStringForCoreTab() {
        return "research & development";
    }

    private static void removePanels(ArrayList<UIComponentAPI> componentAPIS, UIPanelAPI mainParent, UIComponentAPI panelToIgnore) {
        for (UIComponentAPI componentAPI : componentAPIS) {
            if (componentAPI instanceof ButtonAPI) continue;

            if (componentAPI.equals(panelToIgnore)) continue;
            mainParent.removeComponent(componentAPI);
        }
    }

    private boolean hasComponentPresent(UIComponentAPI component) {
        for (UIComponentAPI buttonAPI : ReflectionUtilis.getChildrenCopy(ProductionUtil.getCurrentTab())) {
            if (component.equals(buttonAPI)) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldHandleReset() {
        for (UIComponentAPI buttonAPI : ReflectionUtilis.getChildrenCopy(ProductionUtil.getCurrentTab())) {
            if (buttonAPI instanceof CustomProductionPanel) {
                return true;
            }
        }
        return false;
    }

    private void handleButtonsHighlight() {
        for (ButtonAPI buttonAPI : panelMap.keySet()) {
            if (!buttonAPI.equals(currentTab)) {
                buttonAPI.unhighlight();
            } else {
                buttonAPI.highlight();
            }
        }

    }

    private void handleButtons() {
        for (ButtonAPI buttonAPI : panelMap.keySet()) {
            if (buttonAPI.isChecked()) {
                buttonAPI.setChecked(false);
                if (!currentTab.equals(buttonAPI)) {
                    ProductionUtil.getCurrentTab().removeComponent((UIComponentAPI) panelMap.get(currentTab));
                    if(buttonAPI.getText().toLowerCase().contains(getStringForCoreTab())){
                        coreUiTech.playSound(coreUiTech.getCurrentlyChosen());
                    } else if (currentTab.getText().toLowerCase().contains(getStringForCoreTab())) {
                        coreUiTech.pauseSound();
                    }
                    currentTab = buttonAPI;
                    setMemFlag(currentTab.getText().toLowerCase());
                }


            }
        }
    }

    private HashMap<ButtonAPI, Object> getPanelMap(UIComponentAPI mainParent) {
        HashMap<ButtonAPI, Object> map = (HashMap<ButtonAPI, Object>) ReflectionUtilis.invokeMethod("getButtonToTab", mainParent);
        return map;
    }

    private void insertButton(ButtonAPI button, UIPanelAPI mainParent, String name, TooltipMakerAPI.TooltipCreator creator, ButtonAPI button2, float size, int keyBind, boolean dissabled) {
        ButtonAPI newButton = createPanelButton(name, size, button.getPosition().getHeight(), keyBind, dissabled, creator).two;

        mainParent.addComponent(newButton).inTL(button.getPosition().getX() + button.getPosition().getWidth() - button2.getPosition().getX() + 1, 0);
        mainParent.bringComponentToTop(newButton);
    }

    private void insertNewPanel(ButtonAPI tiedButton) {
        if (coreUiTech == null) {
            coreUiTech = new TechnologyCoreUI();
            coreUiTech.init(Global.getSettings().createCustom(UIData.WIDTH, UIData.HEIGHT, coreUiTech), getMemFlagForTechTab(), null);
        }

        panelMap.put(tiedButton, coreUiTech.getMainPanel());
    }

    private Pair<CustomPanelAPI, ButtonAPI> createPanelButton(String buttonName, float width, float height, int bindingValue, boolean dissabled, TooltipMakerAPI.TooltipCreator onHoverTooltip) {
        CustomPanelAPI panel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltipMakerAPI = panel.createUIElement(width, height, false);
        ButtonAPI button = tooltipMakerAPI.addButton(buttonName, null, NidavelirMainPanelPlugin.base, NidavelirMainPanelPlugin.bg, Alignment.MID, CutStyle.TOP, width, height, 0f);
        button.setShortcut(bindingValue, false);
        button.setEnabled(!dissabled);
        if (onHoverTooltip != null) {
            tooltipMakerAPI.addTooltipToPrevious(onHoverTooltip, TooltipMakerAPI.TooltipLocation.BELOW);

        }
        panel.addUIElement(tooltipMakerAPI).inTL(0, 0);
        return new Pair(panel, button);
    }
}
