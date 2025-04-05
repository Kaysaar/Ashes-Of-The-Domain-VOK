package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class SpecialProjectStageWindow implements CustomUIPanelPlugin {
    ButtonAPI buttonOfStage;
    CustomPanelAPI panelOfStage;
    CustomPanelAPI panelInfoOfStage;

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    public void drawLineFromObject(UIComponentAPI componentToDrawFrom) {

    }
//    public void drawLine(Color red, Color green, Color blue, Vector2f start, Vector2f end) {
//        GL11.glColor3f(red.getRed()/255f, green, blue);  // Set the line color
//        GL11.glBegin(GL11.GL_LINES);       // Start drawing lines
//        GL11.glVertex2f(start.x, start.y);   // Specify the first point
//        GL11.glVertex2f(endX, endY);       // Specify the second point
//        GL11.glEnd();                      // End drawing
//    }
}
