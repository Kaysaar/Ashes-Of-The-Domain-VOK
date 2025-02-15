package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TiledTextureRenderer {
    private int textureId;

    public TiledTextureRenderer(int textureId) {
        this.textureId = textureId;
    }

    public void renderTiledTexture(float x, float y, float width, float height, float tileWidth, float tileHeight, float alphaMult, Color color) {
        if (textureId == 0) {
            System.err.println("Error: Invalid texture ID.");
            return;
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        // Enable blending for alpha transparency
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Set the texture to repeat
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        // Set nearest neighbor filtering to preserve the lines
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        // Calculate texture repeat factors based on the panel's size and the texture's tile size
        float uMax = width / tileWidth; // Repeat in the X direction
        float vMax = height / tileHeight; // Repeat in the Y direction

        // Set color with alpha transparency
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), alphaMult);

        // Render the panel with tiling
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y);                       // Bottom-left
        GL11.glTexCoord2f(uMax, 0);
        GL11.glVertex2f(x + width, y);               // Bottom-right
        GL11.glTexCoord2f(uMax, vMax);
        GL11.glVertex2f(x + width, y + height);      // Top-right
        GL11.glTexCoord2f(0, vMax);
        GL11.glVertex2f(x, y + height);              // Top-left
        GL11.glEnd();

        // Reset color to fully opaque to avoid affecting other renders
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        // Disable blending and textures
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
}

