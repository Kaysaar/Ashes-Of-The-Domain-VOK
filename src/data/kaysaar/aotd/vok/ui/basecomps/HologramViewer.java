package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class HologramViewer extends ResizableComponent {
    public SpriteAPI spriteOfImage;
    public SpriteAPI overlayTexture;
    public Color colorOverlay;
    public float curAlpha = 0f;
    // Cached position and size
    private float cachedX = -1;
    private float cachedY = -1;
    private float cachedCenterX = -1;
    private float cachedCenterY = -1;
    private float cachedWidth = -1;
    private float cachedHeight = -1;
    private boolean requiresUpdate = true;

    public HologramViewer(float width, float height, String imagePath) {
        componentPanel = Global.getSettings().createCustom(width, height, this);
        spriteOfImage = Global.getSettings().getSprite(imagePath);
        setOverlayTexture(Global.getSettings().getSprite("rendering", "test"));
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
        this.curAlpha = 1;

        if (componentPanel.getPosition().getWidth() == 0 || componentPanel.getPosition().getHeight() == 0) return;
        if (overlayTexture == null || spriteOfImage == null) return;

        // Calculate position and size only if necessary
        if (requiresUpdate || isPositionChanged()) {
            updateCachedPositionAndSize();
            requiresUpdate = false;
        }

        // Save the current stencil state to isolate the hologram stencil usage
        GL11.glPushAttrib(GL11.GL_STENCIL_BUFFER_BIT);

        // Enable blending for the hologram effect
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Enable stencil testing without affecting existing stencil setup
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);

        // Step 1: Set up stencil for writing the hologram mask (unique value 2)
        GL11.glStencilFunc(GL11.GL_ALWAYS, 2, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.05f);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        // Bind the overlay texture
        overlayTexture.bindTexture();
        overlayTexture.setAlphaMult(1.0f);

        // Render the hologram texture on a unique stencil layer (2)
        GL11.glBegin(GL11.GL_QUADS);
        float tileSize = 64;
        int tilesX = (int) Math.ceil(cachedWidth / tileSize);
        int tilesY = (int) Math.ceil(cachedHeight / tileSize);

        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                float posX = cachedX + x * tileSize;
                float posY = cachedY + y * tileSize;

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
        spriteOfImage.setAlphaMult(curAlpha);
        spriteOfImage.setSize(cachedWidth, cachedHeight);
        spriteOfImage.renderAtCenter(cachedCenterX, cachedCenterY);

        // Restore the previous stencil state to ensure other components remain unaffected
        GL11.glPopAttrib();

        // Clean up OpenGL states
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private boolean isPositionChanged() {
        float x = componentPanel.getPosition().getX();
        float y = componentPanel.getPosition().getY();
        float width = componentPanel.getPosition().getWidth();
        float height = componentPanel.getPosition().getHeight();
        float centerX = componentPanel.getPosition().getCenterX();
        float centerY = componentPanel.getPosition().getCenterY();

        // Detect drastic change
        return Math.abs(cachedX - x) > 0.5f || Math.abs(cachedY - y) > 0.5f ||
                Math.abs(cachedWidth - width) > 0.5f || Math.abs(cachedHeight - height) > 0.5f ||
                Math.abs(cachedCenterX - centerX) > 0.5f || Math.abs(cachedCenterY - centerY) > 0.5f;
    }

    private void updateCachedPositionAndSize() {
        cachedX = componentPanel.getPosition().getX();
        cachedY = componentPanel.getPosition().getY();
        cachedWidth = componentPanel.getPosition().getWidth();
        cachedHeight = componentPanel.getPosition().getHeight();
        cachedCenterX = componentPanel.getPosition().getCenterX();
        cachedCenterY = componentPanel.getPosition().getCenterY();
    }

    public void setOverlayTexture(SpriteAPI texture) {
        this.overlayTexture = texture;
    }
}
