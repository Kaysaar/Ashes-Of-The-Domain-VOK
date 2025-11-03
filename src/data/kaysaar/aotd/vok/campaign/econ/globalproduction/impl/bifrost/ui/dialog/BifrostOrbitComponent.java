package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui.dialog;

import ashlib.data.plugins.ui.models.resizable.map.MapEntityComponent;
import ashlib.data.plugins.ui.models.resizable.map.MapMainComponent;
import ashlib.data.plugins.ui.models.resizable.map.MapOrbitRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;



public class BifrostOrbitComponent extends MapOrbitRenderer {

    // --- Inputs / context ---
    private MapEntityComponent currentlyCenteredAround;
    private final MapMainComponent mainComponent;

    // --- Gate sprite ---
    private final SpriteAPI gateSprite;
    private final float spriteW, spriteH;


    private float spriteAngleOffsetDeg = 0f;

    // --- Follow lock state ---
    private boolean followLocked = false;
    private Float savedGateAngleRad = null;

    private Float savedRadiusWorld  = null;

    public BifrostOrbitComponent(float radius, MapMainComponent mainComponent) {
        super(radius);
        this.mainComponent = mainComponent;
        this.color = new Color(255, 255, 255);

        String specId = "bifrost_gate"; // your custom entity spec id
        this.gateSprite = Global.getSettings().getSprite(
                Global.getSettings().getCustomEntitySpec(specId).getSpriteName()
        );
        this.spriteW = Global.getSettings().getCustomEntitySpec(specId).getSpriteWidth();
        this.spriteH = Global.getSettings().getCustomEntitySpec(specId).getSpriteHeight();
    }

    // ------------------------------------------------------------------------
    // Configuration
    // ------------------------------------------------------------------------

    public MapEntityComponent getCurrentlyCenteredAround() {
        return currentlyCenteredAround;
    }

    public void setCurrentlyCenteredAround(MapEntityComponent center) {
        this.currentlyCenteredAround = center;
    }

    /** If your sprite's "forward" isn't +X, adjust with a small offset (degrees). */
    public void setSpriteAngleOffsetDeg(float spriteAngleOffsetDeg) {
        this.spriteAngleOffsetDeg = spriteAngleOffsetDeg;
    }

    public boolean isLocked() {
        return followLocked;
    }

    public void toggleLock() {
        if (followLocked) unlockFollow();
        else lockToCurrentFollower();
    }

    public Float getSavedGateAngleRad() {
        return savedGateAngleRad;
    }

    public Float getSavedRadiusWorld() {
        return savedRadiusWorld;
    }
    public Vector2f savedCordsOfGate;

    public Vector2f getSavedCordsOfGate() {
        return savedCordsOfGate;
    }

    /** Freeze gate at current follower position (stores angle in UI-space, radius in world-space). */
    public void lockToCurrentFollower() {
        if (currentlyCenteredAround == null) return;

        float cxUI = currentlyCenteredAround.getComponentPanel().getPosition().getCenterX();
        float cyUI = currentlyCenteredAround.getComponentPanel().getPosition().getCenterY();
        float fxUI = mainComponent.getFollower().getComponentPanel().getPosition().getCenterX();
        float fyUI = mainComponent.getFollower().getComponentPanel().getPosition().getCenterY();

        // Angle in UI space (no Y flip)
        savedGateAngleRad = (float) Math.atan2(fyUI - cyUI, fxUI - cxUI);

        // Keep radius in WORLD units so it’s robust to zoom changes
        float radiusUI = Misc.getDistance(new Vector2f(cxUI, cyUI), new Vector2f(fxUI, fyUI));
        savedRadiusWorld = radiusUI / Math.max(1e-6f, scale);
        savedCordsOfGate = mainComponent.getMapZoom().calculateMouseToWorldCords();
        followLocked = true;
    }

    /** Resume following the follower/mouse. */
    public void unlockFollow() {
        followLocked = false;
        savedGateAngleRad = null;
        savedRadiusWorld  = null;
        savedCordsOfGate = null;
    }

