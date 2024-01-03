package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

public class UiPanel implements AoTDUiComp {
    public CustomPanelAPI mainPanel;
    public CustomPanelAPI panel;
    public TooltipMakerAPI tooltip;

    public void init(CustomPanelAPI mainPanel, CustomPanelAPI panelAPI, TooltipMakerAPI tooltipMakerAPI) {
        this.mainPanel = mainPanel;
        panel = panelAPI;
        tooltip = tooltipMakerAPI;
    }

    public void createUI() {

    }

    @Override
    public void createUI(float x, float y) {

    }

    public void placeTooltip(float x, float y) {
        panel.addUIElement(tooltip).inTL(x, y);
    }

    public void placeSubPanel(float x, float y) {
        mainPanel.addComponent(panel).inTL(x, y);
    }

    @Override
    public void render(Color colorOfRender, float alphamult) {

    }
}
