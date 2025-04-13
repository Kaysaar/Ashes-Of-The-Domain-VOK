package data.kaysaar.aotd.vok.ui.basecomps.holograms;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import data.kaysaar.aotd.vok.ui.basecomps.ResizableComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class HologramViewer extends ResizableComponent {
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
    private float scanlineY = 0f;
    private float scanSpeed = 110; // Pixels per second (adjust as needed)

    public SpriteAPI lineTexture  = Global.getSettings().getSprite("ui","scanline_large");
    // Scaling factor for texture coordinates to increase line distance
    private float textureScale = 2.0f;  // Adjust this to control line spacing
    HologramViewerObjectRendererAPI renderer;
    public boolean renderLine = false;
    public HologramViewer(float width, float height, HologramViewerObjectRendererAPI renderer) {
        componentPanel = Global.getSettings().createCustom(width, height, this);
        setOverlayTexture(Global.getSettings().getSprite("ui", "shimmer"));
        this.renderer = renderer;
        renderer.init(componentPanel);
        setRenderLine(true);
        setTextureScale(1f);
    }

    public void setRenderLine(boolean renderLine) {
        this.renderLine = renderLine;
    }


    public void setColorOverlay(Color colorOverlay) {
        this.colorOverlay = colorOverlay;
    }

    public void setTextureScale(float scale) {
        this.textureScale = scale;
    }

    private boolean scanlineGoingUp = true;

    @Override
    public void advance(float amount) {
        super.advance(amount);

        if (cachedHeight == 0) updateCachedPositionAndSize();

        // Move the scanline
        if (scanlineGoingUp) {
            scanlineY -= scanSpeed * amount;
            if (scanlineY <= -15) {
                scanlineY = -15;
                scanlineGoingUp = false;
            }
        } else {
            scanlineY += scanSpeed * amount;
            if (scanlineY >= cachedHeight+10) {
                scanlineY = cachedHeight+10;
                scanlineGoingUp = true;
            }
        }

        render(curAlpha);
    }


    @Override
    public void render(float alphaMult) {
        this.curAlpha = alphaMult;

        if (componentPanel.getPosition().getWidth() == 0 || componentPanel.getPosition().getHeight() == 0) return;

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
        GL11.glStencilFunc(GL11.GL_ALWAYS, 3, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glColorMask(false, false, false, false);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        // Bind the overlay texture
        overlayTexture.bindTexture();
        overlayTexture.setAlphaMult(1.0f);

        // Calculate tile size and ensure consistent alignment
        float tileSize = 64 * textureScale;  // Scaled tile size for wider line spacing
        int tilesX = (int) Math.ceil(cachedWidth / tileSize);
        int tilesY = (int) Math.ceil(cachedHeight / tileSize);

        float xOffset = (cachedX % tileSize + tileSize) % tileSize;
        float yOffset = (cachedY % tileSize + tileSize) % tileSize;

        // Render the hologram texture on a unique stencil layer (2)
        GL11.glBegin(GL11.GL_QUADS);
        for (int x = -1; x < tilesX + 1; x++) {
            for (int y = -1; y < tilesY + 1; y++) {
                float posX = cachedX + x * tileSize - xOffset;
                float posY = cachedY + y * tileSize - yOffset;

                // Scale the texture coordinates for increased line spacing
                float texX1 = x / textureScale;
                float texY1 = y / textureScale;
                float texX2 = (x + 1) / textureScale;
                float texY2 = (y + 1) / textureScale;

                GL11.glTexCoord2f(texX1, texY1); GL11.glVertex2f(posX, posY);
                GL11.glTexCoord2f(texX2, texY1); GL11.glVertex2f(posX + tileSize, posY);
                GL11.glTexCoord2f(texX2, texY2); GL11.glVertex2f(posX + tileSize, posY + tileSize);
                GL11.glTexCoord2f(texX1, texY2); GL11.glVertex2f(posX, posY + tileSize);
            }
        }
        GL11.glEnd();

        // Now set the stencil function to use only the hologram layer (value 2)
        GL11.glColorMask(true, true, true, true);
        GL11.glStencilFunc(GL11.GL_EQUAL, 3, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        // Render the base image inside the hologram stencil
        renderer.render(alphaMult*0.9f,componentPanel.getPosition().getCenterX(),componentPanel.getPosition().getCenterY());
        if(renderLine){
            GL11.glStencilMask(0xFF);
            GL11.glStencilFunc(GL11.GL_ALWAYS, 4, 0xFF);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
            GL11.glColorMask(false, false, false, false);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);

            renderer.render(1,componentPanel.getPosition().getCenterX(),componentPanel.getPosition().getCenterY());

            // --- STEP 3: Draw scanline masked by stencil == 3 ---
            GL11.glColorMask(true, true, true, true);
            GL11.glStencilFunc(GL11.GL_EQUAL, 4, 0xFF);
            GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

            lineTexture.setAlphaMult(alphaMult);
            lineTexture.setColor(new Color(0, 255, 255, 200));
            lineTexture.setSize(cachedWidth,10);
            lineTexture.render(cachedX, cachedY + scanlineY);

        }

        // Restore the previous stencil state to ensure other components remain unaffected
        GL11.glPopAttrib();

        // Clean up OpenGL states
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }


    public void setOverlayTexture(SpriteAPI texture) {
        this.overlayTexture = texture;
    }

    private void updateCachedPositionAndSize() {
        cachedX = componentPanel.getPosition().getX();
        cachedY = componentPanel.getPosition().getY();
        cachedWidth = componentPanel.getPosition().getWidth();
        cachedHeight = componentPanel.getPosition().getHeight();
        cachedCenterX = componentPanel.getPosition().getCenterX();
        cachedCenterY = componentPanel.getPosition().getCenterY();
    }
    private boolean isPositionChanged() {
        float x = componentPanel.getPosition().getX();
        float y = componentPanel.getPosition().getY();
        float width = componentPanel.getPosition().getWidth();
        float height = componentPanel.getPosition().getHeight();
        float centerX = componentPanel.getPosition().getCenterX();
        float centerY = componentPanel.getPosition().getCenterY();

        return Math.abs(cachedX - x) > 0.5f || Math.abs(cachedY - y) > 0.5f ||
                Math.abs(cachedWidth - width) > 0.5f || Math.abs(cachedHeight - height) > 0.5f ||
                Math.abs(cachedCenterX - centerX) > 0.5f || Math.abs(cachedCenterY - centerY) > 0.5f;
    }

}
