package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;

import java.awt.*;

public class JumpPointRenderer extends ResizableComponent {
    JumpPointAPI point;
    SpriteAPI sprite;
    public static  float size = 300;
    public JumpPointRenderer(JumpPointAPI point) {
        this.point = point;
        this.sprite = Global.getSettings().getSprite("systemMap","icon_jump_point");
        if(point.isWormhole()){
            this.sprite = Global.getSettings().getSprite("systemMap", "icon_wormhole");
        }
        this.sprite.setColor(new Color(255, 0, 255));
        this.componentPanel = Global.getSettings().createCustom(JumpPointRenderer.size,JumpPointRenderer.size,this);
    }

    @Override
    public void render(float alphaMult) {
        float size = JumpPointRenderer.size *scale;
        sprite.setSize(size,size);
        sprite.setAlphaMult(alphaMult);
        sprite.renderAtCenter(componentPanel.getPosition().getCenterX(),componentPanel.getPosition().getCenterY());
    }
}
