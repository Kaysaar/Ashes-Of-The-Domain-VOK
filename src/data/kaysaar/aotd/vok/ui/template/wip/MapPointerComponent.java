package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;

import java.awt.*;

public class MapPointerComponent extends ResizableComponent {

    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering", "GlitchSquare");
    ResizableComponent pointer;
    MapMainComponent mainComponent;

    public MapPointerComponent(ResizableComponent pointer, MapMainComponent mainComponent) {
        this.componentPanel = Global.getSettings().createCustom(1, 1, this);
        this.pointer = pointer;
        this.mainComponent = mainComponent;
    }

    boolean shouldRender = false;

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    @Override
    public void renderBelow(float alphaMult) {
        super.renderBelow(alphaMult);


    }

    @Override
    public void render(float alphaMult) {
        if (!shouldRender || !mainComponent.doesHover()) return;

        // style
        Color color = new Color(174, 192, 196, 180);
        spriteToRender.setColor(color);
        spriteToRender.setAlphaMult(alphaMult * 0.6f);
        spriteToRender.setNormalBlend();

        // --- Calculate bounds EXACTLY like the grid does ---
        // Using the grid's corner components: topLeft/topRight/bottomLeft/bottomRight
        // (Expose getters on MapGridRenderer if needed.)
        ResizableComponent tl = mainComponent.getLeftTop();
        ResizableComponent tr = mainComponent.getRightTop();
        ResizableComponent bl = mainComponent.getLeftBottom();
        ResizableComponent br = mainComponent.getRightBottom(); // not used but here for symmetry

        float left   = tl.getComponentPanel().getPosition().getX();
        float right  = tr.getComponentPanel().getPosition().getX();
        float topY   = tl.getComponentPanel().getPosition().getCenterY();
        float bottomY= bl.getComponentPanel().getPosition().getCenterY();

        // Ensure same ordering logic as in MapGridRenderer
        if (right < left) { float t = left; left = right; right = t; }
        if (bottomY > topY) { float t = bottomY; bottomY = topY; topY = t; }

        float width  = right - left;
        float height = topY - bottomY;

        if (width <= 0f || height <= 0f) return;

        float cx = Global.getSettings().getMouseX();
        float cy = Global.getSettings().getMouseY();

        // clamp crosshair to grid rect
        if (cx < left)  cx = left;
        if (cx > right) cx = right;
        if (cy < bottomY) cy = bottomY;
        if (cy > topY)    cy = topY;

        // horizontal line through cy
        spriteToRender.setSize(width, 3f);
        spriteToRender.render(left, cy);

        // vertical line through cx
        spriteToRender.setSize(3f, height);
        spriteToRender.render(cx, bottomY);
    }
}
