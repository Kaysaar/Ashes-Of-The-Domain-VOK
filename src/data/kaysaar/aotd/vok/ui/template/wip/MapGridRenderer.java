package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;

import java.awt.*;

public class MapGridRenderer extends ResizableComponent {
    private final ResizableComponent topLeft;
    private final ResizableComponent topRight;
    private final ResizableComponent bottomLeft;
    private final ResizableComponent bottomRight;

    /** Base grid size at scale = 1 */
    public float gridSize = 3000f;

    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering", "GlitchSquare");

    public MapGridRenderer(ResizableComponent topLeft, ResizableComponent topRight,
                           ResizableComponent bottomLeft, ResizableComponent bottomRight) {
        this.componentPanel = Global.getSettings().createCustom(1,1,this);
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public void renderBelow(float alphaMult) {
        super.renderBelow(alphaMult);

        // Grid spacing adjusted by current scale
        float scaledGridSize = gridSize * scale;

        // Configure sprite appearance
        Color color = new Color(54, 107, 119, 180);
        spriteToRender.setColor(color);
        spriteToRender.setAlphaMult(alphaMult*0.6f);
        spriteToRender.setNormalBlend();

        // Determine grid bounds
        float startX = topLeft.getComponentPanel().getPosition().getX();
        float endX = topRight.getComponentPanel().getPosition().getX();
        float topY = topLeft.getComponentPanel().getPosition().getCenterY();
        float bottomY = bottomLeft.getComponentPanel().getPosition().getCenterY();

        // Ensure correct Y order
        if (bottomY > topY) {
            float tmp = bottomY;
            bottomY = topY;
            topY = tmp;
        }

        float width = endX - startX;
        float height = topY - bottomY;

        // --- Horizontal grid lines ---
        spriteToRender.setSize(width, 1f);
        for (float y = topY; y >= bottomY; y -= scaledGridSize) {
            spriteToRender.render(startX, y);
        }

        // --- Vertical grid lines ---
        spriteToRender.setSize(1f, height);
        for (float x = startX; x <= endX; x += scaledGridSize) {
            spriteToRender.render(x, bottomY);
        }
    }
}
