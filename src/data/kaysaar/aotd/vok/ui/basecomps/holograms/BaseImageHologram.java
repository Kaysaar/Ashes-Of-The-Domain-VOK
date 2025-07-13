package data.kaysaar.aotd.vok.ui.basecomps.holograms;

import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;

import java.awt.*;

public class BaseImageHologram implements HologramViewerObjectRendererAPI {
    SpriteAPI sprite;
    public BaseImageHologram(SpriteAPI sprite,Color color) {
        this.sprite = sprite;
        sprite.setColor(color);
    }
    Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void init(CustomPanelAPI panelOfRendering) {
        sprite.setSize(panelOfRendering.getPosition().getWidth(),panelOfRendering.getPosition().getHeight());
    }

    @Override
    public void render(float alphaMult,float centerX,float centeryY) {
        sprite.setAlphaMult(alphaMult);
        sprite.renderAtCenter(centerX,centeryY);
    }
}
