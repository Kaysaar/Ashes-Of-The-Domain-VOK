package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

public class RightMouseTooltipMover implements CustomUIPanelPlugin {

    TooltipMakerAPI boundTooltip;
    CustomPanelAPI panelOfTooltip;
    float width;
    float height;
    public float startingX = 0;
    private float startingY = 0;
    public boolean isDraggingWithRightMouse = false;
    private float trueWidth = 0f;
    float prevX = -1f;
    private float startingXOfRight = -1;
    float currOffset = 0f;
    float initalOffset = 0f;

    float leftBorderX;
    float rightBorderX;


    public void setBorders(float left, float right){
        leftBorderX = left;
        rightBorderX =right;
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    public void init(CustomPanelAPI panelAPI, TooltipMakerAPI tooltipOfMovement) {
        boundTooltip = tooltipOfMovement;
        panelOfTooltip = panelAPI;
        leftBorderX = -panelOfTooltip.getPosition().getWidth()+20;
        rightBorderX = panelOfTooltip.getPosition().getWidth()-20;
        currOffset = leftBorderX;

    }




    @Override
    public void renderBelow(float alphaMult) {

    }



    @Override
    public void render(float alphaMult) {


    }

    @Override
    public void advance(float amount) {
        if (panelOfTooltip != null) {
            detectIfRightMouse();
            handleMouseDragging();
            boundTooltip.getExternalScroller().setXOffset(currOffset);
        }


    }




    @Override
    public void processInput(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {

        }
    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    public void handleMouseDragging() {
        if (isDraggingWithRightMouse) {
            if (startingXOfRight == -1) {
                startingXOfRight = Global.getSettings().getMouseX();
            } else {

                float currX = Global.getSettings().getMouseX();
                float offeset = currOffset;
                currOffset = offeset - (startingXOfRight - currX);
                if (checkIfPanelIsGoingTooFar(currOffset)) {
                    currOffset = pickNearest(currOffset);
                }
                initalOffset = currOffset;
                startingXOfRight = currX;
            }

        } else {
            startingXOfRight = -1;
        }

    }

    public void detectIfRightMouse() {
        if (Mouse.isButtonDown(1)) {
            if (panelOfTooltip != null) {
                float x = panelOfTooltip.getPosition().getX();
                float y = panelOfTooltip.getPosition().getY();
                float width = panelOfTooltip.getPosition().getWidth();
                float height = panelOfTooltip.getPosition().getHeight();
                float xMouse = Global.getSettings().getMouseX();
                float yMouse = Global.getSettings().getMouseY();
                if (xMouse > x && xMouse < x + width) {
                    if (yMouse > y && yMouse < y + height) {
                        isDraggingWithRightMouse = true;
                    } else {
                        isDraggingWithRightMouse = false;
                        startingXOfRight = -1;
                    }
                } else {
                    isDraggingWithRightMouse = false;
                    startingXOfRight = -1;

                }
            }

        } else {
            isDraggingWithRightMouse = false;
        }


    }

    public boolean checkIfPanelIsGoingTooFar(float targetedOffset) {
        return (targetedOffset >=rightBorderX  || targetedOffset <=leftBorderX )||(targetedOffset>=panelOfTooltip.getPosition().getWidth()-40||targetedOffset<=-panelOfTooltip.getPosition().getWidth()+40);
    }

    public float pickNearest(float targetOffset) {
        if (targetOffset > 0) {
            if(rightBorderX>panelOfTooltip.getPosition().getWidth()-20){
                return panelOfTooltip.getPosition().getWidth()-20;
            }
            return rightBorderX;
        }
        return leftBorderX;
    }
}
