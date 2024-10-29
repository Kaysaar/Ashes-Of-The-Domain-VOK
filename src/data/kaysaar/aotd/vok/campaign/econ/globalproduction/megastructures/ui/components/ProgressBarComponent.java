package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.graphics.Sprite;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;
import java.util.List;

public class ProgressBarComponent implements CustomUIPanelPlugin {
    public int sections;
    public int currentSection;
    CustomPanelAPI renderingPanel;
    public float progress;
    public boolean isInteractiveBar = false;
    boolean haveMovedToAnotherSegment = true;
    Color progressionColor;
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering","GlitchSquare");
    transient SpriteAPI spriteForButtonGlow = Global.getSettings().getSprite("ui","aotd_text_glow");
    transient SpriteAPI arrows = Global.getSettings().getSprite("ui","sortIcon");
    transient SpriteAPI bar = Global.getSettings().getSprite("terrain","holder");
    public ProgressBarComponent(float width, float height,float currentProgress,Color progressionColor) {
        this.progress = currentProgress;
        this.isInteractiveBar = false;
        this.progressionColor = progressionColor;
        this.renderingPanel = Global.getSettings().createCustom(width,height,this);
    }
    public ProgressBarComponent(float width, float height,int currentSegment, int maxSegments,Color progressionColor) {
        this.progressionColor = progressionColor;
        this.currentSection = currentSegment;
        this.sections = maxSegments;
        this.isInteractiveBar = true;
        this.renderingPanel = Global.getSettings().createCustom(width,height,this);
    }

    public CustomPanelAPI getRenderingPanel() {
        return renderingPanel;
    }
    public boolean haveMovedToAnotherSegment(){
        boolean haveReturned = haveMovedToAnotherSegment;
        haveMovedToAnotherSegment = false;
        return haveReturned;
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {
        if(!isInteractiveBar){
            bar.setSize(renderingPanel.getPosition().getWidth(),renderingPanel.getPosition().getHeight());
            bar.renderAtCenter(renderingPanel.getPosition().getCenterX(),renderingPanel.getPosition().getCenterY());
            float affectedWidth = renderingPanel.getPosition().getWidth()-10;
            spriteForButtonGlow.setSize(affectedWidth,renderingPanel.getPosition().getHeight());
            spriteForButtonGlow.setColor(progressionColor.darker());
            spriteForButtonGlow.renderAtCenter(renderingPanel.getPosition().getCenterX(),renderingPanel.getPosition().getCenterY());
            spriteToRender.setSize((affectedWidth+3)*progress,renderingPanel.getPosition().getHeight()-6);
            spriteToRender.setColor(progressionColor);
            spriteToRender.render(renderingPanel.getPosition().getX()+3,renderingPanel.getPosition().getY()+3);
        }


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
}
