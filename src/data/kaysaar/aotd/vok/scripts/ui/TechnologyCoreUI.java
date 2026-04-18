package data.kaysaar.aotd.vok.scripts.ui;

import ashlib.data.plugins.coreui.CommandTabMemoryManager;
import ashlib.data.plugins.coreui.CommandUIPlugin;
import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;

import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;

import data.kaysaar.aotd.vok.ui.UIData;
import data.kaysaar.aotd.vok.ui.customprod.ProductionMainPanel;
import data.kaysaar.aotd.vok.ui.research.AoTDResearchNewPlugin;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectUIManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager.memflagBlacksite;

public class TechnologyCoreUI extends CommandUIPlugin {
    AoTDResearchNewPlugin pluginResearch = null;
    ProductionMainPanel productionMainPanel = null;
    SpecialProjectUIManager specialProjectUIManager = null;
    boolean pausedMusic = true;

    public TechnologyCoreUI(float width, float height) {
        super(width, height);
    }

    public HashMap<ButtonAPI, CustomPanelAPI> getPanelMap() {
        return panelMap;
    }

    @Override
    public boolean doesPlayCustomSound() {
        return true;
    }

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public void init(String panelToShowcase, Object data) {

        this.panelForPlugins = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight() - 45, null);
        if (panelToShowcase == null) {
            panelToShowcase = "production";
        }

        AoTDMainResearchManager.getInstance().getManagerForPlayer().executeResearchCouncilAdvance(0f);

        UIData.WIDTH = panelForPlugins.getPosition().getWidth();
        UIData.HEIGHT = panelForPlugins.getPosition().getHeight();
        createButtonsAndMainPanels();

        for (Map.Entry<ButtonAPI, CustomPanelAPI> buttons : panelMap.entrySet()) {
            if (buttons.getKey().getText().toLowerCase().contains(panelToShowcase)) {
                currentlyChosen = buttons.getKey();
                break;
            }
        }
        for (CustomPanelAPI value : panelMap.values()) {
            panelForPlugins.addComponent(value).inTL(0,0);
        }

        if (currentlyChosen != null) {
            for (Map.Entry<ButtonAPI, CustomPanelAPI> entry : panelMap.entrySet()) {
                Fader fader = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getFader",entry.getValue());
                if(entry.getKey().equals(currentlyChosen)) {
                    fader.forceIn();
                }
                else{
                    fader.forceOut();
                }
            }
        }
        this.mainPanel.addComponent(panelForPlugins).inTL(0, 35);
    }

    public void resetCurrentPlugin(ButtonAPI newButton) {
        currentlyChosen = newButton;
        for (Map.Entry<ButtonAPI, CustomPanelAPI> entry : panelMap.entrySet()) {
            Fader fader = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getFader",entry.getValue());
            if(entry.getKey().equals(currentlyChosen)) {
                fader.forceIn();
            }
            else{
                fader.forceOut();
            }
        }
        playSound(currentlyChosen);
    }

    @Override
    public void clearUI() {
        super.clearUI();
    }

    public void clearUI(boolean clearMusic) {

        panelMap.clear();
        if(clearMusic){
            pauseSound();
        }

    }

    public void createButtonsAndMainPanels() {
        ButtonAPI research, megastructures, customProd,sp;
        this.buttonPanel = this.mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), 25, null);
        UILinesRenderer renderer = new UILinesRenderer(0f);
        CustomPanelAPI panelHelper = this.buttonPanel.createCustomPanel(490, 0.5f, renderer);
