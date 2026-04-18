package data.kaysaar.aotd.vok.ui.basecomps.holograms;

import ashlib.data.plugins.ui.models.resizable.map.PlanetRenderResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.entities.terrain.Planet;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class EntityHologram implements HologramViewerObjectRendererAPI{
    SectorEntityToken token ;
    SpriteAPI spriteOfEntity;
    PlanetAPI planet;
    float maxRadius;
    float widthSprite,heightSprite;
    public EntityHologram(SectorEntityToken token,float width,float height){

        this.token = token;
        if(token instanceof PlanetAPI pl){
             maxRadius = Math.min(width/2,height/2);
            planet = pl;
        }
        else {
            if(token.getCustomEntitySpec()==null)return;
            spriteOfEntity = Global.getSettings().getSprite(token.getCustomEntitySpec().getSpriteName());

            float widthOrg = token.getCustomEntitySpec().getSpriteWidth();
            float heightOrg = token.getCustomEntitySpec().getSpriteHeight();


            // Scale sprite to fit within given bounds while preserving aspect ratio
            float scaleX = width / widthOrg;
            if(token.getCustomEntitySpec()!=null&&token.getCustomEntitySpec().getId().equals("coronal_tap")){
                scaleX = width/150;
            }
            float scaleY = height / heightOrg;
            float scale = Math.min(scaleX, scaleY);

            widthSprite = widthOrg * scale;
            heightSprite = heightOrg * scale;

        }
    }
    @Override
    public void init(CustomPanelAPI panelOfRendering) {

    }
    @Override
    public void renderMask(float centerX, float centerY) {

    }
    @Override
    public void render(float alphaMult, float centerX, float centeryY) {
        Color color = Misc.interpolateColor(Color.cyan,Misc.getBasePlayerColor(),0.55f);
        if(planet!=null){
            PlanetRenderResizableComponent.renderPlanet(planet,planet.getTypeId(),new Vector2f(centerX,centeryY),maxRadius,0,0,0f,0f,alphaMult,false,37f,1f,color);
        }
        else{
            spriteOfEntity.setSize(widthSprite,heightSprite);
            spriteOfEntity.setColor(color);
            spriteOfEntity.setAlphaMult(alphaMult);
            spriteOfEntity.renderAtCenter(centerX,centeryY);
        }

    }

    @Override
    public void setColor(Color color) {

    }
}
