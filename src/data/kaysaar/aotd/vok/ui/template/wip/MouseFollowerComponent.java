package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class MouseFollowerComponent extends ResizableComponent {
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering", "GlitchSquare");
    boolean showMouseIndicator  =false;
    public MouseFollowerComponent() {
        componentPanel = Global.getSettings().createCustom(1,1,this);
    }
    public void updatePositionOfPanel(Vector2f vec) {
        this.originalCoords = vec;
        resize(this.scale);
    }

    public void setShowMouseIndicator(boolean showMouseIndicator) {
        this.showMouseIndicator = showMouseIndicator;
    }

    @Override
    public void render(float alphaMult) {
        if(showMouseIndicator) {
            spriteToRender.setSize(10,10);
            spriteToRender.setAlphaMult(alphaMult);
            spriteToRender.setColor(Color.orange);
            spriteToRender.renderAtCenter(componentPanel.getPosition().getCenterX(),componentPanel.getPosition().getCenterY());
        }

    }
}