//        renderer.setPanel(panelHelper);
        TooltipMakerAPI buttonTooltip = buttonPanel.createUIElement(mainPanel.getPosition().getWidth(), 20, false);
        Color base, bg;
        base = Global.getSector().getPlayerFaction().getBaseUIColor();
        bg = Global.getSector().getPlayerFaction().getDarkUIColor();
        research = buttonTooltip.addButton("Research", pluginResearch, base, bg, Alignment.MID, CutStyle.TOP, 140, 20, 0f);
        customProd = buttonTooltip.addButton("Production", productionMainPanel, base, bg, Alignment.MID, CutStyle.TOP, 140, 20, 0f);

        sp = null;
        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().getAmountOfBlackSites()>0){
            Global.getSector().getPlayerMemoryWithoutUpdate().set(memflagBlacksite,true);
        }
        if(BlackSiteProjectManager.getInstance().canEngageInBlackSite()){
            sp = buttonTooltip.addButton("Black Site Projects", specialProjectUIManager, Global.getSector().getFaction(Factions.PIRATES).getBaseUIColor(), Global.getSector().getFaction(Factions.PIRATES).getDarkUIColor(), Alignment.MID, CutStyle.TOP, 210, 20, 0f);
            sp.setEnabled(AoTDMainResearchManager.getInstance().getManagerForPlayer().getAmountOfBlackSites()>0);
            if(!sp.isEnabled()){
                buttonTooltip.addTooltipTo(new TooltipMakerAPI.TooltipCreator() {
                    @Override
                    public boolean isTooltipExpandable(Object tooltipParam) {
                        return false;
                    }

                    @Override
                    public float getTooltipWidth(Object tooltipParam) {
                        return 300;
                    }

                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        tooltip.addPara("To access this section you need to have at least one functional %s",2f,Color.ORANGE,"Black Site");
                    }
                },sp, TooltipMakerAPI.TooltipLocation.BELOW,false);
            }
            sp.setShortcut(Keyboard.KEY_S,false);
        }
        else{
            sp = buttonTooltip.addButton("?????", specialProjectUIManager, base, bg, Alignment.MID, CutStyle.TOP, 210, 20, 0f);
            sp.setEnabled(false);

        }

        customProd.setShortcut(Keyboard.KEY_Q, false);
        research.setShortcut(Keyboard.KEY_R, false);
        customProd.getPosition().inTL(0, 0);
        research.getPosition().rightOfMid(customProd,1);
        sp.getPosition().rightOfMid(research,1);
        insertNewResearchPanel(research);
        insertCustomProdPanel(customProd);
        insertSpecialProjectPanel(sp);
        buttonPanel.addUIElement(buttonTooltip).inTL(0, 0);
        buttonPanel.addComponent(panelHelper).inTL(0, 20);
        mainPanel.addComponent(buttonPanel).inTL(0, 10);

    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    public ButtonAPI getCurrentlyChosen() {
        return currentlyChosen;
    }

    @Override
    public String getTabStateId() {
        return "research & production";
    }

    @Override
    public void advance(float amount) {

        for (Map.Entry<ButtonAPI, CustomPanelAPI> entry : panelMap.entrySet()) {
            entry.getKey().unhighlight();
            if (entry.getKey().isChecked()) {
                entry.getKey().setChecked(false);
                if (!entry.getKey().equals(currentlyChosen)) {
                    resetCurrentPlugin(entry.getKey());
                    CommandTabMemoryManager.getInstance().getTabStates().put(getTabStateId(),entry.getKey().getText().toLowerCase());
                }


                break;
            }
        }
        if (currentlyChosen != null) {
            currentlyChosen.highlight();
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    private void insertCustomProdPanel(ButtonAPI tiedButton) {
        if (productionMainPanel == null) {
            productionMainPanel = new ProductionMainPanel(UIData.WIDTH, UIData.HEIGHT);
        }

        panelMap.put(tiedButton, productionMainPanel.getMainPanel());
    }

    private void insertNewResearchPanel(ButtonAPI tiedButton) {
        if (pluginResearch == null) {
            pluginResearch = new AoTDResearchNewPlugin(UIData.WIDTH-10, UIData.HEIGHT);
        }

        panelMap.put(tiedButton,pluginResearch.getMainPanel());
    }

    private void insertNewMegastructuresPanel(ButtonAPI tiedButton) {
//        if (pluginMega == null) {
//            pluginMega = new GPMegastructureMenu();
//            pluginMega.init(Global.getSettings().createCustom(UIData.WIDTH, UIData.HEIGHT, pluginMega));
//        }
//
//        panelMap.put(tiedButton, pluginMega.getMainPanel());
    }
    private void insertSpecialProjectPanel(ButtonAPI tiedButton) {
        if (specialProjectUIManager == null) {
            specialProjectUIManager = new SpecialProjectUIManager(UIData.WIDTH, UIData.HEIGHT);
        }

        panelMap.put(tiedButton, specialProjectUIManager.getMainPanel());
    }
    public void pauseSound() {
        Global.getSoundPlayer().pauseCustomMusic();
        Global.getSoundPlayer().restartCurrentMusic();
        pausedMusic = true;
    }

    @Override
    public void playSound(Object data) {
        playSoundButton((ButtonAPI) data);
    }

    public void playSoundButton(ButtonAPI button) {
        if (button.getText().toLowerCase().contains("production")) {
            productionMainPanel.playSound();
        }
        if (button.getText().toLowerCase().contains("research")) {
            pluginResearch.playSound();
        }

//        if (button.getText().toLowerCase().contains("megastructures")) {
//            pluginMega.playSound();
//        }
        if (button.getText().toLowerCase().contains("black site projects")) {
            specialProjectUIManager.playSound();
        }
    }

}
