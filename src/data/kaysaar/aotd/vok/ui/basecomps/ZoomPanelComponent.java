package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZoomPanelComponent implements CustomUIPanelPlugin {

    public ArrayList<ResizableComponent> resizableComponents = new ArrayList<>();
    public HorizontalMoverData data;
    public CustomPanelAPI mainPanel;
    public float currScale = 1f;
    public float width, height;
    public  UILinesRenderer renderer = new UILinesRenderer(-1f);
    public float posX = 0;
    public float poxY = 0;
    public CustomPanelAPI mainPanelBlockerPlugin;
    public float maxScale = 1f;
    public float minScale = 0.1f;
    float trueWidth, trueHeight;

    public float getTrueHeight() {
        return trueHeight;
    }

    public float getTrueWidth() {
        return trueWidth;
    }

    // --- Zoom inertia fields ---
    // This variable accumulates velocity from scroll events.
    private float zoomVelocity = 0f;
    // The last recorded mouse position (relative to the panel) for zoom centering.
    private Vector2f inertiaMousePos = null;

    public CustomPanelAPI getPluginPanel() {
        return mainPanel;
    }

    public HorizontalMoverData getData() {
        return data;
    }

    public ZoomPanelComponent(float width, float height, float trueWidth, float trueHeight, float startingZoom) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        this.width = width;
        this.height = height;
        minScale = width / trueWidth;
        renderer.setPanel(mainPanel);
        resizableComponents = new ArrayList<>();
        data = new HorizontalMoverData(mainPanel);
        this.currScale = startingZoom;
        posX = data.getCurrentOffsetX();
        poxY = data.getCurrentOffsetY();
        this.trueWidth = trueWidth;
        this.trueHeight = trueHeight;
        setMaxOffsets((int) trueWidth, (int) trueHeight, startingZoom);
    }

    public void startStencil() {
        mainPanel.addComponent(Global.getSettings().createCustom(width, height, new StencilBlockerPlugin(mainPanel)));

    }

    public void endStencil() {
        mainPanelBlockerPlugin = Global.getSettings().createCustom(width, height, new StencilBlockerEndPlugin());
        mainPanel.addComponent(mainPanelBlockerPlugin);
    }
    public void addGrid() {
        GridRenderer renderer1=new GridRenderer(data.maxOffsetX, data.maxOffsetY,500, Color.cyan,data);
        addComponent(renderer1,0,0);
    }
    public void addComponent(ResizableComponent resizableComponent, float x, float y) {
        resizableComponents.add(resizableComponent);
        resizableComponent.setCoords(mainPanel, x, y);
        resizableComponent.setAbsolutePanel(mainPanel);
        mainPanel.removeComponent(mainPanelBlockerPlugin);
        mainPanel.addComponent(resizableComponent.componentPanel).inTL(x, y);
        mainPanel.addComponent(mainPanelBlockerPlugin);
    }
    public void removeComponentOfClass(Class componentClass){
        Iterator<ResizableComponent> iterator = resizableComponents.iterator();
        while(iterator.hasNext()){
            ResizableComponent resizableComponent = iterator.next();
            if(resizableComponent.getClass().equals(componentClass)){
                resizableComponent.clearUI();
                getPluginPanel().removeComponent(resizableComponent.componentPanel);
                iterator.remove();
            }
        }
    }
    public void sentToBottomComponentsOfClass(Class componentClass) {
        mainPanel.removeComponent(mainPanelBlockerPlugin);
        ArrayList<ResizableComponent> componentsToReAddLater = new ArrayList<>();
        for (ResizableComponent resizableComponent : resizableComponents) {
            if (!resizableComponent.getClass().equals(componentClass)) {
                componentsToReAddLater.add(resizableComponent);
            }
            getPluginPanel().removeComponent(resizableComponent.componentPanel);
        }

        for (ResizableComponent resizableComponent : resizableComponents) {
            if (resizableComponent.getClass().equals(componentClass)) {
                getPluginPanel().addComponent(resizableComponent.componentPanel);
            }

        }
        for (ResizableComponent resizableComponent : componentsToReAddLater) {
            getPluginPanel().addComponent(resizableComponent.componentPanel);

        }
        componentsToReAddLater.clear();
        mainPanel.addComponent(mainPanelBlockerPlugin);
    }

    public void setMaxOffsets(int x, int y, float scale) {
        data.maxOffsetY = y;
        data.maxOffsetX = x;
        this.minScale = width/x;
        if(height/y>=minScale){
            this.minScale = height/y;
        }
        data.scale = scale;
    }

    @Override
    public void positionChanged(PositionAPI position) {
        // No-op.
    }

    @Override
    public void renderBelow(float alphaMult) {

        renderer.render(alphaMult);
    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {
        // Let the default dragging behavior handle panning.
        if (data != null) {
            data.handleDragging();
        }

        // Apply zoom inertia gradually.
        if (Math.abs(zoomVelocity) > 0.001f && inertiaMousePos != null) {
            // Get world coordinates at the inertia point before applying zoom.
            Vector2f worldPoint = calculateWorldCoords(inertiaMousePos.x, inertiaMousePos.y);

            // Calculate an effective zoom factor based on the current zoom velocity and elapsed time.
            float effectiveZoomFactor = 1 + zoomVelocity * amount;
            setCurrScale(currScale * effectiveZoomFactor);
            data.scale = currScale;

            // Recalculate world coordinates after zoom update.
            Vector2f worldPointAfterChange = calculateWorldCoords(inertiaMousePos.x, inertiaMousePos.y);
            float diffX = worldPointAfterChange.x - worldPoint.x;
            float diffY = worldPointAfterChange.y - worldPoint.y;
            data.setCurrentOffsetX(data.currentOffsetX + diffX);
            data.setCurrentOffsetY(data.currentOffsetY - diffY);

            // Decay the zoom velocity over time.
            zoomVelocity *= 0.85f;
            if (Math.abs(zoomVelocity) < 0.001f) {
                zoomVelocity = 0f;
            }
        }

        // Update all child components with the latest offsets and scale.
        for (ResizableComponent resizableComponent : resizableComponents) {
            resizableComponent.movingCords.set(-data.getCurrentOffsetX()*currScale, -data.getCurrentOffsetY()*currScale);
            resizableComponent.resize(currScale);
        }
    }

    public void setCurrScale(float currScale) {
        this.currScale = currScale;
        if (this.currScale >= maxScale) {
            this.currScale = maxScale;
        }
        if (this.currScale <= minScale) {
            this.currScale = minScale;
        }
        this.data.setScale(this.currScale);
    }

    public boolean blockComponent = false;

    @Override
    public void processInput(List<InputEventAPI> eventList) {
        if (data != null && !blockComponent) {
            data.processEvents(eventList);
            // Process only scroll events to affect zoom inertia.
            for (InputEventAPI event : eventList) {
                if (event.isConsumed()) continue;
                if (event.isMouseScrollEvent() && data.mouseWithinPanel()) {
                    int value = event.getEventValue();

                    // Determine the mouse position relative to the panel.
                    PositionAPI panelPos = mainPanel.getPosition();
                    float panelX = panelPos.getX();
                    float panelYTop = convertYBLtoYTL(panelPos.getY() + panelPos.getHeight());
                    float mouseX = Global.getSettings().getMouseX() - panelX;
                    float mouseY = convertYBLtoYTL(Global.getSettings().getMouseY()) - panelYTop;

                    // Instead of immediately updating the zoom, add to the zoom velocity.
                    zoomVelocity += (value > 0 ? 1.50f : -1.50f);
                    // Record the current mouse position for inertia centering.
                    inertiaMousePos = new Vector2f(mouseX, mouseY);

                    event.consume();
                }
            }
        }
    }
    public float getMouseX(){
        PositionAPI panelPos = mainPanel.getPosition();
        float panelX = panelPos.getX();
        return Global.getSettings().getMouseX() - panelX;
    }
    public float getMouseY(){
        PositionAPI panelPos = mainPanel.getPosition();
        float panelYTop = convertYBLtoYTL(panelPos.getY() + panelPos.getHeight());
        return  convertYBLtoYTL(Global.getSettings().getMouseY()) - panelYTop;
    }

    public Vector2f calculateWorldCoords(float mouseX, float mouseY) {
        float exactLookingAtX = mouseX - (data.getCurrentOffsetX()*currScale);
        float exactLookingAtY = mouseY + (data.getCurrentOffsetY()*currScale);
        float originalCordX = exactLookingAtX / data.scale;
        float originalCordY = exactLookingAtY / data.scale;
        return new Vector2f(originalCordX, originalCordY);
    }

    public Vector2f calculateUICords(float worldX, float worldY) {
        float uiX = worldX * data.scale + (data.getCurrentOffsetX()*currScale);
        float uiY = worldY * data.scale - (data.getCurrentOffsetY()*currScale);
        return new Vector2f(uiX, uiY);
    }

    private float convertYBLtoYTL(float screenY) {
        return Global.getSettings().getScreenHeight() - screenY;
    }

    @Override
    public void buttonPressed(Object buttonId) {
        // No-op.
    }
}
