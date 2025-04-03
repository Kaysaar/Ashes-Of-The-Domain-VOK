package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.graphics.Sprite;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.scripts.TrapezoidButtonDetector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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
    int minSection;
    boolean pressingMouse = false;
    boolean detectedOnceInPerimiters = false;
    boolean passFirst = true;
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering","GlitchSquare");
    transient SpriteAPI spriteForButtonGlow = Global.getSettings().getSprite("ui","aotd_text_glow");
    transient SpriteAPI arrows = Global.getSettings().getSprite("ui","sortIcon");
    transient SpriteAPI bar = Global.getSettings().getSprite("ui","aotd_holder");
    TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
    public ProgressBarComponent(float width, float height,float currentProgress,Color progressionColor) {
        this.progress = currentProgress;
        this.isInteractiveBar = false;
        this.progressionColor = progressionColor;
        this.renderingPanel = Global.getSettings().createCustom(width,height,this);
    }
    public ProgressBarComponent(float width, float height,int currentSegment, int maxSegments,Color progressionColor,int minSection) {
        this.progressionColor = progressionColor;
        this.currentSection = currentSegment;
        this.sections = maxSegments;
        this.isInteractiveBar = true;
        this.minSection = minSection;
        this.renderingPanel = Global.getSettings().createCustom(width,height,this);

    }

    public CustomPanelAPI getRenderingPanel() {
        return renderingPanel;
    }
    public boolean haveMovedToAnotherSegment(){
        return haveMovedToAnotherSegment;
    }

    public void setHaveMovedToAnotherSegment(boolean haveMovedToAnotherSegment) {
        this.haveMovedToAnotherSegment = haveMovedToAnotherSegment;
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

        float affectedWidth = renderingPanel.getPosition().getWidth()-7;
        spriteForButtonGlow.setSize(affectedWidth,renderingPanel.getPosition().getHeight());
        spriteForButtonGlow.setColor(progressionColor.darker());
        spriteForButtonGlow.renderAtCenter(renderingPanel.getPosition().getCenterX(),renderingPanel.getPosition().getCenterY());
        if(!isInteractiveBar){
            spriteToRender.setSize((affectedWidth)*progress,renderingPanel.getPosition().getHeight()-4);
            spriteToRender.setColor(progressionColor);
            spriteToRender.render(renderingPanel.getPosition().getX()+3,renderingPanel.getPosition().getY()+2);
        }
        else{
            float sectionWidth = affectedWidth/sections;
            spriteToRender.setSize((sectionWidth)*currentSection,renderingPanel.getPosition().getHeight());
            spriteToRender.setColor(progressionColor);
            float centerX = (sectionWidth)*currentSection+renderingPanel.getPosition().getX()+3;
            arrows.setColor(Color.cyan);
            arrows.setAngle(0);
            arrows.renderAtCenter(centerX,renderingPanel.getPosition().getY()+renderingPanel.getPosition().getHeight()+5);
            arrows.setAngle(180);
            arrows.renderAtCenter(centerX,renderingPanel.getPosition().getY()-5);
            spriteToRender.render(renderingPanel.getPosition().getX()+3,renderingPanel.getPosition().getY());

        }
        bar.setSize(renderingPanel.getPosition().getWidth(),renderingPanel.getPosition().getHeight());
        bar.renderAtCenter(renderingPanel.getPosition().getCenterX(),renderingPanel.getPosition().getCenterY());


    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        if(isInteractiveBar){
            float affectedWidth = renderingPanel.getPosition().getWidth()-7;
            float sectionWidth = affectedWidth/sections;
            float mouseX = Global.getSettings().getMouseX();
            float mouseY = Global.getSettings().getMouseY();

            for (InputEventAPI event : events) {
                float topY = renderingPanel.getPosition().getY()+renderingPanel.getPosition().getHeight();
                float bottomY = renderingPanel.getPosition().getY();
                if(event.isConsumed())continue;
                if(event.isLMBDownEvent())pressingMouse = true;
                if(event.isLMBUpEvent()){
                    pressingMouse = false;
                    detectedOnceInPerimiters = false;
                }
                if(pressingMouse){
                    float prevX = renderingPanel.getPosition().getX()+sectionWidth/2+10;
                    if(detectedOnceInPerimiters){
                        topY = Global.getSettings().getScreenHeight();
                        bottomY = 0;
                    }
                    for (int i = minSection; i <= sections; i++) {
                        float currSection = sectionWidth*i;
                        if(detector.determineIfHoversOverButton(prevX,topY,currSection,topY,prevX,bottomY,currSection,bottomY,mouseX,mouseY)){
                            detectedOnceInPerimiters = true;
                            currentSection = i;
                            haveMovedToAnotherSegment = true;
                            break;
                        }
                        prevX += sectionWidth;
                    }
                }
                if(event.getEventValue()== Keyboard.KEY_LEFT){
                    if(!passFirst){
                        currentSection--;

                        if(currentSection<=minSection){
                            currentSection = minSection;
                        }
                        haveMovedToAnotherSegment = true;
                        passFirst = true;
                    }
                    else{
                        passFirst = false;
                    }

                }
                if(event.getEventValue()== Keyboard.KEY_RIGHT){
                    if(!passFirst){
                        currentSection++;
                        if(currentSection >= sections){
                            currentSection = sections;

                        }
                        haveMovedToAnotherSegment = true;
                        passFirst = true;
                    }
                    else{
                        passFirst = false;
                    }


                }

                    event.consume();

            }
        }

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
