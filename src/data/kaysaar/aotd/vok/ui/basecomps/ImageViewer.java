package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ImageViewer extends ResizableComponent {
        public SpriteAPI spriteOfImage;
        public Color colorOverlay;
        public ImageViewer(float width, float height,String imagePath) {
            componentPanel = Global.getSettings().createCustom(width, height, this);
            spriteOfImage = Global.getSettings().getSprite(imagePath);

        }

        public void setColorOverlay(Color colorOverlay) {
            this.colorOverlay = colorOverlay;
        }

        @Override
        public void render(float alphaMult) {
            super.render(alphaMult);

            if(colorOverlay != null) {
                spriteOfImage.setColor(colorOverlay);
            }
            spriteOfImage.setAlphaMult(alphaMult);
            spriteOfImage.setSize(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight());
            spriteOfImage.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());
        }
    }

