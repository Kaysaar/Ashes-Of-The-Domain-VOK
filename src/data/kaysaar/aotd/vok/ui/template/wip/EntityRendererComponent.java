package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class EntityRendererComponent extends ResizableComponent {
    String stationSpec;
    ShipVariantAPI viewMember;

    /** base visual radius for the orbit ring (in px, at scale=1). default: use panel radius */
    public float orbitRadiusPx = -1f;
    /** appearance */
    public Color orbitColor = new Color(126, 126, 126, 180);
    public float orbitLineWidth = 1f;
    public float facing ;
    /** panel radius you passed in (used if orbitRadiusPx < 0) */
    float radius;
    SpriteAPI sprite;
    float originalWidth,originalHeight;

    public EntityRendererComponent(String stationSpec, float radius) {
        this.stationSpec = stationSpec;
        this.radius = radius;
        componentPanel = Global.getSettings().createCustom(radius * 2f, radius * 2f, this);
    }
    public EntityRendererComponent(String spriteName, float width, float height, float radius, float facing) {
         sprite = Global.getSettings().getSprite(spriteName);
        this.facing = facing;
        this.originalWidth = width;
        this.originalHeight = height;
        this.radius = radius;
        componentPanel = Global.getSettings().createCustom(radius * 2f, radius * 2f, this);
    }
    public EntityRendererComponent(ShipVariantAPI var, float radius, float facing) {
        this.viewMember = var;
        this.radius = radius;
        componentPanel = Global.getSettings().createCustom(radius * 2f, radius * 2f, this);
        this.facing= facing;
    }

    @Override
    public void render(float alphaMult) {
        Vector2f center = new Vector2f(
                componentPanel.getPosition().getCenterX(),
                componentPanel.getPosition().getCenterY()
        );

        if (viewMember == null&&sprite==null) {
            renderStationSprite(stationSpec, alphaMult, center, scale);
        } else  if(sprite==null){
            renderStationSprite(viewMember, alphaMult, center, scale,facing,radius);
        }
        else{
            float var5 = radius * scale;
            sprite.setSize(originalWidth*scale, originalHeight*scale);
            sprite.setAngle(facing-90);
            sprite.renderAtCenter(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY());
        }

        // ---- draw orbit ring in screen space (outside any GL scale used for the sprite) ----
        float baseR = radius;
        float orbitR = baseR * scale; // respect zoom from ResizableComponent
        drawOrbit(center, orbitR, orbitColor, orbitLineWidth, alphaMult);
    }

    // --- station rendering (your existing logic, unchanged except naming) ---

    public static void renderStationSprite(String stationSpec, float alpha, Vector2f point, float scale) {
        CustomPanelAPI p1 = Global.getSettings().createCustom(0, 0, new BaseCustomUIPanelPlugin() {
            @Override
            public void render(float alphaMult) {
                CampaignFleetAPI fleet = Global.getFactory().createEmptyFleet(Factions.PLAYER, "", true);
                fleet.setStationMode(true);
                FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, stationSpec);
                fleet.getFleetData().addFleetMember(member);
                fleet.getLocation().set(point);
                Object view = ReflectionUtilis.invokeMethodWithAutoProjection("getFleetView", fleet);
                Object viewMember = ReflectionUtilis.invokeMethodWithAutoProjection("createItemView", view, member);

                GL11.glPushMatrix();
                // no extra GL scale here; render at given point
                ReflectionUtilis.invokeMethodWithAutoProjection("renderSingle", viewMember, point.x, point.y, 0f, null, alphaMult, 1f);
                GL11.glPopMatrix();
            }
        });
        p1.getPosition().setLocation(point.getX(), point.getY());
        p1.render(alpha);
    }

    public static void renderStationSprite(ShipVariantAPI var, float alpha, Vector2f point, float scale,float facing,float radius) {
        CustomPanelAPI p1 = Global.getSettings().createCustom(0, 0, new BaseCustomUIPanelPlugin() {
            @Override
            public void render(float alphaMult) {
                CampaignFleetAPI fleet = Global.getFactory().createEmptyFleet(Factions.PLAYER, "", true);
                FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, var);
                fleet.setFacing(facing);
                fleet.getFleetData().addFleetMember(member);
                fleet.getLocation().set(point);

                Object view = ReflectionUtilis.invokeMethodWithAutoProjection("getFleetView", fleet);
                Object viewMember = ReflectionUtilis.invokeMethodWithAutoProjection("createItemView", view, member);
                ReflectionUtilis.invokeMethodWithAutoProjection("setFacing",viewMember,facing-90f);
                GL11.glPushMatrix();
                // scale the sprite if you want it a bit larger; adjust coordinates accordingly

                float outScale = scale ;
                GL11.glScalef(outScale, outScale, 1.0F);
                ReflectionUtilis.invokeMethodWithAutoProjection("renderSingle", viewMember, point.x / outScale, point.y / outScale, 0f, null, alphaMult, 1f);
                GL11.glPopMatrix();
            }
        });
        p1.getPosition().setLocation(point.getX(), point.getY());
        p1.render(alpha);
    }

    // --- OpenGL orbit ring ---

    private static void drawOrbit(Vector2f center, float radiusPx, Color color, float lineWidth, float alpha) {
        if (radiusPx <= 0f) return;

        final int segments = Math.max(32, Math.min(512, (int) (2 * Math.PI * radiusPx / 4f)));

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LINE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(Math.max(1f, lineWidth));

            float a = (color.getAlpha() / 255f) * alpha;
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, a);

            GL11.glBegin(GL11.GL_LINE_LOOP);
            for (int i = 0; i < segments; i++) {
                double t = i * (2.0 * Math.PI / segments);
                float x = center.x + (float) Math.cos(t) * radiusPx;
                float y = center.y + (float) Math.sin(t) * radiusPx;
                GL11.glVertex2f(x, y);
            }
            GL11.glEnd();
        } finally {
            GL11.glPopAttrib();
        }
    }
}
