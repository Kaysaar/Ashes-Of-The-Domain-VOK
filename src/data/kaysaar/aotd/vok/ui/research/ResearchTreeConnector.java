package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import data.kaysaar.aotd.vok.ui.basecomps.ResizableComponent;

import java.awt.*;

public class ResearchTreeConnector extends ResizableComponent {
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering","GlitchSquare");
    transient ResizableComponent parent;
    transient ResizableComponent children;
    Color color;
    public ResearchTreeConnector( float height,ResizableComponent parent, ResizableComponent children,Color color) {
        componentPanel = Global.getSettings().createCustom(1, height, this);
        this.parent = parent;
        this.children = children;
        this.color = color;

    }

    @Override
    public void renderBelow(float alphaMult) {
        super.renderBelow(alphaMult);
        spriteToRender.setColor(Color.WHITE);
        spriteToRender.setColor(color);
        spriteToRender.setAlphaMult(alphaMult);
        if(children.originalCoords.y!=parent.originalCoords.getY()){
            float distanceX = children.getComponentPanel().getPosition().getX()- (parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth());
            float centerXC = children.getComponentPanel().getPosition().getCenterY();
            float centerXP = parent.getComponentPanel().getPosition().getCenterY();
            float diff = centerXP - centerXC;
            diff = (float) Math.floor(diff);
            float defaultDistance = AoTDUiComp.SEPERATOR_OF_PANELS*scale;
            float reDistanced = distanceX - defaultDistance;
            spriteToRender.setSize(defaultDistance,1);
            spriteToRender.render(parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth(),parent.getComponentPanel().getPosition().getCenterY());
            spriteToRender.render(parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth(),parent.getComponentPanel().getPosition().getCenterY());

            if(diff>0){
                spriteToRender.setSize(1,Math.abs(diff));
                spriteToRender.render(parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth()+defaultDistance,children.getComponentPanel().getPosition().getCenterY());
                spriteToRender.render(parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth()+defaultDistance,children.getComponentPanel().getPosition().getCenterY());


            }
            else{
                spriteToRender.setSize(1,Math.abs(diff));
                spriteToRender.render(parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth()+defaultDistance,parent.getComponentPanel().getPosition().getCenterY());
                spriteToRender.render(parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth()+defaultDistance,parent.getComponentPanel().getPosition().getCenterY());

            }


            spriteToRender.setSize(reDistanced,1);


            spriteToRender.render(children.getComponentPanel().getPosition().getX()-reDistanced,children.getComponentPanel().getPosition().getCenterY());
            spriteToRender.render(children.getComponentPanel().getPosition().getX()-reDistanced,children.getComponentPanel().getPosition().getCenterY());

        }
        else{
            float distance = children.getComponentPanel().getPosition().getX()- (parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth());
            spriteToRender.setSize(distance,1);
            spriteToRender.render(parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth(),parent.getComponentPanel().getPosition().getCenterY());
            spriteToRender.render(parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth(),parent.getComponentPanel().getPosition().getCenterY());

        }


    }

    @Override
    public void render(float alphaMult) {
        super.render(alphaMult);

    }
}
