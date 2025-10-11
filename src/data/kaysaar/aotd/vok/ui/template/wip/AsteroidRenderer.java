package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class AsteroidRenderer extends ResizableComponent {
     AsteroidAPI asteroid;
    SpriteAPI sprite;
    Color asteroidColor;
    public AsteroidRenderer(AsteroidAPI asteroid) {
        sprite = Global.getSettings().getSprite("graphics/warroom/icon_asteroid.png");
        this.asteroid = asteroid;
        this.asteroidColor = Global.getSettings().getColor("asteroidBeltMapColor");
        this.componentPanel= Global.getSettings().createCustom(1,1,this);
    }

    @Override
    public void render(float alphaMult) {
            float var5 = asteroid.getRadius() * scale * 9.0F;
            sprite.setSize(var5, var5);
            sprite.setColor(asteroidColor);
            sprite.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());


    }
    public Vector2f translateCoordinatesToUI(Vector2f worldLocation) {
        float uiX = worldLocation.x + 27000f;   // shift X so -27000 → 0, +27000 → 54000
        float uiY = 27000f - worldLocation.y;   // invert Y so +27000 → 0 (top), -27000 → 54000 (bottom)
        return new Vector2f(uiX, uiY);
    }
}
