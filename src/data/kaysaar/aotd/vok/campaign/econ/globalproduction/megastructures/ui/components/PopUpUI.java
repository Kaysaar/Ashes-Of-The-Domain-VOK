package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.TrapezoidButtonDetector;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;

public class PopUpUI implements CustomUIPanelPlugin {
    SpriteAPI blackBackground = Global.getSettings().getSprite("rendering","GlitchSquare");
    SpriteAPI borders = Global.getSettings().getSprite("rendering","GlitchSquare");
    SpriteAPI panelBackground  = Global.getSettings().getSprite("ui","panel00_center");
    SpriteAPI bot= Global.getSettings().getSprite("ui","panel00_bot");
    SpriteAPI top= Global.getSettings().getSprite("ui","panel00_top");
    SpriteAPI left= Global.getSettings().getSprite("ui","panel00_left");
    SpriteAPI right= Global.getSettings().getSprite("ui","panel00_right");
    SpriteAPI topLeft= Global.getSettings().getSprite("ui","panel00_top_left");
    SpriteAPI topRight= Global.getSettings().getSprite("ui","panel00_top_right");
    SpriteAPI bottomLeft= Global.getSettings().getSprite("ui","panel00_bot_left");
    SpriteAPI bottomRight= Global.getSettings().getSprite("ui","panel00_bot_right");
    public static float buttonConfirmWidth = 160;
    float frames;
    public CustomPanelAPI panelToInfluence;
    public UILinesRenderer rendererBorder = new UILinesRenderer(0f);
   public ButtonAPI confirmButton;
    public ButtonAPI cancelButton;
    public boolean isDialog =true;
    public ButtonAPI getConfirmButton() {
        return confirmButton;
    }

    public CustomPanelAPI getPanelToInfluence() {
        return panelToInfluence;
    }

    public ButtonAPI getCancelButton() {
        return cancelButton;
    }

    float x,y;
    @Override
    public void positionChanged(PositionAPI position) {

    }

