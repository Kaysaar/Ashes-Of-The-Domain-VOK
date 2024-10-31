package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UILinesRenderer implements CustomUIPanelPlugin {
    ArrayList<CustomPanelAPI> panels = new ArrayList<>();
    public SpriteAPI box = Global.getSettings().getSprite("rendering","GlitchSquare");
     public Color boxColor = Misc.getDarkPlayerColor();
    Color progression = Misc.getBrightPlayerColor();
    public void setPanels(ArrayList<CustomPanelAPI> panels) {
        this.panels = panels;
    }
    public void setPanel(CustomPanelAPI panel) {
        panels.add(panel);
    }
    float widthPadding = 10f;
    public ArrayList<CustomPanelAPI> getPanels() {
        return panels;
    }
    boolean renderProgress = false;
    float progress = 0f;
    public void enableProgressMode(float currProgress){
        renderProgress = true;
        progress = currProgress;
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }
    public UILinesRenderer(float widthPadding){
        this.widthPadding = widthPadding;
    }
    public void setBoxColor(Color boxColor) {
        this.boxColor = boxColor;
    }

    @Override
    public void renderBelow(float alphaMult) {



    }

    @Override
    public void render(float alphaMult) {
        if(renderProgress){
            for (CustomPanelAPI panel : panels) {
                if (panel != null) {
                    box.setSize((panel.getPosition().getWidth()+widthPadding)*progress,panel.getPosition().getHeight());
                    box.setColor(progression);
                    box.render(panel.getPosition().getX(),panel.getPosition().getY());
                }
            }
        }
        for (CustomPanelAPI panel : panels) {
            if (panel != null) {
                box.setSize(panel.getPosition().getWidth()+widthPadding,1);
                box.setColor(boxColor);
                box.setAlphaMult(alphaMult);
                box.render(panel.getPosition().getX(),panel.getPosition().getY());
                box.render(panel.getPosition().getX(),panel.getPosition().getY()+panel.getPosition().getHeight());
                box.setSize(1,panel.getPosition().getHeight());
                box.render(panel.getPosition().getX(),panel.getPosition().getY());
                box.render(panel.getPosition().getX()+panel.getPosition().getWidth()+widthPadding,panel.getPosition().getY());
            }
        }
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
}
