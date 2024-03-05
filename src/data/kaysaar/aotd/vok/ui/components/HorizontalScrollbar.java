package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.ui.P;
import com.fs.starfarer.util.M;
import org.apache.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class HorizontalScrollbar {
    public static final Logger log = Global.getLogger(HorizontalScrollbar.class);
    private float scrollbarX = 800f; // Initial position
    public float startingX = 0;
    private float startingY = 0;
    private float scrollbarWidth = 120f;
    public boolean isDraggingWithRightMouse = false;
    private float scrollbarHeight = 20;
    private float scrollbarYPosition = 130F;
    private boolean isDragging = false;
    private float trueWidth = 0f;
    private float startingXOfRight = -1;
    public boolean initSet = false;
    float currOffset = 0f;
    TooltipMakerAPI boundTooltip;
    CustomPanelAPI boundedPanel;
    LabelAPI labelAPI;

    public HorizontalScrollbar(float yScrollbarPosition, float xInitPostition, TooltipMakerAPI boundedTooltipPanel, float width, CustomPanelAPI panel) {
        scrollbarX = xInitPostition;
        scrollbarYPosition = yScrollbarPosition;
        boundTooltip = boundedTooltipPanel;
        trueWidth = width;
        boundedPanel = panel;
    }

    public void setPosition(float xInitPostition, float yScrollbarPosition) {
        if (!initSet) {
            startingX = xInitPostition;
            startingY = yScrollbarPosition;
            initSet = true;
        }
        scrollbarX = xInitPostition;
        scrollbarYPosition = yScrollbarPosition;
    }

    public void setScrollbarDimensions(float width, float height) {
        scrollbarWidth = width;
        scrollbarHeight = height;
    }

    public void displayScrollbar(Color scrollbarCollor, float alphamult) {
        GL11.glColor4f(scrollbarCollor.getRed() / 255f, scrollbarCollor.getGreen() / 255f, scrollbarCollor.getBlue() / 255f, alphamult); // Blue color
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(scrollbarX, scrollbarYPosition);
        GL11.glVertex2f(scrollbarX + scrollbarWidth, scrollbarYPosition);
        GL11.glVertex2f(scrollbarX + scrollbarWidth, scrollbarYPosition + scrollbarHeight);
        GL11.glVertex2f(scrollbarX, scrollbarYPosition + scrollbarHeight);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void handleMouseDragging(float panelWidth) {
        if (isDragging) {
            int x = Global.getSettings().getMouseX();// Replace with the actual method to get mouse X position in LWJGL


            scrollbarX = x - scrollbarWidth / 2;

            float begining = startingX;
            float end = begining + panelWidth;
            float endOffset = begining + panelWidth - scrollbarWidth;
            float widthOfScene = endOffset - begining;

            // Ensure scrollbarX stays within valid range
            if (scrollbarX < begining) {
                scrollbarX = begining;
            }
            if (scrollbarX > end - scrollbarWidth - scrollbarWidth) {
                scrollbarX = begining + panelWidth - scrollbarWidth - scrollbarWidth;
            }
            float currentOffset = scrollbarX - begining;
            float percent = 0;
            percent = currentOffset / panelWidth;

            if (boundTooltip != null) {
                currOffset = -(trueWidth * percent);
                boundTooltip.getExternalScroller().setXOffset(currOffset);


            }


        }
        if (!isDragging && isDraggingWithRightMouse) {
            if (startingXOfRight == -1) {
                startingXOfRight = Global.getSettings().getMouseX();
            } else {
                float currX = Global.getSettings().getMouseX();
                if (currX < startingXOfRight) {
                    float offeset = boundTooltip.getExternalScroller().getXOffset();
                    currOffset = offeset - (startingXOfRight-currX);
                    float begining = startingX;
                    float end = begining + panelWidth;
                    float endOffset = begining + panelWidth - scrollbarWidth;
                    float widthOfScene = endOffset - begining;
                    float percent = 0;
                    percent = currOffset*-1 / trueWidth;
                    scrollbarX = startingX+panelWidth*percent;

                    // Ensure scrollbarX stays within valid range
                    if (scrollbarX < begining) {
                        scrollbarX = begining;
                    }
                        boundTooltip.getExternalScroller().setXOffset(currOffset);

                    if (scrollbarX > end - scrollbarWidth - scrollbarWidth) {
                        scrollbarX = begining + panelWidth - scrollbarWidth - scrollbarWidth;
                        float percents = 0;
                        float currentOffset = scrollbarX - begining;

                        percents = currentOffset / panelWidth;
                        currOffset = -(trueWidth * percents);
                        boundTooltip.getExternalScroller().setXOffset(currOffset);
                    }

                }
                if (currX > startingXOfRight) {
                    float offeset = boundTooltip.getExternalScroller().getXOffset();
                    float begining = startingX;
                    float end = begining + panelWidth;
                    float endOffset = begining + panelWidth - scrollbarWidth;
                    float widthOfScene = endOffset - begining;
                    float percent = 0;

                    // Ensure scrollbarX stays within valid range
                    if (scrollbarX < begining) {
                        scrollbarX = begining;
                    }
                    if (scrollbarX > end - scrollbarWidth - scrollbarWidth) {
                        scrollbarX = begining + panelWidth - scrollbarWidth - scrollbarWidth;
                    }
                    currOffset = offeset + (currX-startingXOfRight);
                    percent = currOffset*-1 / trueWidth;
                    scrollbarX = startingX+panelWidth*percent;
                    if (currOffset < 0) {
                        boundTooltip.getExternalScroller().setXOffset(currOffset);
                    }
                    else{
                        currOffset = 0;
                    }
                }
                startingXOfRight = currX;
            }
        }
        if(!isDraggingWithRightMouse){
            startingXOfRight = -1;
        }

    }

    public void moveTooltip(float panelWidth, float begining) {
        if(!isDraggingWithRightMouse&&!isDragging){
            float currentOffset = scrollbarX - begining;
            float percent = 0;
            percent = currentOffset / panelWidth;

            if (boundTooltip != null) {
                currOffset = -(trueWidth * percent);
                boundTooltip.getExternalScroller().setXOffset(currOffset);


            }
        }

    }

    public void processInputForScrollbar(List<InputEventAPI> events, float panelWidth) {
        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;
            if (event.isLMBDownEvent() && !isDragging) {
                int x = Global.getSettings().getMouseX(); // Replace with the actual method to get mouse X position in LWJGL
                int y = Global.getSettings().getMouseY();
                float begining = startingX;
                float end = begining + panelWidth;
                float trueX = x - scrollbarWidth / 2;
                if (y > startingY + 10 || y < startingY - scrollbarHeight - 5) {
                    continue;
                }
                if (trueX < begining) {
                    continue;
                }
                if (scrollbarX > end - scrollbarWidth) {
                    continue;
                }
                isDragging = true;

            } else if (event.isLMBUpEvent()) {
                isDragging = false;
            }

        }
    }

    public void detectIfRightMouse() {
        if (Mouse.isButtonDown(1)) {
            if (boundedPanel != null) {
                float x = boundedPanel.getPosition().getX();
                float y = boundedPanel.getPosition().getY();
                float width = boundedPanel.getPosition().getWidth();
                float height = boundedPanel.getPosition().getHeight();
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

        }
        else{
            isDraggingWithRightMouse = false;
        }


    }

    public float getXOffset() {
        return this.scrollbarX;
    }

    public void setScrollbarAfterinit(float panelWidth, float percent) {
        scrollbarX = startingX + ((panelWidth - scrollbarWidth / 2) * percent);
    }

    public float getCurrOffset() {
        return currOffset;
    }

}
