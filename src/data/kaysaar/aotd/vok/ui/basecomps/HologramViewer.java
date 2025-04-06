package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class HologramViewer extends ResizableComponent {
    public SpriteAPI spriteOfImage;
    public SpriteAPI overlayTexture;
    public Color colorOverlay;
    public float curAlpha= 0f;
    public HologramViewer(float width, float height, String imagePath) {
        componentPanel = Global.getSettings().createCustom(width, height, this);
        spriteOfImage = Global.getSettings().getSprite(imagePath);
        setOverlayTexture(Global.getSettings().getSprite("rendering","test"));

    }

    public void setColorOverlay(Color colorOverlay) {
        this.colorOverlay = colorOverlay;
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        render(curAlpha);
    }

    @Override
    public void render(float alphaMult) {
        this.curAlpha = alphaMult;

        if (overlayTexture == null || spriteOfImage == null) return;

        // Enable blending for the hologram effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Enable and clear the stencil buffer for masking (without clearing the entire buffer)
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);  // Allow writing to all bits
        GL11.glClearStencil(0);     // Default clear value
        // Only clear stencil values corresponding to the hologram layer (e.g., value 2)
        GL11.glStencilFunc(GL11.GL_ALWAYS, 2, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.05f);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        // Bind the overlay texture
        overlayTexture.bindTexture();
        overlayTexture.setAlphaMult(1.0f);

        // Calculate component position and size
        float xComp = componentPanel.getPosition().getX();
        float yComp = componentPanel.getPosition().getY();
        float xCompCenter = componentPanel.getPosition().getCenterX();
        float yCompCenter = componentPanel.getPosition().getCenterY();
        float width = componentPanel.getPosition().getWidth();
        float height = componentPanel.getPosition().getHeight();
        float tileSize = 64;

        int tilesX = (int) Math.ceil(width / tileSize);
        int tilesY = (int) Math.ceil(height / tileSize);

        // Render the hologram texture on a unique stencil layer (2)
        GL11.glBegin(GL11.GL_QUADS);
        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                float posX = xComp + x * tileSize;
                float posY = yComp + y * tileSize;

                GL11.glTexCoord2f(0, 0); GL11.glVertex2f(posX, posY);
                GL11.glTexCoord2f(1, 0); GL11.glVertex2f(posX + tileSize, posY);
                GL11.glTexCoord2f(1, 1); GL11.glVertex2f(posX + tileSize, posY + tileSize);
                GL11.glTexCoord2f(0, 1); GL11.glVertex2f(posX, posY + tileSize);
            }
        }
        GL11.glEnd();

        // Now set the stencil function to use only the hologram layer (value 2)
        GL11.glColorMask(true, true, true, true);
        GL11.glStencilFunc(GL11.GL_EQUAL, 2, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        // Render the base image inside the hologram stencil
        spriteOfImage.setColor(Color.cyan);
        spriteOfImage.setAlphaMult(alphaMult);
        spriteOfImage.setSize(width, height);
        spriteOfImage.renderAtCenter(xCompCenter, yCompCenter);

        // Clean up OpenGL states
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }







    public void setOverlayTexture(SpriteAPI texture) {
        this.overlayTexture = texture;
    }
}
