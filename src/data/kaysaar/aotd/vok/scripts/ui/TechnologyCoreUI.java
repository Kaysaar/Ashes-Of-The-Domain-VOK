package data.kaysaar.aotd.vok.scripts.ui;

import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;
import data.kaysaar.aotd.vok.scripts.CoreUITracker;
import data.kaysaar.aotd.vok.scripts.SoundUIManager;
import data.kaysaar.aotd.vok.ui.patrolfleet.PatrolFleetDataManager;
import data.kaysaar.aotd.vok.ui.research.AoTDResearchNewPlugin;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechnologyCoreUI implements CustomUIPanelPlugin {
    NidavelirMainPanelPlugin customProdPlugin = null;
    AoTDResearchNewPlugin pluginResearch = null;
    GPMegasturcutreMenu pluginMega = null;
    CustomPanelAPI mainPanel;
    CustomPanelAPI panelForPlugins = null;
    CustomPanelAPI buttonPanel = null;
    ButtonAPI currentlyChosen;
    SoundUIManager manager;
    HashMap<ButtonAPI, CustomPanelAPI> panelMap = new HashMap<>();
    boolean pausedMusic = true;

    public HashMap<ButtonAPI, CustomPanelAPI> getPanelMap() {
        return panelMap;
    }

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public void init(CustomPanelAPI mainPanel, String panelToShowcase, Object data) {
        this.mainPanel = mainPanel;
        this.panelForPlugins = mainPanel.createCustomPanel(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight() - 45, null);
        if (panelToShowcase == null) {
            panelToShowcase = "production";
        }


        UIData.WIDTH = panelForPlugins.getPosition().getWidth();
        UIData.HEIGHT = panelForPlugins.getPosition().getHeight();
        UIData.recompute();
        createButtonsAndMainPanels();

        for (Map.Entry<ButtonAPI, CustomPanelAPI> buttons : panelMap.entrySet()) {
            if (buttons.getKey().getText().toLowerCase().contains(panelToShowcase)) {
                currentlyChosen = buttons.getKey();
                break;
            }
        }


        if (currentlyChosen != null) {
            panelForPlugins.addComponent(panelMap.get(currentlyChosen)).inTL(0, 0);
        }
        this.mainPanel.addComponent(panelForPlugins).inTL(0, 35);
    }

    public void resetCurrentPlugin(ButtonAPI newButton) {
        if (currentlyChosen != null) {
            this.panelForPlugins.removeComponent(panelMap.get(currentlyChosen));
        }
        currentlyChosen = newButton;
        this.panelForPlugins.addComponent(panelMap.get(currentlyChosen)).inTL(0, 0);
        playSound(currentlyChosen);
    }

    public void clearUI(boolean clearMusic) {
        customProdPlugin.clearUI(false);
        pluginMega.clearUI();
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
        customProd = buttonTooltip.addButton("Production", customProdPlugin, base, bg, Alignment.MID, CutStyle.TOP, 140, 20, 0f);
        research = buttonTooltip.addButton("Research", pluginResearch, base, bg, Alignment.MID, CutStyle.TOP, 140, 20, 0f);
        megastructures = buttonTooltip.addButton("Megastructures", pluginMega, base, bg, Alignment.MID, CutStyle.TOP, 170, 20, 0f);
//        sp = buttonTooltip.addButton("Special Projects", pluginMega, base, bg, Alignment.MID, CutStyle.TOP, 170, 20, 0f);
        customProd.setShortcut(Keyboard.KEY_Q, false);
        research.setShortcut(Keyboard.KEY_R, false);
        megastructures.setShortcut(Keyboard.KEY_T, false);
//        sp.setShortcut(Keyboard.KEY_S,false);
        customProd.getPosition().inTL(0, 0);
        research.getPosition().inTL(141, 0);
        megastructures.getPosition().inTL(282, 0);
//        sp.getPosition().inTL(453, 0);
        insertCustomProdPanel(customProd);
        insertNewResearchPanel(research);
        insertNewMegastructuresPanel(megastructures);
        megastructures.setEnabled(!GPManager.getInstance().getMegastructures().isEmpty());
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
    public void advance(float amount) {

        for (Map.Entry<ButtonAPI, CustomPanelAPI> entry : panelMap.entrySet()) {
            entry.getKey().unhighlight();
            if (entry.getKey().isChecked()) {
                entry.getKey().setChecked(false);
                if (!entry.getKey().equals(currentlyChosen)) {
                    resetCurrentPlugin(entry.getKey());
                    CoreUITracker.setMemFlagForTechTab(entry.getKey().getText().toLowerCase());
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
        if (customProdPlugin == null) {
            customProdPlugin = new NidavelirMainPanelPlugin(false, Global.getSector().getCampaignUI().getCurrentCoreTab(), null);
            customProdPlugin.init(Global.getSettings().createCustom(UIData.WIDTH, UIData.HEIGHT, customProdPlugin), null, null);
        }

        panelMap.put(tiedButton, customProdPlugin.getPanel());
    }

    private void insertNewResearchPanel(ButtonAPI tiedButton) {
        if (pluginResearch == null) {
            pluginResearch = new AoTDResearchNewPlugin(UIData.WIDTH-10, UIData.HEIGHT);
        }

        panelMap.put(tiedButton,pluginResearch.getMainPanel());
    }

    private void insertNewMegastructuresPanel(ButtonAPI tiedButton) {
        if (pluginMega == null) {
            pluginMega = new GPMegasturcutreMenu();
            pluginMega.init(Global.getSettings().createCustom(UIData.WIDTH, UIData.HEIGHT, pluginMega));
        }

        panelMap.put(tiedButton, pluginMega.getMainPanel());
    }

    public void pauseSound() {
        Global.getSoundPlayer().pauseCustomMusic();
        Global.getSoundPlayer().restartCurrentMusic();
        pausedMusic = true;
    }

    public void playSound(ButtonAPI button) {
        if (button.getText().toLowerCase().contains("production")) {
            customProdPlugin.playSound();
        }
        if (button.getText().toLowerCase().contains("research")) {
            pluginResearch.playSound();
        }
        if (button.getText().toLowerCase().contains("megastructures")) {
            pluginMega.playSound();
        }
    }

}
