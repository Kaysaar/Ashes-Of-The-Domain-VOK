package data.kaysaar_aotd_vok.ui;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Pair;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorizontalScrollbar {
    private float scrollbarX = 800f; // Initial position
    private float startingX =0;
    private float startingY= 0;
    private float scrollbarWidth = 120f;
    private float scrollbarHeight = 20;
    private float scrollbarYPosition = 130F;
    private boolean isDragging = false;
    private float trueWidth = 0f;

    TooltipMakerAPI  boundTooltip;
    public HashMap<CustomPanelAPI, Pair<Float,Float>> panelsList = new HashMap<>();
    LabelAPI labelAPI;
    public HorizontalScrollbar(float yScrollbarPosition, float xInitPostition, TooltipMakerAPI boundedTooltipPanel,float width){
        scrollbarX = xInitPostition;
        scrollbarYPosition = yScrollbarPosition;
        boundTooltip = boundedTooltipPanel;
        trueWidth = width;
    }
    public void setPosition(float xInitPostition, float yScrollbarPosition){
        scrollbarX = xInitPostition;
        startingX = xInitPostition;
        startingY = yScrollbarPosition;
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
            if(scrollbarX>end-scrollbarWidth){
                scrollbarX = begining+panelWidth-scrollbarWidth;
            }
            float currentOffset = scrollbarX-begining;
            float percent = 0;
            if(currentOffset==0){
                float tooltipWidth = trueWidth;
                for (Map.Entry<CustomPanelAPI, Pair<Float, Float>> customPanelAPIPairEntry : panelsList.entrySet()) {
                    boundTooltip.removeComponent(customPanelAPIPairEntry.getKey());
                    boundTooltip.addCustom( customPanelAPIPairEntry.getKey(),10f).getPosition().setLocation(0,0).inTL(customPanelAPIPairEntry.getValue().one,customPanelAPIPairEntry.getValue().two);
                }
            }
            else {
                percent = currentOffset/endOffset;
            }
            if(boundTooltip!=null){
                float tooltipWidth = trueWidth;
                for (Map.Entry<CustomPanelAPI, Pair<Float, Float>> customPanelAPIPairEntry : panelsList.entrySet()) {
                    boundTooltip.removeComponent(customPanelAPIPairEntry.getKey());
                    boundTooltip.addCustom( customPanelAPIPairEntry.getKey(),10f).getPosition().setLocation(0,0).inTL(customPanelAPIPairEntry.getValue().one-(tooltipWidth*percent),customPanelAPIPairEntry.getValue().two);
                }



            }



        }
    }
    public void addPanel(CustomPanelAPI subPanel,float xpad, float ypad){
        panelsList.put(subPanel,new Pair<>(xpad,ypad));
    }
    public void processInputForScrollbar(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;
            if (event.isMouseDownEvent()) {
                int x = event.getX();;
                int y = event.getY();
                isDragging = true;

                event.consume();
            } else if (event.isMouseUpEvent()) {
                event.consume();
                isDragging = false;
            }
        }
    }
}
