package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ImageViewer extends ResizableComponent {
    public SpriteAPI spriteOfImage;
    public SpriteAPI overlayTexture;
    public Color colorOverlay;
    public ImageViewer(float width, float height,String imagePath) {
        componentPanel = Global.getSettings().createCustom(width, height, this);
        spriteOfImage = Global.getSettings().getSprite(imagePath);
        setOverlayTexture(Global.getSettings().getSprite("rendering","test"));

    }

    public void setColorOverlay(Color colorOverlay) {
        this.colorOverlay = colorOverlay;
    }

    @Override
    public void render(float alphaMult) {
        super.render(alphaMult);

        if (spriteOfImage == null) return;

        // Step 1: Render the base image normally
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f); // Discard almost fully transparent pixels

        spriteOfImage.setAlphaMult(alphaMult);
        spriteOfImage.setSize(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight());
        spriteOfImage.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());

        // Step 2: Overlay the texture where the base image is not transparent
        if (overlayTexture != null) {
            // Set blending mode for the overlay
            GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // Bind and render the overlay texture
            overlayTexture.bindTexture();
            overlayTexture.setSize(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight());
            overlayTexture.setAlphaMult(alphaMult * 0.5f); // Adjust alpha for the overlay if needed
            overlayTexture.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());
        }

        // Clean up OpenGL states
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }
    public void setOverlayTexture(SpriteAPI texture) {
        this.overlayTexture = texture;
    }
}
