package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import data.kaysaar.aotd.vok.ui.basecomps.ResizableComponent;

import java.awt.*;

public class ResearchTreeConnector extends ResizableComponent {
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering","GlitchSquare");
    transient SpriteAPI spriteDiagonal = Global.getSettings().getSprite("ui","diagonal");

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
        spriteToRender.setAdditiveBlend();
        spriteDiagonal.setColor(Color.WHITE);
        spriteDiagonal.setColor(color);
        spriteDiagonal.setAlphaMult(alphaMult);
        spriteDiagonal.setAdditiveBlend();
        if(children.originalCoords.y!=parent.originalCoords.getY()){
            float distanceX = children.getComponentPanel().getPosition().getX()- (parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth());
            float centerXC = children.getComponentPanel().getPosition().getCenterY();
            float centerXP = parent.getComponentPanel().getPosition().getCenterY();
            float diff = centerXP - centerXC;
            diff = (float) Math.floor(diff);
            float defaultDistance = AoTDUiComp.SEPERATOR_OF_PANELS*scale;
            float reDistanced = distanceX - defaultDistance;
            int line = 9;
            spriteToRender.setSize(defaultDistance- line+1,1);
            spriteToRender.render(parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth(),parent.getComponentPanel().getPosition().getCenterY());
            spriteToRender.render(parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth(),parent.getComponentPanel().getPosition().getCenterY());

            if(diff>0){
                float x = parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth()+defaultDistance;
                spriteDiagonal.setAngle(270);
                spriteDiagonal.render(x,children.getComponentPanel().getPosition().getCenterY());
                spriteDiagonal.render(x,children.getComponentPanel().getPosition().getCenterY());

                spriteDiagonal.setAngle(90);
                spriteDiagonal.render(x-line,parent.getComponentPanel().getPosition().getCenterY()-9);
                spriteDiagonal.render(x-line,parent.getComponentPanel().getPosition().getCenterY()-9);

                spriteToRender.setSize(1,Math.abs(diff)- (line*2));
                spriteToRender.render(x,children.getComponentPanel().getPosition().getCenterY()+ line);
                spriteToRender.render(x,children.getComponentPanel().getPosition().getCenterY()+ line);

            }
            else{
                spriteToRender.setSize(1,Math.abs(diff)- (line*2));
                float x = parent.getComponentPanel().getPosition().getX()+parent.getComponentPanel().getPosition().getWidth()+defaultDistance;;
                spriteDiagonal.setAngle(0);
                spriteDiagonal.render(x-line,parent.getComponentPanel().getPosition().getCenterY());
                spriteDiagonal.render(x-line,parent.getComponentPanel().getPosition().getCenterY());

                spriteDiagonal.setAngle(180);
                spriteDiagonal.render(x,children.getComponentPanel().getPosition().getCenterY()-9);
                spriteDiagonal.render(x,children.getComponentPanel().getPosition().getCenterY()-9);

                spriteToRender.render(x,parent.getComponentPanel().getPosition().getCenterY()+ line);
                spriteToRender.render(x,parent.getComponentPanel().getPosition().getCenterY()+ line);

            }



            spriteToRender.setSize(reDistanced- line,1);
            spriteToRender.render(children.getComponentPanel().getPosition().getX()-reDistanced+line,children.getComponentPanel().getPosition().getCenterY());
            spriteToRender.render(children.getComponentPanel().getPosition().getX()-reDistanced+line,children.getComponentPanel().getPosition().getCenterY());

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
