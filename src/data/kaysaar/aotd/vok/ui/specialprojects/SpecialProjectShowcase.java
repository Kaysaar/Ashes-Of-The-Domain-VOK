package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.customprod.components.RightMouseTooltipMover;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialProjectShowcase implements CustomUIPanelPlugin {
    ArrayList<SpecialProjectStageWindow> stagesWindow = new ArrayList<>();
    CustomPanelAPI mainPanel;
    UILinesRenderer renderer;
    CustomPanelAPI objectOfInterest;
    public static float widthExpected = 1220;

    //effective range must be 1120;
    public SpecialProjectShowcase(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        CustomPanelAPI test = mainPanel.createCustomPanel(width,height-30,null);

        mainPanel.addComponent(test).inTL(0,20);
        renderer = new UILinesRenderer(0f);
        renderer.setBoxColor(Color.ORANGE);
        renderer.setPanel(mainPanel);
        SpecialProjectStageWindow window = new SpecialProjectStageWindow();
        stagesWindow.add(window);




    }


    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {
        for (SpecialProjectStageWindow window : stagesWindow) {
            window.drawLineFromObject(objectOfInterest);
        }
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
}
