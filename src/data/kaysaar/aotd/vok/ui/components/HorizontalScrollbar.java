package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class HorizontalScrollbar {
    private float scrollbarX = 800f; // Initial position
    private float startingX =0;
    private float startingY= 0;
    private float scrollbarWidth = 120f;
    private float scrollbarHeight = 20;
    private float scrollbarYPosition = 130F;
    private boolean isDragging = false;
    private float trueWidth = 0f;
    public boolean initSet = false;
    float currOffset = 0f;
    TooltipMakerAPI  boundTooltip;

    LabelAPI labelAPI;
    public HorizontalScrollbar(float yScrollbarPosition, float xInitPostition, TooltipMakerAPI boundedTooltipPanel,float width){
        scrollbarX = xInitPostition;
        scrollbarYPosition = yScrollbarPosition;
        boundTooltip = boundedTooltipPanel;
        trueWidth = width;
    }
    public void setPosition(float xInitPostition, float yScrollbarPosition){
        if(!initSet){
            startingX = xInitPostition;
            startingY = yScrollbarPosition;
            initSet = true;
        }
        scrollbarX = xInitPostition;
        scrollbarYPosition = yScrollbarPosition;
    }
    public void  setScrollbarDimensions(float width, float height){
        scrollbarWidth = width;
        scrollbarHeight = height;
    }
    public void  displayScrollbar(Color scrollbarCollor, float alphamult){
        GL11.glColor4f(scrollbarCollor.getRed() / 255f, scrollbarCollor.getGreen() / 255f, scrollbarCollor.getBlue() / 255f,alphamult); // Blue color
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
            int x = Mouse.getX(); // Replace with the actual method to get mouse X position in LWJGL

            scrollbarX = x-scrollbarWidth/2;

            float begining = startingX;
            float end = begining+panelWidth;
            float endOffset = begining+panelWidth-scrollbarWidth;
            float widthOfScene = endOffset - begining;

            // Ensure scrollbarX stays within valid range
            if (scrollbarX < begining) {
                scrollbarX = begining;
            }
            if(scrollbarX>end-scrollbarWidth-scrollbarWidth){
                scrollbarX = begining+panelWidth-scrollbarWidth-scrollbarWidth;
            }
            float currentOffset = scrollbarX-begining;
            float percent = 0;
            percent = currentOffset/panelWidth;

            if(boundTooltip!=null){
                float tooltipWidth = trueWidth-50;
                currOffset = -(trueWidth*percent);
               boundTooltip.getExternalScroller().setXOffset(currOffset);



            }



        }
    }

    public void processInputForScrollbar(List<InputEventAPI> events,float panelWidth) {
        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;
            if (event.isMouseDownEvent()&&!isDragging) {
                int x = Mouse.getX(); // Replace with the actual method to get mouse X position in LWJGL
                int y = Mouse.getY();
                float begining = startingX;
                float end = begining+panelWidth;
                float trueX= x-scrollbarWidth/2;
                if(y>startingY+10||y<startingY-scrollbarHeight-5){
                 continue;
                }
                if (trueX < begining) {
                    continue;
                }
                if(scrollbarX>end-scrollbarWidth){
                   continue;
                }
                isDragging = true;

            }
            else if (event.isMouseUpEvent()) {
                isDragging = false;
            }
        }
    }
    public float getXOffset(){
        return this.scrollbarX;
    }
    public void  setScrollbarAfterinit(float panelWidth, float percent){
        scrollbarX = startingX+((panelWidth-scrollbarWidth/2)*percent);
    }

    public float getCurrOffset() {
        return currOffset;
    }
}
