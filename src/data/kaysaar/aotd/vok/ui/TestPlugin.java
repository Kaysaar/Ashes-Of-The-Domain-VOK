package data.kaysaar.aotd.vok.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.List;

public class TestPlugin implements CustomUIPanelPlugin {

    CustomPanelAPI mainPanel;
    ButtonAPI button;
    CustomPanelAPI pa2;
    float pos = 0;

    public TestPlugin(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        mainPanel.addComponent(Global.getSettings().createCustom(width, height, new StencilBlockerPlugin(mainPanel)));
        CustomPanelAPI panelToWorkWith = mainPanel.createCustomPanel(width, height, null);
        createUI(panelToWorkWith);
        mainPanel.addComponent(panelToWorkWith);
        mainPanel.addComponent(Global.getSettings().createCustom(width, height, new StencilBlockerEndPlugin()));

    }

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public void createUI(CustomPanelAPI mainPanel) {
        pa2 = mainPanel.createCustomPanel(1, 1, null);
        TooltipMakerAPI tooltipMakerAPI = pa2.createUIElement(mainPanel.getPosition().getWidth() + 7900, mainPanel.getPosition().getHeight(), false);
        button = tooltipMakerAPI.addButton("test", null, 1300, 100, 0f);
        pa2.addUIElement(tooltipMakerAPI);
        mainPanel.addComponent(pa2);
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

        if (pa2 != null&&pa2.getPosition()!=null) {
            pa2.getPosition().setXAlignOffset(pos);
            pos--;
        }

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
