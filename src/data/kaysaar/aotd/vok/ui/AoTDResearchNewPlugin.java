package data.kaysaar.aotd.vok.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import data.kaysaar.aotd.vok.scripts.SoundUIManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;
import data.kaysaar.aotd.vok.ui.research.*;

import java.util.List;

public class AoTDResearchNewPlugin implements CustomUIPanelPlugin, SoundUIManager {

    CustomPanelAPI mainPanel;
    float pos = 0;
    ResearchZoomPanel component;
    CurrentResearchShowcase showcase1;
    TechTreeModButton modButton;
    HeadOfResearchShowcase showcase2;
    public TechTreeModButton getModButton() {
        return modButton;
    }

    public CurrentResearchShowcase getCurrResearchShowcase() {
        return showcase1;
    }
    public ResearchZoomPanel getZoomPanel(){
        return component;
    }

    public AoTDResearchNewPlugin(float width, float height) {
        //Researcher : 100
        //Title : 40
        // Spacers : 10
        float heightOfOthers = 192;
        component = new ResearchZoomPanel(width, height - heightOfOthers - 5, width * 3, height * 3, 1f,this);
         modButton = new TechTreeModButton(500, 40, "aotd_vok", this,true,null);
        component.startStencil();
        component.endStencil();

        component.createTechTree("aotd_vok");
//        component.addGrid();
//        component.sentToBottomComponentsOfClass(GridRenderer.class);
        UILinesRenderer renderer = new UILinesRenderer(0f);
        mainPanel = Global.getSettings().createCustom(width, height, this);
         showcase1 = new CurrentResearchShowcase(400, 130, this);
        ScientistAPI scientistAPI = AoTDMainResearchManager.getInstance().getManagerForPlayer().currentHeadOfCouncil;
         showcase2 = new HeadOfResearchShowcase(400, 130, scientistAPI);
        mainPanel.addComponent(new ScientistButtonComponent(130, 130, scientistAPI).getPanelOfButton()).inTL((width / 2) - 65, height - 130);
        mainPanel.addComponent(modButton.getPanelOfButton()).inTL((width / 2) - 250, 0);
        mainPanel.addComponent(showcase1.getMainPanel()).inTL((width / 2) - 65 - 405, height - 130);
        mainPanel.addComponent(showcase2.getMainPanel()).inTL((width / 2) + 70, height - 130);
        mainPanel.addComponent(component.getPluginPanel()).inTL(0, 45);

    }

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public void resetCurrTechTree() {
       component.refresh();
    }
    public void resetCurrentResearch(){
        showcase1.resetUI();
    }
    public void blockButtonsFromHover() {
        for (TechTreeEra era : component.getEras()) {
            for (ResearchPanelComponent researchOptionPanel : era.getResearchOptionPanels()) {
                researchOptionPanel.setClickable(false);
            }
        }
    }
    public void unlockButtonsFromHover() {
        for (TechTreeEra era : component.getEras()) {
            for (ResearchPanelComponent researchOptionPanel : era.getResearchOptionPanels()) {
                researchOptionPanel.setClickable(true);
            }
        }
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

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
    @Override
    public void playSound() {
        Global.getSoundPlayer().playCustomMusic(1, 1, "aotd_research", true);

    }

    @Override
    public void pauseSound() {
        Global.getSoundPlayer().pauseCustomMusic();
        Global.getSoundPlayer().restartCurrentMusic();
    }
}
