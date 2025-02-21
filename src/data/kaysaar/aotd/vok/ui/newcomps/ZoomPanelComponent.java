package data.kaysaar.aotd.vok.ui.newcomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.ui.P;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.ui.StencilBlockerPlugin;

import java.util.ArrayList;
import java.util.List;

public class ZoomPanelComponent implements CustomUIPanelPlugin {

    ArrayList<ResizableComponent> resizableComponents = new ArrayList<>();
    HorizontalMoverData data;
    CustomPanelAPI mainPanel;
    float currScale = 1f;
    float width, height;
    UILinesRenderer renderer = new UILinesRenderer(-1f);
    float posX = 0;
    float poxY = 0;
    CustomPanelAPI mainPanelBlockerPlugin;
    float maxScale =1f;
    float minScale = 0.3f;
    public CustomPanelAPI getPluginPanel() {
        return mainPanel;
    }

    public ZoomPanelComponent(float width, float height, float trueWidth, float trueHeight) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        mainPanel.addComponent(Global.getSettings().createCustom(width, height, new StencilBlockerPlugin(mainPanel)));
        this.width = width;
        this.height = height;
        renderer.setPanel(mainPanel);
        mainPanelBlockerPlugin = Global.getSettings().createCustom(width, height, new StencilBlockerPlugin(mainPanel));

        mainPanel.addComponent(mainPanelBlockerPlugin);
        data = new HorizontalMoverData(mainPanel);
        posX = data.getCurrentOffsetX();
        poxY = data.getCurrentOffsetY();
        setMaxOffsets((int) trueWidth, (int) trueHeight, 1f);
    }

    public void addComponent(ResizableComponent resizableComponent, float x, float y) {
        resizableComponents.add(resizableComponent);
        resizableComponent.setAbsoluteBoundry(mainPanel, x, y);
        mainPanel.addComponent(resizableComponent.componentPanel).inTL(x, y);
    }

    public void setMaxOffsets(int x, int y, float scale) {
        data.maxOffsetY = y;
        data.maxOffsetX = x;
        data.scale = scale;

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
        if (data != null) {
            data.handleDragging();
            for (ResizableComponent resizableComponent : resizableComponents) {
                resizableComponent.movingCords.set(-data.getCurrentOffsetX(), -data.getCurrentOffsetY());
                resizableComponent.resize(currScale);
            }

        }

        mainPanel.bringComponentToTop(mainPanelBlockerPlugin);
    }

    public void setCurrScale(float currScale) {
        this.currScale = currScale;
        if( this.currScale>=maxScale){
            this.currScale = maxScale;
        }
        if( this.currScale<=minScale){
            this.currScale = minScale;
        }
    }

    @Override
    public void processInput(List<InputEventAPI> eventList) {
        if (data != null) {
            data.processEvents(eventList);
            for (InputEventAPI event : eventList) {
                if (event.isConsumed()) continue;
                if (event.isMouseScrollEvent() && data.mouseWithinPanel()) {
                    int value = event.getEventValue();
                    float oldScale = currScale;
                    float zoomFactor = value > 0 ? 1.1f : 0.9f;
                    float newScale = Math.min(maxScale, Math.max(minScale, currScale * zoomFactor));

                    // Get mouse position relative to panel
                    PositionAPI panelPos = mainPanel.getPosition();
                    float mouseX = Global.getSettings().getMouseX() - panelPos.getX();
                    float mouseY = Global.getSettings().getMouseY() - panelPos.getY();

                    // Calculate new offsets to keep content under mouse stable
                    float ratioX = mouseX / (panelPos.getWidth() * oldScale);
                    float ratioY = mouseY / (panelPos.getHeight() * oldScale);

                    float newOffsetX = (data.currentOffsetX + mouseX) * (oldScale / newScale) - mouseX;
                    float newOffsetY = (data.currentOffsetY + mouseY) * (oldScale / newScale) - mouseY;

                    // Apply changes
                    currScale = newScale;
                    data.currentOffsetX = Math.max(-data.getMaxOffsetX(), Math.min(newOffsetX, 0));
                    data.currentOffsetY = Math.max(-data.getMaxOffsetY(), Math.min(newOffsetY, 0));
                    data.scale = newScale;
                    event.consume();
                }
            }
        }
    }







    @Override
    public void buttonPressed(Object buttonId) {

    }
}
