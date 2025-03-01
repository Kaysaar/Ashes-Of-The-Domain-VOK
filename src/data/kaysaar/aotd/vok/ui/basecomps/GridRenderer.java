package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.Global;
import org.lwjgl.opengl.GL11;
import java.awt.*;

public class GridRenderer extends ResizableComponent {
    public float gridRecSize;
    public Color gridColor;
    HorizontalMoverData data;

    public GridRenderer(float width, float height, float gridRecSize, Color gridColor, HorizontalMoverData data) {
        this.originalWidth = width;
        this.originalHeight = height;
        this.gridRecSize = gridRecSize;
        this.gridColor = gridColor;
        this.data = data;
        componentPanel = Global.getSettings().createCustom(width, height, this);
    }

    public int getCurrentGridSize() {
        return (int) (gridRecSize * scale);
    }

    @Override
    public void render(float alphaMult) {
        // Get panel position and dimensions (in screen coordinates)
        float panelX = componentPanel.getPosition().getX();
        float panelY = componentPanel.getPosition().getY();
        float panelWidth = componentPanel.getPosition().getWidth();
        float panelHeight = componentPanel.getPosition().getHeight();
        float cellSize = getCurrentGridSize();

        // Determine the grid's world anchor.
        // originalCoords is the unscaled "world" coordinate for the grid’s origin.
        // data offsets (from panning) are in world coordinates as well.
        float worldAnchorX = (originalCoords != null ? originalCoords.x : 0) + data.getCurrentOffsetX();
        float worldAnchorY = (originalCoords != null ? originalCoords.y : 0) + data.getCurrentOffsetY();

        // Convert the world anchor to screen space by multiplying with the scale.
        float anchorX = worldAnchorX * scale;
        float anchorY = worldAnchorY * scale;

        // Compute starting positions for grid lines so that they remain aligned to the world anchor.
        float startX = anchorX + (float) Math.floor((panelX - anchorX) / cellSize) * cellSize;
        float startY = anchorY + (float) Math.floor((panelY - anchorY) / cellSize) * cellSize;

        // Set grid line color with alpha modulation.
        GL11.glColor4f(
                gridColor.getRed() / 255f,
                gridColor.getGreen() / 255f,
                gridColor.getBlue() / 255f,
                (gridColor.getAlpha() / 255f) * alphaMult
        );

        // Optionally, set the line width.
        GL11.glLineWidth(1.0f);

        GL11.glBegin(GL11.GL_LINES);
        // Draw vertical grid lines well beyond the visible panel.
        for (float x = startX; x <= panelX + panelWidth + 100000; x += cellSize) {
            GL11.glVertex2f(x, panelY);
            GL11.glVertex2f(x, panelY + panelHeight + 100000);
        }
        // Draw horizontal grid lines.
        for (float y = startY; y <= panelY + panelHeight + 100000; y += cellSize) {
            GL11.glVertex2f(panelX, y);
            GL11.glVertex2f(panelX + panelWidth + 100000, y);
        }
        GL11.glEnd();
    }

    @Override
    public void advance(float amount) {
        // Per-frame logic (if needed).
    }
}
