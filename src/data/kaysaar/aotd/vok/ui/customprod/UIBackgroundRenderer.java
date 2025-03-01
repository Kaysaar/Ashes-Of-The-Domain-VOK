package data.kaysaar.aotd.vok.ui.customprod;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.awt.*;

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
