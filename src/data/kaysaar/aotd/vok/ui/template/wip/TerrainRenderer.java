package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
import com.fs.starfarer.combat.CombatViewport;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class TerrainRenderer extends ResizableComponent {
    CampaignTerrainAPI terrain;
    MapZoomableComponent zoom;

    public TerrainRenderer(CampaignTerrainAPI terrain,MapZoomableComponent component) {
        this.terrain = terrain;
        this.zoom = component;
        this.componentPanel = Global.getSettings().createCustom(1,1,this);

    }

    @Override
    public void render(float alphaMult) {
        float panelTLX = zoom.getPluginPanel().getPosition().getX();
        float panelTLY = zoom.getPluginPanel().getPosition().getY();
        float panelW   = zoom.getPluginPanel().getPosition().getWidth();
        float panelH   = zoom.getPluginPanel().getPosition().getHeight()+10;

// Convert to screen bottom-left for raw GL
        float screenH  = Global.getSettings().getScreenHeight();
        float panelBLX = panelTLX;
        float panelBLY = screenH - (panelTLY + panelH);

// UI target in screen coords (here: panel center)
        float sx =  componentPanel.getPosition().getX();
        float sy = componentPanel.getPosition().getY();

// World location that the method will translate to internally
        Vector2f loc = terrain.getLocation();
        float wx = loc.x;
        float wy = loc.y;

// Pixels-per-world scaling (your zoom)
        float px = zoom.getData().scale;  // <= same scale you use for markers/grid

// Optional: clip to panel
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_SCISSOR_BIT);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(Math.round(panelBLX), Math.round(panelBLY), Math.round(panelW), Math.round(panelH));

// --- Matrix sandwich ---
        GL11.glPushMatrix();
        GL11.glTranslatef(sx, sy, 0f);     // place at UI target
        GL11.glScalef(px, px, 1f);         // world units -> pixels
        CombatViewport viewport = new CombatViewport(componentPanel.getPosition().getX(),componentPanel.getPosition().getY(),0,0);
        viewport.setAlphaMult(alphaMult);
        viewport.setEverythingNearViewport(true);
// Call the unmodifiable method exactly as-is:
        for (CampaignEngineLayers value : CampaignEngineLayers.values()) {
            ReflectionUtilis.invokeMethodWithAutoProjection("render",terrain,value,viewport);
        }

// --- cleanup ---
        GL11.glPopMatrix();
        GL11.glPopAttrib();;
    }
}

