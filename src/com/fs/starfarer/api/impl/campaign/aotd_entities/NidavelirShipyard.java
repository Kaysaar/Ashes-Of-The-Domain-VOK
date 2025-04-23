package com.fs.starfarer.api.impl.campaign.aotd_entities;

import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.CombatViewport;
import com.fs.starfarer.combat.entities.terrain.Planet;
import com.fs.starfarer.loading.specs.PlanetSpec;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.LinkedHashMap;

public class NidavelirShipyard extends BaseCustomEntityPlugin {

    protected Planet graphics;
    protected LinkedHashMap<Planet,String>layers = new LinkedHashMap<>();
    public float elapsed;
    public float seconds;
    public boolean isVanising = false;
    public boolean isAppearing =false;
    float currAlpha = 1f;
    public float waitingTime = 0f;

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(tiedToPlanet!=null){
//            graphics.advance(amount);
//            graphics.getLocation().set(tiedToPlanet.getLocation());
            for (Planet layer : layers.keySet()) {
                layer.setSpec(getSpec(layers.get(layer)));
                layer.advance(amount);
                layer.getLocation().set(tiedToPlanet.getLocation());

            }
        }
        if(isVanising&&!isAppearing){
            elapsed += amount;
            currAlpha = 1-elapsed/seconds;
        }
        else{
            currAlpha =1;
        }
        if(isAppearing){
            elapsed += amount;
            currAlpha  = 0f;
            if(elapsed>waitingTime){
                currAlpha = (elapsed-waitingTime)/seconds;
                if(currAlpha>=1){
                    currAlpha = 1;
                    isAppearing = false;
                }
            }

        }



    }
    public PlanetSpec getSpec(String id ){
        for (PlanetSpecAPI allPlanetSpec : Global.getSettings().getAllPlanetSpecs()) {
            if(allPlanetSpec.getPlanetType().equals(id)){
                return (PlanetSpec) allPlanetSpec;
            }
        }
        return null;
    }

    @Override
    public void init(SectorEntityToken entity, Object pluginParams) {
        super.init(entity, pluginParams);
    }
    public SectorEntityToken getEntity(){
        return this.entity;
    }
    PlanetAPI tiedToPlanet;
    public void reinitRendering(String type,String shadowType,boolean isDestroyed){
        layers.clear();
        this.entity.setLocation(tiedToPlanet.getLocation().x,tiedToPlanet.getLocation().y);
        float angle = (float)Math.random() * 360.0F;
        if(!isDestroyed){
            Planet renderer = new Planet(shadowType,tiedToPlanet.getRadius(),0.0f,new Vector2f());
            renderer.setAngle(angle);
            renderer.setTilt(tiedToPlanet.getSpec().getTilt());
            layers.put(renderer,shadowType);

//            Planet rendererOfShip1 = new Planet("aotd_nidavelir_inner_bottom",tiedToPlanet.getRadius()+34,0.0f,new Vector2f());
//            Planet rendererOfShip2 = new Planet("aotd_nidavelir_inner_top",tiedToPlanet.getRadius()+34,0.0f,new Vector2f());
//            Planet rendererOfShip3 = new Planet("aotd_nidavelir_outer_bottom",tiedToPlanet.getRadius()+35,0.0f,new Vector2f());
//            Planet rendererOfShip4 = new Planet("aotd_nidavelir_outer_top",tiedToPlanet.getRadius()+35,0.0f,new Vector2f());
//
//            rendererOfShip1.setAngle(angle);
//            rendererOfShip1.setTilt(tiedToPlanet.getSpec().getTilt());
//
//            rendererOfShip2.setAngle(angle);
//            rendererOfShip2.setTilt(tiedToPlanet.getSpec().getTilt());
//
//            rendererOfShip3.setAngle(angle);
//            rendererOfShip3.setTilt(tiedToPlanet.getSpec().getTilt());
//
//            rendererOfShip4.setAngle(angle);
//            rendererOfShip4.setTilt(tiedToPlanet.getSpec().getTilt());



//            layers.add(rendererOfShip1);
//            layers.add(rendererOfShip2);
//            layers.add(rendererOfShip3);
//            layers.add(rendererOfShip4);
            for (int i = 0; i < 3; i++) {
                Planet rendererText = new Planet(type,tiedToPlanet.getRadius()+35-0.5f*i,0.0f,new Vector2f());
                rendererText.setAngle(angle);
                rendererText.setTilt(tiedToPlanet.getSpec().getTilt());
                layers.put(rendererText,type);
            }
        }
        else{
            for (int i = 0; i < 3; i++) {
                Planet rendererText = new Planet(type,tiedToPlanet.getRadius()+35-0.5f*i,0.0f,new Vector2f());
                rendererText.setAngle(angle);
                rendererText.setTilt(tiedToPlanet.getSpec().getTilt());
                layers.put(rendererText,type);
            }
        }



    }
    public  void trueInit(String type,String shadowType, PlanetAPI planetTied){
        tiedToPlanet = planetTied;
        reinitRendering(type,shadowType,false);
        seconds = 1;
        isAppearing=true;
        elapsed = 0;
//        graphics = new Planet(type,planetTied.getRadius()+25f,0.0f,new Vector2f());


    }

    @Override
    public float getRenderRange() {
        return  1000000f;
    }

    @Override
    public void render(CampaignEngineLayers layer, ViewportAPI viewport) {
        float prevAlpha = viewport.getAlphaMult();
        viewport.setAlphaMult(currAlpha);
        if(tiedToPlanet!=null){
            if (this.entity.getLightSource() != null) {
                Vector2f var4 = this.entity.getLightSource().getLocation();
                float distance = Misc.getDistance(this.entity.getLocation(),var4);
                float zDimBrightness = 1.0F;
                Fader lightHeight = (Fader)  ReflectionUtilis.getPrivateVariableFromSuperClass("lightHeight",entity.getContainingLocation());
                if (this.entity.getContainingLocation() != null && lightHeight != null) {
                    float brightness = lightHeight.getBrightness();
                    zDimBrightness = 1.5F * brightness - 0.5F;
                }

                Color lightColor = this.entity.getLightColor();
                if (this.entity.getLightSource().hasTag("ambient_ls")) {
                    for (Planet layerRing : layers.keySet()) {
                        layerRing.setLightSourceLocation(this.entity.getLocation().x, this.entity.getLocation().y, this.entity.getRadius() * 2.0F);
                    }
//                        this.graphics.setLightSourceLocation(this.entity.getLocation().x, this.entity.getLocation().y, this.entity.getRadius() * 2.0F);
                } else {
                    for (Planet layerRing : layers.keySet()) {
                        layerRing.setLightSourceLocation(var4.x, var4.y, distance * 0.75F * zDimBrightness);
                    }
//                        this.graphics.setLightSourceLocation(var4.x, var4.y, distance * 0.75F * zDimBrightness);
                }
                for (Planet planet : layers.keySet()) {
                    planet.setLightColorOverride(lightColor);
                }

//                    this.graphics.setLightColorOverride(lightColor);

            } else {
                for (Planet planet : layers.keySet()) {
                    planet.resetLightLocation();;
                    planet.setLightColorOverride((Color)null);
                }
//                    this.graphics.resetLightLocation();
//                    this.graphics.setLightColorOverride((Color)null);
            }
            startStencil(tiedToPlanet.getRadius()+31f,tiedToPlanet.getLocation(),360);
            if (layer == CampaignEngineLayers.TERRAIN_3) {
                for (Planet planet : layers.keySet()) {
                    planet.renderSphere((CombatViewport) viewport);
                }
                endStencil();
                for (Planet planet : layers.keySet()) {
                    planet.renderStarGlow((CombatViewport) viewport);
                    break;
                }

//                    this.graphics.renderSphere((CombatViewport) viewport);
//                    this.graphics.renderStarGlow((CombatViewport) viewport);
            }


        }
        viewport.setAlphaMult(prevAlpha);
        // Apply glitch effect

    }
    public static void startStencil(float radius, Vector2f location, int circlePoints) {
        GL11.glClearStencil(0);
        GL11.glStencilMask(0xff);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glColorMask(false, false, false, false);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xff);
        GL11.glStencilMask(0xff);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

        GL11.glBegin(GL11.GL_POLYGON);

        float x = location.x;
        float y = location.y;

        for (int i = 0; i <= circlePoints; i++) {
            double angle = (2 * Math.PI * i / circlePoints);
            double vertX = Math.cos(angle) * radius;
            double vertY = Math.sin(angle) * radius;
            GL11.glVertex2d(x + vertX, y + vertY);
        }

        GL11.glEnd();

        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
    }
    public static void endStencil() {
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

}
