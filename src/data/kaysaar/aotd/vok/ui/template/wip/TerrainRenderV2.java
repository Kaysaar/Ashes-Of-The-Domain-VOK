    package data.kaysaar.aotd.vok.ui.template.wip;

    import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
    import com.fs.starfarer.api.Global;
    import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
    import com.fs.starfarer.api.ui.PositionAPI;
    import org.lwjgl.opengl.GL11;
    import org.lwjgl.util.vector.Vector2f;

    import java.awt.*;

    public class TerrainRenderV2 extends ResizableComponent {
        private final CampaignTerrainAPI terrain;
        private Color color = new Color(129, 122, 99, 255); // default, can be set from ring.getColor()
        private  final MapZoomableComponent zoom;
        public TerrainRenderV2(CampaignTerrainAPI ring,MapZoomableComponent zoom) {
            this.terrain = ring;
            this.zoom = zoom;
            this.componentPanel = Global.getSettings().createCustom(1, 1, this);

        }

        public void setColor(Color c) { if (c != null) color = c; }

        @Override
        public void render(float alphaMult) {
            // panel geometry
            PositionAPI pos   = zoom.getPluginPanel().getPosition();
            float pTLX = pos.getX(), pTLY = pos.getY(), pW = pos.getWidth(), pH = pos.getHeight()+10;
            float screenH = Global.getSettings().getScreenHeight();
            float pBLX = pTLX, pBLY = screenH - (pTLY + pH);

            // terrain world center
            float wx = terrain.getLocation().x;
            float wy = terrain.getLocation().y;

            // world -> panel-local TL
            Vector2f ui = zoom.calculateUICords(wx, wy);
            // panel-local TL -> screen BL
            float sx = pBLX + ui.x;
            float sy = pBLY + (pH - ui.y);

            // pixels-per-world
            float factor = Math.max(1e-6f, zoom.getData().scale);

            // clip to panel (optional)
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_SCISSOR_BIT);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(Math.round(pBLX), Math.round(pBLY), Math.round(pW), Math.round(pH));

            // ---- place the tiled terrain so its world center appears at (sx, sy) ----
            GL11.glPushMatrix();
            // renderOnMap multiplies *factor* into all vertices; cancel the world origin
            // and align entity center to (sx, sy):
            GL11.glTranslatef(componentPanel.getPosition().getCenterX(), componentPanel.getPosition().getCenterY(), 0f);
            // draw whole terrain in UI
            terrain.getPlugin().renderOnMap(factor, alphaMult);

            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }



    }
