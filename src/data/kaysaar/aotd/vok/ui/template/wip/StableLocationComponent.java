package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.graphics.SpriteAPI;

public class StableLocationComponent extends ResizableComponent {
    SpriteAPI sprite = Global.getSettings().getSprite("systemMap","icon_stable_location");
    SectorEntityToken token;
    public StableLocationComponent(SectorEntityToken token) {
        this.token = token;
        this.componentPanel = Global.getSettings().createCustom(token.getRadius()*4,token.getRadius()*4,this);

    }

    @Override
    public void render(float alphaMult) {
        float iconSize = token.getRadius()*2;
        float trueIconSize = iconSize*scale;
        sprite.setSize(trueIconSize, trueIconSize);
        sprite.renderAtCenter(componentPanel.getPosition().getCenterX(),componentPanel.getPosition().getCenterY());
    }
}