    // ------------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------------

    @Override
    public void render(float alphaMult) {
        if (currentlyCenteredAround == null) return;

        // --- Orbit center (UI) ---
        float cxUI = currentlyCenteredAround.getComponentPanel().getPosition().getCenterX();
        float cyUI = currentlyCenteredAround.getComponentPanel().getPosition().getCenterY();

        // --- Compute orbit radius in UI ---
        float radiusUI;
        if (followLocked && savedRadiusWorld != null) {
            radiusUI = savedRadiusWorld * scale;
        } else {
            float fxUI = mainComponent.getFollower().getComponentPanel().getPosition().getCenterX();
            float fyUI = mainComponent.getFollower().getComponentPanel().getPosition().getCenterY();
            radiusUI = Misc.getDistance(new Vector2f(cxUI, cyUI), new Vector2f(fxUI, fyUI));
        }

        // Clamp for planets/stars like your original logic
        if (currentlyCenteredAround.getToken() instanceof PlanetAPI planet) {
            if (!planet.isStar() && !planet.isBlackHole()) {
                radiusUI = Math.max((currentlyCenteredAround.getToken().getRadius() * 1.5f) * scale, radiusUI);
                radiusUI = Math.min((currentlyCenteredAround.getToken().getRadius() * 3.0f) * scale, radiusUI);
            } else {
                radiusUI = Math.max((currentlyCenteredAround.getToken().getRadius() * 2.0f) * scale, radiusUI);
                radiusUI = Math.min(25000f * scale, radiusUI);
            }
        }
        if (radiusUI <= 0f) return;

        // --- Orbit line (UI) ---
        float circumference = (float) (2f * Math.PI * radiusUI);
        int segments = Math.max(32, Math.min(512, (int) (circumference / 4f)));

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LINE_BIT);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            float lw = Math.max(1f, lineWidth * scale);
            GL11.glLineWidth(lw);

            float a = (color.getAlpha() / 255f) * alphaMult;
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, a);

            GL11.glBegin(GL11.GL_LINE_LOOP);
            for (int i = 0; i < segments; i++) {
                float t = (float) (i * (2 * Math.PI / segments));
                float x = cxUI + (float) Math.cos(t) * radiusUI;
                float y = cyUI + (float) Math.sin(t) * radiusUI;
                GL11.glVertex2f(x, y);
            }
            GL11.glEnd();
        } finally {
            GL11.glPopAttrib();
        }

        // --- Gate placement (UI) ---
        // Use UI-space angle so "down" in UI produces a gate drawn at the bottom.
        float angleRadToUse;
        if (followLocked && savedGateAngleRad != null) {
            angleRadToUse = savedGateAngleRad;          // already UI-space
        } else {
            float fxUI = mainComponent.getFollower().getComponentPanel().getPosition().getCenterX();
            float fyUI = mainComponent.getFollower().getComponentPanel().getPosition().getCenterY();
            angleRadToUse = (float) Math.atan2(fyUI - cyUI, fxUI - cxUI); // UI space
        }

        float cos = (float) Math.cos(angleRadToUse);
        float sin = (float) Math.sin(angleRadToUse);
        float px  = cxUI + cos * radiusUI;
        float py  = cyUI + sin * radiusUI;

        // Face center:
        float faceCenterDeg = (float) Math.toDegrees(angleRadToUse) + 180f + spriteAngleOffsetDeg;
        if(isLocked()){
            savedRadiusWorld = radiusUI/scale;
            gateSprite.setSize(spriteW * scale, spriteH * scale);
            gateSprite.setColor(Color.cyan);
            gateSprite.setAlphaMult(0.9f * alphaMult);
            gateSprite.setAngle(faceCenterDeg);
            gateSprite.renderAtCenter(px, py);

        }
        else{
            MapEntityComponent.drawFilledCircle(new Vector2f(px,py),55*scale,new Color(118, 255, 144),alphaMult);
        }

    }
}
