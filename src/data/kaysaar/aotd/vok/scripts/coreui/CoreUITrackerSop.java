package data.kaysaar.aotd.vok.scripts.coreui;

import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.campaign.command.CustomProductionPanel;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.industry.MiscHiddenIndustry;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.ui.TechnologyCoreUI;
import data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;
import data.kaysaar.aotd.vok.ui.patrolfleet.PatrolFleetDataManager;
import data.misc.AoTDSopMisc;
import data.misc.UIDataSop;
import data.ui.FactionPanel;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.tryToGetButtonProd;

public class CoreUITrackerSop extends CoreUITracker{
    boolean inserted = false;
    boolean insertedOnce = false;
    boolean removed = false;
    TechnologyCoreUI coreUiTech = null;
    PatrolFleetDataManager fleetManager = null;
    boolean pausedMusic = true;
    FactionPanel factionPanel ;
    boolean needsToResetStates = false;
    @Override
    public boolean isDone() {
        return false;
    }

    HashMap<ButtonAPI, Object> panelMap = null;
    ButtonAPI currentTab = null;
    String nameOfCurrentTab;
    boolean tunedMusicOnce= false;
    public static boolean sendSignalToOpenCore = false;
    public static final String memFlag = "$aotd_outpost_state";
    public static final String memFlag2 = "$aotd_technology_tab_state";
    public static final String memFlag3 = "$aotd_faction_tab_state";
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
    public static String getMemFlagForFactinTab(){
        String s = null;
        try {
            s = Global.getSector().getMemory().getString(memFlag3);

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
                coreUiTech.clearUI(tunedMusicOnce);
                coreUiTech = null;

            }
            if(factionPanel!=null){
                factionPanel.clearUI(tunedMusicOnce);
                factionPanel = null;
            }
            tunedMusicOnce = false;
            removed = false;
            insertedOnce = false;
            return;
        }
        if (Global.getSector().getCampaignUI().getCurrentCoreTab() != null) {
            sendSignalToOpenCore = false;
        }
        UIPanelAPI mainParent = ProductionUtil.getCurrentTab();
        if (mainParent == null) return;
        ButtonAPI button = tryToGetButtonProd("income");
        ButtonAPI toRemove2 = tryToGetButtonProd("orders");


