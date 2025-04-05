package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.campaign.fleet.NoFuelDriftScript;
import data.kaysaar.aotd.vok.scripts.SoundUIManager;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.util.List;

public class SpecialProjectUIManager implements CustomUIPanelPlugin , SoundUIManager {
    UILinesRenderer renderer;
    CustomPanelAPI mainPanel;
    SpecialProjectListManager listManager;
    SpecialProjectShowcase currProjectShowcase;
    public CustomPanelAPI getMainPanel() {
        return mainPanel;

    }

    public SpecialProjectUIManager(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width,height,this);
        listManager = new SpecialProjectListManager(width*0.30f,height);
        currProjectShowcase = new SpecialProjectShowcase(width*0.7f-10,height);
        mainPanel.addComponent(listManager.mainPanel).inTL(0,0);
        mainPanel.addComponent(currProjectShowcase.mainPanel).inTL(width*0.30f+10f,0);
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);

    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        renderer.render(alphaMult);
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

    }

    @Override
    public void pauseSound() {

    }
}
