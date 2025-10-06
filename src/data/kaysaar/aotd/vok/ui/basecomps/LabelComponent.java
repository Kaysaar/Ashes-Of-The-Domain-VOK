package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import org.lazywizard.lazylib.ui.FontException;
import org.lazywizard.lazylib.ui.LazyFont;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class LabelComponent extends ResizableComponent {

    public float fontSize;
    public LazyFont.DrawableString draw;
    public LazyFont font;
    float width;
    float pWidth,pHeight;
    public Color color ;
    public LabelComponent(String font, float fontSize, String text, Color color,float width,float height) {
        this.originalWidth =width;
        this.fontSize = fontSize;
        this.color = color;
        try {
            this.font = LazyFont.FontLoader.loadFont(font);
            draw = this.font.createText(text, color, fontSize);
            draw.setBlendSrc(GL11.GL_SRC_ALPHA);
            draw.setBlendDest(GL11.GL_ONE_MINUS_SRC_ALPHA);
            draw.setMaxWidth(width);
        } catch (FontException e) {
            throw new RuntimeException(e);
        }
        pWidth = width;
        pHeight = height;
        this.originalHeight = pHeight;
        componentPanel = Global.getSettings().createCustom(width,height,this);
    }
    public void setText(String text) {
        draw = this.font.createText(text, color, fontSize);
        draw.setMaxWidth(pWidth);
    }
    public float getTextWidth(){
        return draw.getWidth();
    }


    @Override
    public void resizeComponent(float scale) {

    }

    public CustomPanelAPI getReferencePanel() {
        return componentPanel;
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        if(componentPanel!=null){
            draw.setMaxWidth(pWidth*this.scale);
            draw.setFontSize(scale*fontSize);
            Color newO = new Color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, (color.getAlpha()*alphaMult)/255f);
            draw.setBaseColor(newO);
            draw.setBlendSrc(GL11.GL_SRC_ALPHA);
            draw.setBlendDest(GL11.GL_ONE_MINUS_SRC_ALPHA);
            draw.draw(componentPanel.getPosition().getX(),(componentPanel.getPosition().getY()+componentPanel.getPosition().getHeight()));
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

//public CustomPanelAPI referencePanel;
//float scale = 1f;
//public float fontSize;
//LazyFont.DrawableString draw;
//float width;
//        float pWidth,pHeight;
//public LabelComponent(String font, float fontSize, String text, Color color,float width) {
//    this.width =width;
//    this.fontSize = fontSize;
//    try {
//        LazyFont drawableString = LazyFont.FontLoader.loadFont(font);
//        draw = drawableString.createText(text, color, fontSize);
//        draw.setMaxWidth(width);
//    } catch (FontException e) {
//        throw new RuntimeException(e);
//    }
//    pHeight = draw.getHeight();
//    pWidth = draw.getWidth();
//    ra
//            referencePanel = Global.getSettings().createCustom(draw.getWidth(),draw.getHeight(),this);
//}
//
//public void setReferencePanel(CustomPanelAPI referencePanel) {
//    this.referencePanel = referencePanel;
//}
//
//public CustomPanelAPI getReferencePanel() {
//    return referencePanel;
//}
//
//@Override
//public void positionChanged(PositionAPI position) {
//
//}
//
//@Override
//public void renderBelow(float alphaMult) {
//
//}
//
//@Override
//public void render(float alphaMult) {
//    if(referencePanel!=null){
//        draw.draw(referencePanel.getPosition().getX(),referencePanel.getPosition().getY());
//
//    }
//}
//
//@Override
//public void advance(float amount) {
//    scale-=0.01f;
//    if(scale<=0.1f){
//        scale = 1f;
//    }
//    draw.setMaxWidth(width*scale);
//    draw.setFontSize(scale*fontSize);
//    referencePanel.getPosition().setSize(pWidth*scale,pHeight*scale);
//}
//
//@Override
//public void processInput(List<InputEventAPI> events) {
//
//}
//
//@Override
//public void buttonPressed(Object buttonId) {
//
//}