        ButtonAPI toRemove = tryToGetButtonProd("custom production");
        if (button == null){
            return;
        }
        if(toRemove!=null) {
            mainParent.removeComponent(toRemove);
        }
//        if(toRemove2!=null) {
//            mainParent.removeComponent(toRemove2);
//        }
        if (tryToGetButtonProd(getStringForCoreTabResearch()) == null) {
            mainParent.removeComponent(tryToGetButtonProd("doctrine & blueprints"));
            insertButton(button, mainParent, "Doctrine & Blueprints", null, tryToGetButtonProd("colonies"), 200, Keyboard.KEY_4, false);

            insertButton(tryToGetButtonProd("doctrine & blueprints"), mainParent, "Research & Production", new TooltipMakerAPI.TooltipCreator() {
                @Override
                public boolean isTooltipExpandable(Object tooltipParam) {
                    return false;
                }

                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return 500;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addSectionHeading("Ashes of the Domain : Vaults of Knowledge", Alignment.MID,0f);
                    tooltip.addPara("In this tab, you can find the research tab to manage your technological advancement and special technology projects, the custom order tab to build new ships and weapons as well as launch special military projects, and the megastructure tab to manage the Domain-era marvels you've seized or built.",5f);
                }
            }, tryToGetButtonProd("colonies"), 240, Keyboard.KEY_5, false);
        }
        if(toRemove2!=null){


            insertButton(tryToGetButtonProd("colonies"), mainParent, "Faction", new TooltipMakerAPI.TooltipCreator() {
                @Override
                public boolean isTooltipExpandable(Object tooltipParam) {
                    return false;
                }

                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return 500;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addSectionHeading("Ashes of the Domain : Seats Of Power",Alignment.MID,0f);
                    tooltip.addPara("In this tab,your empire timeline and can govern your faction by changing policies.",5f);
                }
            }, tryToGetButtonProd("colonies"), toRemove2.getPosition().getWidth(), Keyboard.KEY_2, false);

            mainParent.removeComponent(toRemove2);
            tryToGetButtonProd("income").getPosition().rightOfMid(tryToGetButtonProd("faction"),1f);
            AoTDSopMisc.tryToGetButtonProd("faction").setEnabled(!Misc.getFactionMarkets(Factions.PLAYER).isEmpty());

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
                UIData.WIDTH = Global.getSettings().getScreenWidth() - tryToGetButtonProd("colonies").getPosition().getX();
                UIData.HEIGHT = componentToReplace.getPosition().getHeight();
                    UIDataSop.WIDTH = UIData.WIDTH;
                    UIDataSop.HEIGHT = UIData.HEIGHT;
                
                UIData.recompute();

            }
            removePanels((ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy(mainParent), mainParent, null);
            insertNewPanel(tryToGetButtonProd(getStringForCoreTabResearch()));
            insertNewPanelFaction(tryToGetButtonProd(getStringForFaction()));


        }
        if (currentTab == null && getMemFlag() == null) {
            for (ButtonAPI buttonAPI : panelMap.keySet()) {
                if (buttonAPI.isHighlighted()) {
                    currentTab = buttonAPI;
                    setMemFlag(currentTab.getText().toLowerCase());
                    refreshHiddenInd(currentTab);
                    break;
                }
            }
        }
        if (currentTab == null && getMemFlag() != null) {
            for (ButtonAPI buttonAPI : panelMap.keySet()) {
                if (buttonAPI.getText().toLowerCase().contains(getMemFlag())) {
                    currentTab = buttonAPI;
                    refreshHiddenInd(currentTab);
                }
            }
        }


        if(currentTab.getText().toLowerCase().contains(getStringForCoreTabResearch())){
            if(!tunedMusicOnce){
                tunedMusicOnce = true;
                if(coreUiTech.getCurrentlyChosen()!=null){
                    coreUiTech.playSound(coreUiTech.getCurrentlyChosen());
                }
            }
        } else if (currentTab.getText().toLowerCase().contains(getStringForFaction())) {
            if(!tunedMusicOnce){
                tunedMusicOnce = true;
                if(coreUiTech.getCurrentlyChosen()!=null){
                    factionPanel.playSound();
                }
            }

        } else{
            tunedMusicOnce = false;
        }
        if (!hasComponentPresent((UIComponentAPI) panelMap.get(currentTab))) {
            removePanels((ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy(mainParent), mainParent, null);
            if(currentTab.getText().toLowerCase().contains("doctrine & blueprints")){
                UIComponentAPI comp =(UIComponentAPI) panelMap.get(currentTab);
                ReflectionUtilis.invokeMethodWithAutoProjection("createIfNeeded",comp);
                ReflectionUtilis.invokeMethodWithAutoProjection("restoreTableUIState", comp);
                Fader fader = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getFader",comp);
                fader.forceIn();
            }
            mainParent.addComponent((UIComponentAPI) panelMap.get(currentTab));
            setMemFlag(currentTab.getText().toLowerCase());


            ;
        }
        handleButtonsHighlight();
        handleButtons();


    }

    public static void refreshHiddenInd(ButtonAPI currentTab) {
        if(currentTab.getText().toLowerCase().contains("income")||currentTab.getText().toLowerCase().contains("colonies")){
            MiscHiddenIndustry.clearHiddenIndustries();
        }
        else{
            for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                MiscHiddenIndustry.getInstance(playerMarket);
            }
        }
    }

    public static @NotNull String getStringForCoreTabResearch() {
        return "research & production";
    }
    public static @NotNull String getStringForPatrolTemplate() {
        return "faction fleets";
    }
    public static @NotNull String getStringForFaction() {
        return "faction";
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
        boolean pauseSound = true;
        if(!tryToGetButtonProd("doctrine & blueprints").isEnabled()){
            if(!needsToResetStates){
                needsToResetStates = true;
                for (ButtonAPI buttonAPI : panelMap.keySet()) {
                    buttonAPI.setEnabled(false);
                }
            }

        }
        else if (needsToResetStates){
            needsToResetStates = false;
            panelMap.keySet().forEach(x->x.setEnabled(true));
            AoTDSopMisc.tryToGetButtonProd("faction").setEnabled(!Misc.getFactionMarkets(Factions.PLAYER).isEmpty());
        }

        for (ButtonAPI buttonAPI : panelMap.keySet()) {
            if (buttonAPI.isChecked()) {
                buttonAPI.setChecked(false);
                if (!currentTab.equals(buttonAPI)) {
                    refreshHiddenInd(buttonAPI);
                    ProductionUtil.getCurrentTab().removeComponent((UIComponentAPI) panelMap.get(currentTab));
                    if(buttonAPI.getText().toLowerCase().contains(getStringForCoreTabResearch())){
                        if(coreUiTech.getCurrentlyChosen()!=null){
                            coreUiTech.playSound(coreUiTech.getCurrentlyChosen());
                            pauseSound = false;
                        }
                    } else if (currentTab.getText().toLowerCase().contains(getStringForCoreTabResearch())) {
                        coreUiTech.pauseSound();
                    }
                    if(buttonAPI.getText().toLowerCase().contains(getStringForFaction())){
                        factionPanel.playSound();

                    } else if (currentTab.getText().toLowerCase().contains(getStringForFaction())&&pauseSound) {
                        factionPanel.pauseSound();
                    }

                    currentTab = buttonAPI;

                    setMemFlag(currentTab.getText().toLowerCase());
                }


            }
        }
    }

    private HashMap<ButtonAPI, Object> getPanelMap(UIComponentAPI mainParent) {
        HashMap<ButtonAPI, Object> map = (HashMap<ButtonAPI, Object>) ReflectionUtilis.invokeMethod("getButtonToTab", mainParent);
        ButtonAPI replace = null;
        for (ButtonAPI value : map.keySet()) {
            if(value.getText().toLowerCase().contains("doctrine & blueprints")){
                replace = value;
                break;
            }
        }
        Object target = map.get(replace);
        map.remove(replace);
        map.put(tryToGetButtonProd("doctrine & blueprints"), target);
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
    private void insertNewPanelFaction(ButtonAPI tiedButton) {
        if (factionPanel == null) {
            factionPanel = new FactionPanel();
            float width = UIDataSop.WIDTH;
            float height = UIDataSop.HEIGHT;
            factionPanel.init(Global.getSettings().createCustom(width, height, factionPanel), getMemFlagForFactinTab(), panelMap.get(tryToGetButtonProd("colonies")));
        }

        panelMap.put(tiedButton, factionPanel.getMainPanel());
    }
    private void insertNewPanelPatrol(ButtonAPI tiedButton) {
        if (fleetManager == null) {
            fleetManager = new PatrolFleetDataManager(UIData.WIDTH, UIData.HEIGHT);
        }

        panelMap.put(tiedButton, fleetManager.getMainPanel());
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
