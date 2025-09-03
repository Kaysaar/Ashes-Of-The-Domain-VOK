package data.kaysaar.aotd.vok.scripts.coreui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.campaign.CampaignPlanet;
import com.fs.starfarer.combat.CombatViewport;
import com.fs.starfarer.combat.entities.terrain.Planet;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static com.fs.starfarer.api.impl.campaign.aotd_entities.NidavelirShipyard.endStencil;
import static com.fs.starfarer.api.impl.campaign.aotd_entities.NidavelirShipyard.startStencil;


public class BackgroundInterlooper implements ExtendedUIPanelPlugin {
    ArrayList<Planet> planets;
    public CustomPanelAPI mainPanel;
    public CampaignPlanet campaignPlanet;
    public UIPanelAPI reference;
    public boolean doesHaveShadowOnStart = false;
    Planet shadow;
    float scale =1f;
    float currRadius = 0f;
    float currAngle = 0f;
    public BackgroundInterlooper(String planetType, float width, float height, CampaignPlanet reference2, UIPanelAPI reference,float angle) {
        planets = new ArrayList<>();
        if(planetType.equals("aotd_nidavelir")){
            shadow = new Planet("aotd_nidavelir_shadow", reference2.getRadius() , 0.0f, new Vector2f());
            shadow.setTilt(reference2.getGraphics().getTilt());
            shadow.setAngle(angle);
            currAngle = shadow.getAngle();
        }
        for (int i = 0; i < 3; i++) {
            Planet planet = new Planet(planetType, reference2.getRadius() + 35 - (0.5f * i), 0.0f, new Vector2f());
            planet.setTilt(reference2.getGraphics().getTilt());
            planet.setAngle(angle);
            planets.add(planet);

        }


        mainPanel = Global.getSettings().createCustom(width, height, this);
        this.reference = reference;
        this.campaignPlanet = reference2;

    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        if (ReflectionUtilis.invokeMethod("getPlanetBackground", ProductionUtil.getCoreUI()) == null) return;

        if (reference != null) {
            PositionAPI refPos = reference.getPosition();
            float refX = refPos.getX();
            float refY = refPos.getY();
            float refCenterX = refPos.getCenterX();
            float refCenterY = refPos.getCenterY();
            float refWidth = refPos.getWidth();
            float refHeight = refPos.getHeight();

            boolean isRightSide = Global.getSettings().optBoolean("planetBGRightSide", false);

            // Base scale radius from panel dimensions
            float scaledRadius = Math.max(refWidth, refHeight) * 0.5f;
            if (isRightSide) {
                scaledRadius *= 0.67f;
            }

            // Adjust radius based on campaign planet size
            float sizeFactor = (campaignPlanet.getRadius() + 100.0f) / 300.0f;
            sizeFactor = Math.max(0.5f, Math.min(sizeFactor, 1.25f));

            scaledRadius *= sizeFactor;
            scaledRadius = Math.min(scaledRadius, (campaignPlanet.getRadius()) * 2.0f + 100.0f);

            float maxAllowedRadius = refWidth / 3.0f;
            if (scaledRadius > maxAllowedRadius) {
                scaledRadius = maxAllowedRadius;
            }

            CombatViewport sphereViewport = new CombatViewport(refX, refY, refWidth, refHeight);
            CombatViewport shadowViewport = new CombatViewport(refX, refY, refWidth, refHeight);

            sphereViewport.setAlphaMult(alphaMult);
            shadowViewport.setAlphaMult(Math.min(alphaMult, 0.4f));

            currRadius = scaledRadius;
            scale = scaledRadius / campaignPlanet.getRadius();
            float trueRadius = (campaignPlanet.getRadius() + 35) * scale;

            // Stencil setup
            Vector2f centerPos = isRightSide
                    ? new Vector2f(refCenterX + refWidth / 3.0f, refCenterY)
                    : new Vector2f(refCenterX, refCenterY);
            startStencil(trueRadius - 3, centerPos, 360, 2);

            // Draw shadow
            if (shadow != null) {
                shadow.getLocation().set(centerPos.x, centerPos.y);
                shadow.setRadius(currRadius);
                shadow.renderSphere(shadowViewport);
                shadow.renderStarGlow(shadowViewport);
            }

            // Draw planets
            int planetIndex = 0;
            for (Planet planet : planets) {
                planet.getLocation().set(centerPos.x, centerPos.y);
                planet.setLightSourceLocation(centerPos.x, centerPos.y + scaledRadius * 10.0f, scaledRadius * 30.0f);
                planet.setRadius(trueRadius - (0.5f * planetIndex));
                planet.renderSphere(sphereViewport);
                planetIndex++;
            }

            endStencil();
        }
    }


    @Override
    public void advance(float amount) {
        float am =amount*0.33f;
        for (Planet planet : planets) {
            planet.advance(am);
        }
        if(shadow!=null){
            float radSpeed = planets.get(0).getRadius()/shadow.getRadius();
            shadow.getSpec().setRotation(planets.get(0).getSpec().getRotation());
            shadow.advance(am*radSpeed);
        }

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {

    }
    public static float getSmallCircleRotation(float thetaBig, float radiusBig, float radiusSmall) {
        return thetaBig * (radiusSmall / radiusBig);
    }



}
