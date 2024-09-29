package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;

import java.awt.*;
import java.util.List;

public class UIBackgroundRenderer extends UILinesRenderer {

    public UIBackgroundRenderer(float widthPadding , Color color) {
        super(widthPadding);
        setBoxColor(color);
    }
    @Override
    public void render(float alphaMult) {


    }

    @Override
    public void renderBelow(float alphaMult) {
        for (CustomPanelAPI panel : getPanels()) {
            if (panel != null) {
                box.setSize(panel.getPosition().getWidth(), panel.getPosition().getHeight());
                box.setColor(boxColor);
                box.render(panel.getPosition().getX(), panel.getPosition().getY());
            }

        }
    }
}
