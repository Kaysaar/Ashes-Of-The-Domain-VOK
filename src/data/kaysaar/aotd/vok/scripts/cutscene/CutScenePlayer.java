package data.kaysaar.aotd.vok.scripts.cutscene;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;
import data.scripts.VideoModes;
import data.scripts.VideoPlayerFactory;
import data.scripts.playerui.VideoPlayer;

import java.awt.*;
import java.util.List;

public class CutScenePlayer implements ExtendedUIPanelPlugin {
    VideoPlayer player;
    public enum Stage{
        IN,
        PLAYING,
        OUT
    }
    boolean added = false;
     SpriteAPI sprite = Global.getSettings().getSprite("rendering", "GlitchSquare");
    Stage stage = Stage.IN;
    CustomPanelAPI mainPanel;
    float opacity = 0f;
    UIPanelAPI parent;
    float frames = 0;
    float maxFrames =8;
    float progressFrames = 4;
    float exitFrames = 6;
    public CutScenePlayer(){
        this.parent = ProductionUtil.getCoreUI();
        Global.getSector().setPaused(true);

        mainPanel = Global.getSettings().createCustom(Global.getSettings().getScreenWidthPixels(),Global.getSettings().getScreenHeightPixels(),this);
        Global.getSoundPlayer().setSuspendDefaultMusicPlayback(true);
        Global.getSoundPlayer().pauseCustomMusic();
        Global.getSoundPlayer().pauseMusic();
        parent.addComponent(mainPanel).inTL(0,0);
        parent.bringComponentToTop(mainPanel);

    }
    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {

    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {




    }

    @Override
    public void render(float alphaMult) {
        if(stage.equals(Stage.IN)){
            sprite.setSize(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight());
            sprite.setColor(Color.black);
            sprite.setAlphaMult(opacity);
            sprite.renderAtCenter(mainPanel.getPosition().getCenterX(), mainPanel.getPosition().getCenterY());
        } else if (frames<maxFrames) {

            float rem = maxFrames-frames;
            if(frames>=progressFrames){
                float progress = rem/maxFrames;
                sprite.setAlphaMult(progress);
            }
            sprite.setSize(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight());
            sprite.setColor(Color.black);
            sprite.renderAtCenter(mainPanel.getPosition().getCenterX(), mainPanel.getPosition().getCenterY());
            frames++;

        }
        if(stage.equals(Stage.OUT)||(player!=null&&!player.getProjector().isRendering())){
            if(exitFrames<=0){
                sprite.setSize(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight());
                sprite.setColor(Color.black);
                sprite.setAlphaMult(opacity);
                sprite.renderAtCenter(mainPanel.getPosition().getCenterX(), mainPanel.getPosition().getCenterY());
            }
            else{
                exitFrames--;
                sprite.setSize(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight());
                sprite.setColor(Color.black);
                sprite.setAlphaMult(alphaMult);
                sprite.renderAtCenter(mainPanel.getPosition().getCenterX(), mainPanel.getPosition().getCenterY());
            }


        }

    }

    @Override
    public void advance(float amount) {
        if(opacity<1&&stage.equals(Stage.IN)){
            opacity+=amount;

        }
        Global.getSector().getCampaignUI().clearMessages();
        if(!added&&stage.equals(Stage.PLAYING)){
            added =true;
            player.getProjector().advance(amount);
            player.addTo(mainPanel).inTL(0,0);
            player.init();

        }
        if(opacity>=1&&stage==Stage.IN){
            opacity =1f;
            stage = Stage.PLAYING;
            player = VideoPlayerFactory.createAudioVideoPlayer("vl_demo",(int)mainPanel.getPosition().getWidth(),(int)mainPanel.getPosition().getHeight(),1f, VideoModes.PlayMode.PLAYING, VideoModes.EOFMode.PLAY_UNTIL_END,true);



            return;
        }
        if(Stage.PLAYING==stage&&!player.getProjector().isRendering()){
            stage = Stage.OUT;
        }
        if(stage==Stage.OUT){
            opacity-=amount;
            if(opacity<=0){
                Global.getSoundPlayer().setSuspendDefaultMusicPlayback(false);
                Global.getSoundPlayer().resumeCustomMusic();
                parent.removeComponent(mainPanel);
                player.finish();
                Global.getSector().setPaused(false);
            }
        }

    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        events.stream().filter(x->!x.isConsumed()).forEach(InputEventAPI::consume);
    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
