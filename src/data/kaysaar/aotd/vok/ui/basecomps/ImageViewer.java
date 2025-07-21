package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;

import java.awt.*;

public class ImageViewer extends ResizableComponent {
        public SpriteAPI spriteOfImage;
        public Color colorOverlay;
        public float angle;

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public ImageViewer(float width, float height, String imagePath) {
            componentPanel = Global.getSettings().createCustom(width, height, this);
            spriteOfImage = Global.getSettings().getSprite(imagePath);
            angle = 0;

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
            spriteOfImage.setAngle(angle);
            spriteOfImage.setSize(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight());
            spriteOfImage.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());
        }
    }

