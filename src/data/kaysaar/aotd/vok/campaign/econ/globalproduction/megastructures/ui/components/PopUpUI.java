package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.TrapezoidButtonDetector;
import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
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
    public float limit =5;
    public float frames;
    public CustomPanelAPI panelToInfluence;
    public ashlib.data.plugins.ui.plugins.UILinesRenderer rendererBorder = new UILinesRenderer(0f);
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
    public boolean reachedMaxHeight =  false;
    float originalSizeX ,originalSizeY;
    float x,y;
    @Override
    public void positionChanged(PositionAPI position) {

    }

    public void init(CustomPanelAPI panelAPI,float x, float y,boolean isDialog) {
        panelToInfluence = panelAPI;
        UIPanelAPI mainPanel = ProductionUtil.getCoreUI();
        originalSizeX = panelAPI.getPosition().getWidth();
        originalSizeY = panelAPI.getPosition().getHeight();

        panelToInfluence.getPosition().setSize(16,16);
        this.isDialog = isDialog;

        mainPanel.addComponent(panelToInfluence).inTL(x, mainPanel.getPosition().getHeight()-y);
        mainPanel.bringComponentToTop(panelToInfluence);
        rendererBorder.setPanel(panelToInfluence);

    }
    public void createUI(CustomPanelAPI panelAPI){
        //Note here is where you create UI : Methods you need to change is advance , createUI, and inputEvents handler
        //Also remember super.apply()


    }
    public float createUIMockup(CustomPanelAPI panelAPI){
        return 0f;
    }
    @Override
    public void renderBelow(float alphaMult) {
        if(panelToInfluence != null){
            TiledTextureRenderer renderer = new TiledTextureRenderer(panelBackground.getTextureId());
            if(isDialog){
                blackBackground.setSize(ProductionUtil.getCoreUI().getPosition().getWidth(), ProductionUtil.getCoreUI().getPosition().getHeight());
                blackBackground.setColor(Color.black);
                blackBackground.setAlphaMult(0.6f);
                blackBackground.renderAtCenter(ProductionUtil.getCoreUI().getPosition().getCenterX(),ProductionUtil.getCoreUI().getPosition().getCenterY());
                renderer.renderTiledTexture(panelToInfluence.getPosition().getX(), panelToInfluence.getPosition().getY(), panelToInfluence.getPosition().getWidth(),  panelToInfluence.getPosition().getHeight(), panelBackground.getTextureWidth(),  panelBackground.getTextureHeight(),(frames/limit)*0.9F,Color.BLACK);

            }
            else {
                renderer.renderTiledTexture(panelToInfluence.getPosition().getX(), panelToInfluence.getPosition().getY(), panelToInfluence.getPosition().getWidth(),  panelToInfluence.getPosition().getHeight(), panelBackground.getTextureWidth(),  panelBackground.getTextureHeight(),(frames/limit),panelBackground.getColor());

            }
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

        if(frames<=limit){
            frames++;
            float progress = frames/limit;
            if(frames<limit&&!reachedMaxHeight){
                panelToInfluence.getPosition().setSize(originalSizeX,originalSizeY*progress);
                return;
            }
            if(frames>=limit&&!reachedMaxHeight){
                reachedMaxHeight = true;
                panelToInfluence.getPosition().setSize(originalSizeX,originalSizeY);
                createUI(panelToInfluence);
                return;

            }


        }
        if(confirmButton!=null){
            if(confirmButton.isChecked()){
                confirmButton.setChecked(false);
                applyConfirmScript();
              ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
                onExit();
            }
        }
        if(cancelButton!=null){
            if(cancelButton.isChecked()){
                cancelButton.setChecked(false);
                ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
                onExit();
            }

        }
    }
    public void applyConfirmScript(){

    }
    @Override
    public void processInput(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if(frames>=limit-1&&reachedMaxHeight){
                if(event.isMouseDownEvent()&&!isDialog){
                    ashlib.data.plugins.ui.models.TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
                    float xLeft = panelToInfluence.getPosition().getX();
                    float xRight = panelToInfluence.getPosition().getX()+panelToInfluence.getPosition().getWidth();
                    float yBot = panelToInfluence.getPosition().getY();
                    float yTop = panelToInfluence.getPosition().getY()+panelToInfluence.getPosition().getHeight();
                    boolean hovers = detector.determineIfHoversOverButton(xLeft,yTop,xRight,yTop,xLeft,yBot,xRight,yBot,Global.getSettings().getMouseX(),Global.getSettings().getMouseY());
                    if(!hovers){
                       ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
                        event.consume();
                        onExit();
                    }
                }
                if(!event.isConsumed()){
                    if(event.getEventValue()== Keyboard.KEY_ESCAPE){
                        ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
                        event.consume();
                        onExit();
                        break;
                    }
                }
            }
            event.consume();
        }

    }
    public void forceDismiss(){
       ProductionUtil.getCoreUI().removeComponent(panelToInfluence);
        onExit();
    }
    public void onExit(){

    }
    @Override
    public void buttonPressed(Object buttonId) {

    }
    public void renderBorders(CustomPanelAPI panelAPI) {
        float leftX = panelAPI.getPosition().getX()+16;
        float currAlpha = frames/limit;
        if(currAlpha>=1)currAlpha =1;
        top.setSize(16,16);
        bot.setSize(16,16);
        topLeft.setSize(16,16);
        topRight.setSize(16,16);
        bottomLeft.setSize(16,16);
        bottomRight.setSize(16,16);
        left.setSize(16,16);
        right.setSize(16,16);

        top.setAlphaMult(currAlpha);
        bot.setAlphaMult(currAlpha);
        topLeft.setAlphaMult(currAlpha);
        topRight.setAlphaMult(currAlpha);
        bottomLeft.setAlphaMult(currAlpha);
        bottomRight.setAlphaMult(currAlpha);
        left.setAlphaMult(currAlpha);
        right.setAlphaMult(currAlpha);

        float rightX = panelAPI.getPosition().getX()+panelAPI.getPosition().getWidth()-16;
        float botX = panelAPI.getPosition().getY()+16;
        AshMisc.startStencilWithXPad(panelAPI,8);
        for (float i = leftX; i <= panelAPI.getPosition().getX()+panelAPI.getPosition().getWidth() ; i+=top.getWidth()) {
            top.renderAtCenter(i,panelAPI.getPosition().getY()+panelAPI.getPosition().getHeight());
            bot.renderAtCenter(i,panelAPI.getPosition().getY());
        }
        AshMisc.endStencil();
        AshMisc.startStencilWithYPad(panelAPI,8);
        for (float i = botX; i <= panelAPI.getPosition().getY()+panelAPI.getPosition().getHeight();  i+=top.getWidth()) {
            left.renderAtCenter(panelAPI.getPosition().getX(),i);
            right.renderAtCenter(panelAPI.getPosition().getX()+panelAPI.getPosition().getWidth(),i);
        }
        AshMisc.endStencil();
        topLeft.renderAtCenter(leftX-16,panelAPI.getPosition().getY()+panelAPI.getPosition().getHeight());
        topRight.renderAtCenter( panelAPI.getPosition().getX()+panelAPI.getPosition().getWidth(),panelAPI.getPosition().getY()+panelAPI.getPosition().getHeight());
        bottomLeft.renderAtCenter(leftX-16,panelAPI.getPosition().getY());
        bottomRight.renderAtCenter( panelAPI.getPosition().getX()+panelAPI.getPosition().getWidth(),panelAPI.getPosition().getY());
    }
    public ButtonAPI generateConfirmButton(TooltipMakerAPI tooltip){
        ButtonAPI button = tooltip.addButton("Confirm","confirm", Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,160,25,0f);
        button.setShortcut(Keyboard.KEY_G,true);
        confirmButton = button;
        return button;
    }
    public ButtonAPI generateCancelButton(TooltipMakerAPI tooltip){
        ButtonAPI button = tooltip.addButton("Cancel","cancel", Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,buttonConfirmWidth,25,0f);
        button.setShortcut(Keyboard.KEY_ESCAPE,true);
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
        float bottom = originalSizeY;
        mainPanel.addUIElement(tooltip).inTL(mainPanel.getPosition().getWidth()-(totalWidth)-10,bottom-40);
    }
}
