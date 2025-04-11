package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.scripts.misc.TrapezoidButtonDetector;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

public class HorizontalMoverData {
    public float currentOffsetX;
    public float currentOffsetY;
    public static final Logger log = Global.getLogger(HorizontalMoverData.class);
    public float maxOffsetX, maxOffsetY;
    public boolean isDraggingRightMouse = false;
    public float scale = 0f;

    public Vector2f mouseCordsBeforeDrag = new Vector2f(-1, -1);
    public TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
    public CustomPanelAPI mainPanel;

    public HorizontalMoverData(CustomPanelAPI mainPanel) {
        this.mainPanel = mainPanel;
    }

    public float getCurrentOffsetX() {
        return currentOffsetX ;
    }

    public float getCurrentOffsetY() {
        return currentOffsetY;

    }

    public void setCurrentOffsetX(float currentOffsetX) {
        this.currentOffsetX = Math.max(-getMaxOffsetX(), Math.min(currentOffsetX,0));
    }

    public void setCurrentOffsetY(float currentOffsetY) {
        this.currentOffsetY = Math.max(0, Math.min(currentOffsetY,getMaxOffsetY()));

    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getMaxOffsetX() {

        return maxOffsetX-(mainPanel.getPosition().getWidth()/scale);
    }

    public float getMaxOffsetY() {
        return maxOffsetY-(mainPanel.getPosition().getHeight()/scale);
    }

    public void processEvents(List<InputEventAPI> eventList) {
        for (InputEventAPI input : eventList) {
            if (input.isConsumed()) continue;

            if (input.isRMBDownEvent()) {  // Right Mouse Button Pressed
                if (mouseCordsBeforeDrag.x == -1) {
                    mouseCordsBeforeDrag.set(Global.getSettings().getMouseX(), Global.getSettings().getMouseY());
                }
                isDraggingRightMouse = true;
                log.info("Right mouse button down - starting drag");
            }

            if (input.isRMBUpEvent()) {  // Right Mouse Button Released
                isDraggingRightMouse = false;
                mouseCordsBeforeDrag.set(-1, -1);
                log.info("Right mouse button up - stopping drag");
            }
        }
    }

    public boolean mouseWithinPanel() {

        float currX = Global.getSettings().getMouseX();
        float currY = Global.getSettings().getMouseY();

        float xLeft = mainPanel.getPosition().getX();
        float xRight = xLeft + mainPanel.getPosition().getWidth();
        float yBot = mainPanel.getPosition().getY();
        float yTop = yBot + mainPanel.getPosition().getHeight();

        return (detector.determineIfHoversOverButton(xLeft, yTop, xRight, yTop, xLeft, yBot, xRight, yBot, currX, currY));

    }

    public void handleDragging() {
        if (!isDraggingRightMouse) return;  // Exit early if not dragging

        float currX = Global.getSettings().getMouseX();
        float currY = Global.getSettings().getMouseY();

        // Ensure we're dragging inside the panel bounds
        float xLeft = mainPanel.getPosition().getX();
        float xRight = xLeft + mainPanel.getPosition().getWidth();
        float yBot = mainPanel.getPosition().getY();
        float yTop = yBot + mainPanel.getPosition().getHeight();

        if (!detector.determineIfHoversOverButton(xLeft, yTop, xRight, yTop, xLeft, yBot, xRight, yBot, currX, currY)) {
            isDraggingRightMouse = false;
            mouseCordsBeforeDrag.set(-1, -1);
            return;
        }

        float diffX = (currX - mouseCordsBeforeDrag.x)/scale;
        float diffY = (currY - mouseCordsBeforeDrag.y)/scale;


        if (diffX != 0 || diffY != 0) {
            mouseCordsBeforeDrag.set(currX, currY); // Update only if there was movement

            // Log movement detection
            if (diffX != 0) log.info("Dragging detected in X direction");
            if (diffY != 0) log.info("Dragging detected in Y direction");

            // Update offsets with bounds checking
            currentOffsetX = Math.max(-getMaxOffsetX(), Math.min(currentOffsetX + diffX,0));
            currentOffsetY = Math.max(0, Math.min(currentOffsetY + diffY,getMaxOffsetY()));
        }
    }


}
