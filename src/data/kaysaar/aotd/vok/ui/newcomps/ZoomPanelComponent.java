    package data.kaysaar.aotd.vok.ui.newcomps;

    import com.fs.starfarer.api.Global;
    import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
    import com.fs.starfarer.api.input.InputEventAPI;
    import com.fs.starfarer.api.ui.CustomPanelAPI;
    import com.fs.starfarer.api.ui.PositionAPI;
    import com.fs.starfarer.ui.P;
    import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
    import data.kaysaar.aotd.vok.ui.StencilBlockerPlugin;
    import org.lwjgl.util.vector.Vector2f;

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
        float minScale = 0.2f;
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
                        float zoomFactor = value > 0 ? 1.1f : 0.9f;
                        float oldscale = currScale;
                        float newScale = Math.min(maxScale, Math.max(minScale, currScale * zoomFactor));

                        // Get mouse position relative to the panel
                        // Convert the panel's Y position from bottom–left to top–left
                        PositionAPI panelPos = mainPanel.getPosition();
                        float panelX = panelPos.getX();
                        float panelYTop = convertYBLtoYTL(panelPos.getY() + panelPos.getHeight());
                        float mouseX = Global.getSettings().getMouseX() - panelX;
                        float mouseY = convertYBLtoYTL(Global.getSettings().getMouseY()) - panelYTop;
//                        float mouseX = panelX+(panelPos.getWidth()/2);
//                        float mouseY = panelYTop+(panelPos.getHeight()/2);
                        // 1. Convert the mouse's UI position to world coordinates (using current offset/scale)
                        Vector2f worldPoint = calculateWorldCoords(mouseX, mouseY);

                        // 2. Update the scale (zoom)
                        setCurrScale(newScale);
                        data.scale = currScale;
                        Vector2f worldPointAfterChange = calculateWorldCoords(mouseX, mouseY);
                        float diffX = worldPointAfterChange.x- worldPoint.x;
                        float diffY = worldPointAfterChange.y- worldPoint.y;

                        data.setCurrentOffsetX( data.currentOffsetX+=diffX);
                        data.setCurrentOffsetY( data.currentOffsetY-=diffY);


                        event.consume();
                    }
                }
            }
        }

        public Vector2f calculateWorldCoords(float mouseX, float mouseY) {
             float exactLookingAtX = mouseX- data.getCurrentOffsetX();
            float exactLookingAtY = mouseY+ data.getCurrentOffsetY();

            float originalCordX = exactLookingAtX/data.scale;
            float originalCordY = exactLookingAtY/data.scale;


            return new Vector2f(originalCordX,originalCordY);
        }
        public Vector2f calculateUICords(float worldX, float worldY) {
            float uiX = worldX * data.scale + data.getCurrentOffsetX();
            float uiY = worldY * data.scale - data.getCurrentOffsetY();
            return new Vector2f(uiX, uiY);
        }




        private float convertYBLtoYTL(float screenY) {
            return Global.getSettings().getScreenHeight() - screenY;
        }


        @Override
        public void buttonPressed(Object buttonId) {

        }
    }
