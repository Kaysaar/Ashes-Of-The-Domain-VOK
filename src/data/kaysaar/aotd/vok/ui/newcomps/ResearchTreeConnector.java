package data.kaysaar.aotd.vok.ui.newcomps;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import data.kaysaar.aotd.vok.ui.newcomps.basecomponents.ResizableComponent;

import java.awt.*;

public class ResearchTreeConnector extends ResizableComponent {
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering","GlitchSquare");
    transient ResearchPanelComponent parent;
    transient ResearchPanelComponent children;
    Color color;
    public ResearchTreeConnector( float height,ResearchPanelComponent parent, ResearchPanelComponent children,Color color) {
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
        if(children.originalCoords.y!=parent.originalCoords.getY()){
            float distanceX = children.getPanelOfButton().getPosition().getX()- (parent.getPanelOfButton().getPosition().getX()+parent.getPanelOfButton().getPosition().getWidth());
            float centerXC = children.getPanelOfButton().getPosition().getY()+(children.getPanelOfButton().getPosition().getHeight()/2);
            float centerXP = parent.getPanelOfButton().getPosition().getY()+(parent.getPanelOfButton().getPosition().getHeight()/2);
            float diff = centerXP - centerXC;
            float defaultDistance = AoTDUiComp.SEPERATOR_OF_PANELS*scale;
            float reDistanced = distanceX - defaultDistance;
            spriteToRender.setSize(defaultDistance,1);
            spriteToRender.render(parent.getPanelOfButton().getPosition().getX()+parent.getPanelOfButton().getPosition().getWidth(),parent.getPanelOfButton().getPosition().getCenterY());
            spriteToRender.render(parent.getPanelOfButton().getPosition().getX()+parent.getPanelOfButton().getPosition().getWidth(),parent.getPanelOfButton().getPosition().getCenterY());

            if(diff>0){
                spriteToRender.setSize(1,Math.abs(diff));
                spriteToRender.render(parent.getPanelOfButton().getPosition().getX()+parent.getPanelOfButton().getPosition().getWidth()+defaultDistance,children.getPanelOfButton().getPosition().getCenterY());
                spriteToRender.render(parent.getPanelOfButton().getPosition().getX()+parent.getPanelOfButton().getPosition().getWidth()+defaultDistance,children.getPanelOfButton().getPosition().getCenterY());


            }
            else{
                spriteToRender.setSize(1,Math.abs(diff));
                spriteToRender.render(parent.getPanelOfButton().getPosition().getX()+parent.getPanelOfButton().getPosition().getWidth()+defaultDistance,parent.getPanelOfButton().getPosition().getCenterY());
                spriteToRender.render(parent.getPanelOfButton().getPosition().getX()+parent.getPanelOfButton().getPosition().getWidth()+defaultDistance,parent.getPanelOfButton().getPosition().getCenterY());

            }


            spriteToRender.setSize(reDistanced,1);


            spriteToRender.render(children.getPanelOfButton().getPosition().getX()-reDistanced,children.getPanelOfButton().getPosition().getCenterY());
            spriteToRender.render(children.getPanelOfButton().getPosition().getX()-reDistanced,children.getPanelOfButton().getPosition().getCenterY());

        }
        else{
            float distance = children.getPanelOfButton().getPosition().getX()- (parent.getPanelOfButton().getPosition().getX()+parent.getPanelOfButton().getPosition().getWidth());
            spriteToRender.setSize(distance,1);
            spriteToRender.render(parent.getPanelOfButton().getPosition().getX()+parent.getPanelOfButton().getPosition().getWidth(),parent.getPanelOfButton().getPosition().getCenterY());
            spriteToRender.render(parent.getPanelOfButton().getPosition().getX()+parent.getPanelOfButton().getPosition().getWidth(),parent.getPanelOfButton().getPosition().getCenterY());

        }


    }

    @Override
    public void render(float alphaMult) {
        super.render(alphaMult);

    }
}
