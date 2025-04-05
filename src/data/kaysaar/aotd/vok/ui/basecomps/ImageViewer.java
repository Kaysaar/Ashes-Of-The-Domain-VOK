package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.ui.S;
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
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f); // Treat almost any alpha as solid

        spriteOfImage.setAlphaMult(alphaMult*0.3f);
        if(colorOverlay!=null){
            spriteOfImage.setColor(colorOverlay);
        }
        spriteOfImage.setSize(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight());
        spriteOfImage.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());

        // Step 2: Generate the dilated outer contour shape by expanding the solid area
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        // Writing the dilated shape to the stencil buffer
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        // Increase alpha acceptance to cover semi-transparent and slightly connected pixels
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.005f); // Lower threshold for tiny gaps
        GL11.glColorMask(false, false, false, false); // Disable color writing

        // Render the base image again to mark the stencil
        spriteOfImage.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());

        // Expand the stencil by drawing a slightly larger area to cover small gaps
        GL11.glPushMatrix();
        GL11.glTranslatef(-1, -1, 0); // Slightly shift the drawing position
        spriteOfImage.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(1, 1, 0); // Shift in opposite direction
        spriteOfImage.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());
        GL11.glPopMatrix();

        // Step 3: Render overlay texture within the dilated shape
        if (overlayTexture != null) {
            GL11.glColorMask(true, true, true, true);  // Re-enable color writing
            GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);   // Only render where stencil is marked
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            overlayTexture.bindTexture();
            overlayTexture.setColor(Color.cyan);
            overlayTexture.setAlphaMult(alphaMult * 0.7f); // Adjust overlay alpha
            overlayTexture.setSize(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight());
            overlayTexture.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());
        }

        // Clean up OpenGL states
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }



    public void setOverlayTexture(SpriteAPI texture) {
        this.overlayTexture = texture;
    }
}
