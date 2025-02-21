package data.kaysaar.aotd.vok.ui.newcomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.TiledTextureRenderer;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.scripts.TrapezoidButtonDetector;
import org.lazywizard.lazylib.ui.LazyFont;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class ButtonComponent extends ResizableComponent {

    float currHighlight = 0f;
    float maxHighlight = 0.15f;
    Color highlightColor = Color.RED;
    CustomPanelAPI absolutePanel;
    boolean decreasedMode = true;
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering", "GlitchSquare");
    SpriteAPI panelBackground = Global.getSettings().getSprite("ui", "panel00_center");
    TiledTextureRenderer renderers = new TiledTextureRenderer(panelBackground.getTextureId());
    UILinesRenderer renderer;
    IntervalUtil breakBetweenButtons = null;
    boolean overrideHighlight = false;
    LabelComponent component;

    public ButtonComponent(float width, float height) {
        currHighlight = 0f;
        componentPanel = Global.getSettings().createCustom(width, height, this);
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(componentPanel);
    }

    public void setText(String text) {
        if(component==null){

            component = new LabelComponent("graphics/fonts/orbitron12condensed.fnt", 16, text, Color.ORANGE, componentPanel.getPosition().getWidth() - 20, componentPanel.getPosition().getHeight() / 3);
            Vector2f vec=new Vector2f(componentPanel.getPosition().getWidth()/2 - component.getTextWidth()/2, (componentPanel.getPosition().getHeight() /2) - (component.draw.getHeight()/2) );
            component.setOriginalCoords(vec);
            componentPanel.addComponent(component.getReferencePanel()).setLocation(0,0).inTL(vec.x, vec.y);
        }
        else{

            component.setText(text);
        }

    }



    public boolean shouldHighlight() {

        return doesHover() || overrideHighlight;
    }

    public CustomPanelAPI getPanelOfButton() {
        return componentPanel;
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {
        renderers.renderTiledTexture(componentPanel.getPosition().getX(), componentPanel.getPosition().getY(), componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight(), panelBackground.getTextureWidth(), panelBackground.getTextureHeight(), 0.9f * alphaMult, highlightColor);
        if (currHighlight >= maxHighlight) {
            currHighlight = maxHighlight;
        }
        spriteToRender.setColor(highlightColor);
        spriteToRender.setAlphaMult(alphaMult * currHighlight);
        spriteToRender.setSize(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight());
        spriteToRender.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());
        renderer.render(alphaMult);
    }

    @Override
    public void render(float alphaMult) {


    }

    @Override
    public void advance(float amount) {
        float scale = this.scale;
        if (shouldHighlight()) {
            currHighlight += 0.02f;
            if (currHighlight >= maxHighlight) {
                currHighlight = maxHighlight;
            }

        } else {
            currHighlight -= 0.02f;
            if (currHighlight <= 0) {
                currHighlight = 0f;
            }
        }
        if (breakBetweenButtons != null) {
            breakBetweenButtons.advance(amount);
            if (breakBetweenButtons.intervalElapsed()) {
                breakBetweenButtons = null;
            }
        }
    }

    public boolean isBreakBetweenClicks() {
        return breakBetweenButtons != null;
    }

    public void initBreak() {
        breakBetweenButtons = new IntervalUtil(0.1f, 0.1f);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;
            if (event.isLMBDownEvent()) {
                if (doesHover() && !isBreakBetweenClicks()) {
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1, 1);
                    initBreak();
                    event.consume();
                }
            }
        }
    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
