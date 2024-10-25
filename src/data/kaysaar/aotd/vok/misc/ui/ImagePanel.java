package data.kaysaar.aotd.vok.misc.ui;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;

import java.awt.*;
import java.util.List;

public class ImagePanel implements CustomUIPanelPlugin {
    CustomPanelAPI panelTiedTo;
    SpriteAPI sprite;

    public ImagePanel() {



    }
    public void init(CustomPanelAPI panelTiedTo, SpriteAPI sprite){
        this.panelTiedTo = panelTiedTo;
        this.sprite = sprite;
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        if(sprite!=null) {
            sprite.setSize(panelTiedTo.getPosition().getWidth(),panelTiedTo.getPosition().getHeight());
            sprite.renderAtCenter(panelTiedTo.getPosition().getCenterX(),panelTiedTo.getPosition().getCenterY());
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