    public void init(CustomPanelAPI panelAPI,float x, float y,boolean isDialog) {
        panelToInfluence = panelAPI;
        UIPanelAPI mainPanel =  ProductionUtil.getCoreUI();
        createUI(panelToInfluence);
        this.isDialog = isDialog;
        mainPanel.addComponent(panelToInfluence).inTL(x, mainPanel.getPosition().getHeight()-y);
        mainPanel.bringComponentToTop(panelToInfluence);
        rendererBorder.setPanel(panelToInfluence);

    }
    public void createUI(CustomPanelAPI panelAPI){
        //Note here is where you create UI : Methods you need to change is advance , createUI, and inputEvents handler
        //Also remember super.apply()


    }
    @Override
    public void renderBelow(float alphaMult) {
        if(panelToInfluence != null){
            if(isDialog){
                blackBackground.setSize(ProductionUtil.getCoreUI().getPosition().getWidth(), ProductionUtil.getCoreUI().getPosition().getHeight());
                blackBackground.setColor(Color.black);
                blackBackground.setAlphaMult(0.6f);
                blackBackground.renderAtCenter( ProductionUtil.getCoreUI().getPosition().getCenterX(),ProductionUtil.getCoreUI().getPosition().getCenterY());
            }
            TiledTextureRenderer renderer = new TiledTextureRenderer(panelBackground.getTextureId());
            renderer.renderTiledTexture(panelToInfluence.getPosition().getX(), panelToInfluence.getPosition().getY(), panelToInfluence.getPosition().getWidth(),  panelToInfluence.getPosition().getHeight(), panelBackground.getTextureWidth(),  panelBackground.getTextureHeight());
            if(isDialog){
                renderBorders(panelToInfluence);
            }
            else{
                rendererBorder.render(alphaMult);
            }


        }
    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {
        if(frames<15){
            frames++;
        }
        if(confirmButton!=null){
            if(confirmButton.isChecked()){
                confirmButton.setChecked(false);
                applyConfirmScript();
                ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
            }
        }
        if(cancelButton!=null){
            if(cancelButton.isChecked()){
                cancelButton.setChecked(false);
                ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
            }

        }
    }
    public void applyConfirmScript(){

    }
    @Override
    public void processInput(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if(frames>=15){
                if(event.isMouseDownEvent()&&!isDialog){
                    TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
                    float xLeft = panelToInfluence.getPosition().getX();
                    float xRight = panelToInfluence.getPosition().getX()+panelToInfluence.getPosition().getWidth();
                    float yBot = panelToInfluence.getPosition().getY();
                    float yTop = panelToInfluence.getPosition().getY()+panelToInfluence.getPosition().getHeight();
                    boolean hovers = detector.determineIfHoversOverButton(xLeft,yTop,xRight,yTop,xLeft,yBot,xRight,yBot,Global.getSettings().getMouseX(),Global.getSettings().getMouseY());
                    if(!hovers){
                        ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
                        event.consume();
                    }
                }
                if(!event.isConsumed()){
                    if(event.getEventValue()== Keyboard.KEY_ESCAPE){
                        ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
                        event.consume();
                        break;
                    }
                }
            }


            event.consume();
        }
    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
    public void renderBorders(CustomPanelAPI panelAPI) {
        float leftX = panelAPI.getPosition().getX()+16;
        top.setSize(16,16);
        bot.setSize(16,16);
        topLeft.setSize(16,16);
        topRight.setSize(16,16);
        bottomLeft.setSize(16,16);
        bottomRight.setSize(16,16);
        left.setSize(16,16);
        right.setSize(16,16);
        float rightX = panelAPI.getPosition().getX()+panelAPI.getPosition().getWidth()-16;
        float botX = panelAPI.getPosition().getY()+16;
        AoTDMisc.startStencilWithXPad(panelAPI,8);
        for (float i = leftX; i <= panelAPI.getPosition().getX()+panelAPI.getPosition().getWidth() ; i+=top.getWidth()) {
            top.renderAtCenter(i,panelAPI.getPosition().getY()+panelAPI.getPosition().getHeight());
            bot.renderAtCenter(i,panelAPI.getPosition().getY());
        }
        AoTDMisc.endStencil();
        AoTDMisc.startStencilWithYPad(panelAPI,8);
        for (float i = botX; i <= panelAPI.getPosition().getY()+panelAPI.getPosition().getHeight();  i+=top.getWidth()) {
            left.renderAtCenter(panelAPI.getPosition().getX(),i);
            right.renderAtCenter(panelAPI.getPosition().getX()+panelAPI.getPosition().getWidth(),i);
        }
        AoTDMisc.endStencil();
        topLeft.renderAtCenter(leftX-16,panelAPI.getPosition().getY()+panelAPI.getPosition().getHeight());
        topRight.renderAtCenter( panelAPI.getPosition().getX()+panelAPI.getPosition().getWidth(),panelAPI.getPosition().getY()+panelAPI.getPosition().getHeight());
        bottomLeft.renderAtCenter(leftX-16,panelAPI.getPosition().getY());
        bottomRight.renderAtCenter( panelAPI.getPosition().getX()+panelAPI.getPosition().getWidth(),panelAPI.getPosition().getY());
    }
    public ButtonAPI generateConfirmButton(TooltipMakerAPI tooltip){
        ButtonAPI button = tooltip.addButton("Confirm","confirm", NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg,Alignment.MID,CutStyle.TL_BR,160,25,0f);
        button.setShortcut(Keyboard.KEY_G,false);
        confirmButton = button;
        return button;
    }
    public ButtonAPI generateCancelButton(TooltipMakerAPI tooltip){
        ButtonAPI button = tooltip.addButton("Cancel","cancel", NidavelirMainPanelPlugin.base,NidavelirMainPanelPlugin.bg,Alignment.MID,CutStyle.TL_BR,buttonConfirmWidth,25,0f);
        button.setShortcut(Keyboard.KEY_ESCAPE,false);
        cancelButton = button;
        return button;
    }
    public void createConfirmAndCancelSection(CustomPanelAPI mainPanel){
        float totalWidth = buttonConfirmWidth*2+10;
        TooltipMakerAPI tooltip = mainPanel.createUIElement(totalWidth,25,false);
        tooltip.setButtonFontOrbitron20();
        generateConfirmButton(tooltip);
        generateCancelButton(tooltip);
        confirmButton.getPosition().inTL(0,0);
        cancelButton.getPosition().inTL(buttonConfirmWidth+5,0);
        float bottom = Math.abs(mainPanel.getPosition().getY()+mainPanel.getPosition().getHeight());
        mainPanel.addUIElement(tooltip).inTL(mainPanel.getPosition().getWidth()-(totalWidth)-10,bottom-40);
    }
}
