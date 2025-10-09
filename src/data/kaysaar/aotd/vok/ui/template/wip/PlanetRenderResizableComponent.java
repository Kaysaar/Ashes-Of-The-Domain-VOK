package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.combat.CombatViewport;
import com.fs.starfarer.combat.entities.terrain.Planet;
import org.lwjgl.util.vector.Vector2f;

public class PlanetRenderResizableComponent extends ResizableComponent {
    String planetType;
    float originalSize;
    boolean isStar;
    float angle = 0f;
    PlanetAPI planet;
    public static void renderPlanet(String spec, Vector2f point, float size, float facing, float pitch, float surfaceAngle, float atmoAngle, float alpha,boolean isStar ){
        CustomPanelAPI p1 = Global.getSettings().createCustom(0,0, new BaseCustomUIPanelPlugin(){
            @Override
            public void render(float alphaMult) {
                CombatViewport vv = new CombatViewport(point.x,point.y, 0, 0);
                vv.setAlphaMult(alpha);
                Planet planet = new Planet(spec, size, 0, point);
                planet.setAngle(surfaceAngle);
                planet.setCloudAngle(atmoAngle);
                planet.getSpec().setAtmosphereThickness(0f);
                planet.setTilt(facing-90f);
                //planet.renderSphere((CombatViewport) viewport);
                planet.renderSphere(vv);
                if(isStar){
                    planet.renderStarGlow(vv);
                }

            }
        });
        p1.getPosition().setLocation(point.getX(), point.getY());
        p1.render(alpha);
    }
    public PlanetRenderResizableComponent(float size,String type,boolean isStar){
        this.originalSize = size;
        this.planetType = type;
        this.isStar = isStar;
        this.componentPanel = Global.getSettings().createCustom(1,1,this);
    }
    public PlanetRenderResizableComponent(PlanetAPI reference){
        this.originalSize = reference.getRadius();
        this.planet = reference;
        this.isStar = reference.isStar();
        this.componentPanel = Global.getSettings().createCustom(1,1,this);
    }

    @Override
    public void render(float alphaMult) {
        super.render(alphaMult);
        if(planet==null){
            renderPlanet(planetType,new Vector2f(componentPanel.getPosition().getCenterX(),componentPanel.getPosition().getCenterY()),originalSize*scale,90f,2f,angle,0f,alphaMult,isStar);

        }
        else{
            renderPlanet(planet.getTypeId(),new Vector2f(componentPanel.getPosition().getCenterX(),componentPanel.getPosition().getCenterY()),originalSize*scale,planet.getFacing(),planet.getSpec().getPitch(),0f,0f,alphaMult,isStar);

        }

    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        angle+=0.2f;
        if(angle>=360){
            angle =0f;
        }
    }
}
