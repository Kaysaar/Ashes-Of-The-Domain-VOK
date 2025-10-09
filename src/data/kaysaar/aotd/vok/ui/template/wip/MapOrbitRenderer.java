package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class MapOrbitRenderer extends ResizableComponent {
    private float radius;                 // world-space radius of the orbit
    private Vector2f center = new Vector2f(0f, 0f); // world-space center
    private Color color = new Color(120, 160, 200, 180);
    private float lineWidth = 1f;         // base line width at scale=1

    public MapOrbitRenderer(float radius) {
        this.radius = radius;
        this.componentPanel = Global.getSettings().createCustom(1, 1, this);
    }

    public void setCenter(Vector2f center) { this.center = center; }
    public void setColor(Color color) { this.color = color; }
    public void setLineWidth(float lineWidth) { this.lineWidth = lineWidth; }
    public void setRadius(float radius) { this.radius = radius; }

    @Override
    public void renderBelow(float alphaMult) {
        super.renderBelow(alphaMult);

        // Convert center to UI coordinates (use your mapping)


        // Scale radius by current UI scale
        float scaledRadius = radius * scale;

        if (scaledRadius <= 0f) return;

        // Choose segment count based on on-screen circumference (≈ 4 px per segment)
        float circumference = (float) (2f * Math.PI * scaledRadius);
        int segments = Math.max(32, Math.min(512, (int) (circumference / 4f)));

        // --- OpenGL state ---
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LINE_BIT);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // Line width scales with UI scale (clamped for sanity)
            float lw = Math.max(1f, lineWidth * scale);
            GL11.glLineWidth(lw);

            float a = (color.getAlpha() / 255f) * alphaMult;
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, a);

            // Draw the orbit as a line loop
            GL11.glBegin(GL11.GL_LINE_LOOP);
            for (int i = 0; i < segments; i++) {
                float t = (float) (i * (2 * Math.PI / segments));
                float x = componentPanel.getPosition().getCenterX() + (float) Math.cos(t) * scaledRadius;
                float y = componentPanel.getPosition().getCenterY() + (float) Math.sin(t) * scaledRadius;
                GL11.glVertex2f(x, y);
            }
            GL11.glEnd();
        } finally {
            GL11.glPopAttrib();
        }
    }

    /**
     * World -> UI translation.
     * Assumes UI top-left is (0,0) and world spans [-27000 .. +27000] in both axes.
     * If your UI Y axis is inverted, keep the minus on Y; otherwise, switch to +.
     */
    public static Vector2f translateCoordinatesToUI(Vector2f worldLocation) {
        final float HALF = 27000f;
        float uiX = worldLocation.x + HALF;  // -HALF → 0, +HALF → 2*HALF
        float uiY = HALF - worldLocation.y;  // +HALF (top) → 0, -HALF (bot) → 2*HALF
        return new Vector2f(uiX, uiY);
    }
}
