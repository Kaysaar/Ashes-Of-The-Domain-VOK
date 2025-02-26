package data.kaysaar.aotd.vok.ui.newcomps.basecomponents;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;

public class ImageViewer extends ResizableComponent {
    public SpriteAPI spriteOfImage;

    public ImageViewer(float width, float height,String imagePath) {
        componentPanel = Global.getSettings().createCustom(width, height, this);
        spriteOfImage = Global.getSettings().getSprite(imagePath);

    }
    @Override
    public void render(float alphaMult) {
        super.render(alphaMult);
        spriteOfImage.setAlphaMult(alphaMult);
        spriteOfImage.setSize(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight());
        spriteOfImage.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());
    }
}
