package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.TiledTextureRenderer;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class ButtonComponent extends ResizableComponent {

    float currHighlight = 0f;
    float maxHighlight = 0.15f;
    Color highlightColor = Misc.getButtonTextColor();
    Color backgroundColor = Misc.getStoryDarkColor();
    CustomPanelAPI absolutePanel;
    public boolean isClickable = true;
    public float alphaBG = 0.95f;

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }
    boolean enableRightClick = false;
    boolean decreasedMode = true;

    public void setEnableRightClick(boolean enableRightClick) {
        this.enableRightClick = enableRightClick;
    }

    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering", "GlitchSquare");
    SpriteAPI panelBackground = Global.getSettings().getSprite("ui", "panel00_center");
    TiledTextureRenderer renderers = new TiledTextureRenderer(panelBackground.getTextureId());
    public UILinesRenderer renderer;
    IntervalUtil breakBetweenButtons = null;
    boolean overrideHighlight = false;
    LabelComponent component;
    public void setColorOfBorder(Color color){
        this.renderer.setBoxColor(color);
    }
    public ButtonComponent(float width, float height) {
        currHighlight = 0f;
        originalWidth = width;
        originalHeight = height;
        componentPanel = Global.getSettings().createCustom(width, height, this);
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(componentPanel);
    }

    public void setText(String text) {
        if (component == null) {

            component = new LabelComponent("graphics/fonts/orbitron12condensed.fnt", 16, text, Color.ORANGE, componentPanel.getPosition().getWidth() - 20, componentPanel.getPosition().getHeight() / 3);
            Vector2f vec = new Vector2f(componentPanel.getPosition().getWidth() / 2 - component.getTextWidth() / 2, (componentPanel.getPosition().getHeight() / 2) - (component.draw.getHeight() / 2));
            addComponent(component, vec.x, vec.y);
        } else {

            component.setText(text);
        }

    }


    public boolean shouldHighlight() {

        return (doesHover() || overrideHighlight)&&isClickable;
    }


    public CustomPanelAPI getPanelOfButton() {
        return componentPanel;
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }
public boolean shouldRenderBorders = true;

    public void setShouldRenderBorders(boolean shouldRenderBorders) {
        this.shouldRenderBorders = shouldRenderBorders;
    }

    @Override
    public void renderBelow(float alphaMult) {
        renderers.renderTiledTexture(componentPanel.getPosition().getX(), componentPanel.getPosition().getY(), componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight(), panelBackground.getTextureWidth(), panelBackground.getTextureHeight(), alphaBG * alphaMult, backgroundColor);
        if (currHighlight >= maxHighlight) {
            currHighlight = maxHighlight;
        }


    }

    @Override
    public void render(float alphaMult) {
        spriteToRender.setColor(highlightColor);
        spriteToRender.setAlphaMult(alphaMult * currHighlight);
        spriteToRender.setSize(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight());
        if(currHighlight!=0){
            spriteToRender.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());

        }
        if(shouldRenderBorders){
            renderer.render(alphaMult);
            renderer.render(alphaMult);
            renderer.render(alphaMult);
        }


    }

    @Override
    public void advance(float amount) {
        if(!blockButtonInstance){
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

    }
public  boolean blockButtonInstance = false;

    public boolean isBreakBetweenClicks() {
        return breakBetweenButtons != null;
    }

    public void initBreak() {
        breakBetweenButtons = new IntervalUtil(0.1f, 0.1f);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        if(!blockButtonInstance){
            for (InputEventAPI event : events) {
                if (event.isConsumed()) continue;
                if (event.isLMBDownEvent()) {
                    if (doesHover() && !isBreakBetweenClicks()&&isClickable) {
                        Global.getSoundPlayer().playUISound("ui_button_pressed", 1, 1);
                        initBreak();
                        performActionOnClick(false);
                        event.consume();
                    }
                }
                if (event.isRMBDownEvent()) {
                    if (doesHover() && !isBreakBetweenClicks()&&isClickable&&enableRightClick) {
                        Global.getSoundPlayer().playUISound("ui_button_pressed", 1, 1);
                        initBreak();
                        performActionOnClick(true);
                        event.consume();
                    }
                }
            }
        }

    }

    public void performActionOnClick(boolean isRightClick) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
