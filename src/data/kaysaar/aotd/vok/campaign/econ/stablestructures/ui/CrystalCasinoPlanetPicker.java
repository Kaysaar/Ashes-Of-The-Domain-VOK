package data.kaysaar.aotd.vok.campaign.econ.stablestructures.ui;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import ashlib.data.plugins.ui.models.resizable.map.MapEntityComponent;
import ashlib.data.plugins.ui.models.resizable.map.MapMainComponent;
import ashlib.data.plugins.ui.models.resizable.map.PlanetRenderResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.ui.starsystems.stuctures.map.StructureLocationPicker;
import data.kaysaar.aotd.vok.campaign.econ.stablestructures.impl.CrystalCasinoImpl;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class CrystalCasinoPlanetPicker extends MapMainComponent implements StructureLocationPicker {
    protected final CrystalCasinoImpl casino;
    protected final StarSystemAPI system;
    protected MapEntityComponent selectedPlanet;
    protected ArrayList<MapEntityComponent> eligiblePlanets = new ArrayList<>();
    protected CasinoPlanetIndicators indicators;
    protected boolean updateSelectionText = true;

    public CrystalCasinoPlanetPicker(float width, float height, StarSystemAPI system, CrystalCasinoImpl casino) {
        super(width, height, system);
        this.system = system;
        this.casino = casino;
        for (MapEntityComponent component : getMapZoom().getAllEntitiesComponents()) {
            if (!(component instanceof PlanetRenderResizableComponent)) continue;
            if (isValidPlanet(component.getToken())) eligiblePlanets.add(component);
        }
        if (!eligiblePlanets.isEmpty()) {
            indicators = new CasinoPlanetIndicators();
            getMapZoom().addComponent(indicators, WORLD_X, WORLD_X);
        }
    }

    public boolean isValidPlanet(SectorEntityToken token) {
        return token instanceof PlanetAPI && !token.isExpired() && token.getStarSystem() == system
                && casino.canBuildOnPlanet((PlanetAPI) token);
    }

    public boolean selectPlanet(MapEntityComponent candidate) {
        if (!eligiblePlanets.contains(candidate) || !isValidPlanet(candidate.getToken())) return false;
        if (selectedPlanet == candidate) return true;
        selectedPlanet = candidate;
        updateSelectionText = true;
        return true;
    }

    public void clearSelection() {
        if (selectedPlanet == null) return;
        selectedPlanet = null;
        updateSelectionText = true;
    }

    @Override
    public boolean hasSelection() {
        if (selectedPlanet != null && !isValidPlanet(selectedPlanet.getToken())) clearSelection();
        return selectedPlanet != null;
    }

    @Override
    public SectorEntityToken getSelectedEntity() {
        return hasSelection() ? selectedPlanet.getToken() : null;
    }

    @Override
    public Vector2f getSelectedLocation() {
        SectorEntityToken token = getSelectedEntity();
        return token instanceof PlanetAPI ? casino.getPlacementLocation((PlanetAPI) token) : null;
    }

    @Override
    public boolean consumeSelectionTextUpdate() {
        boolean value = updateSelectionText;
        updateSelectionText = false;
        return value;
    }

    @Override
    public void createSelectionText(TooltipMakerAPI tooltip) {
        if (eligiblePlanets.isEmpty()) {
            tooltip.addPara("No eligible player-owned habitable planets in this star system.",
                    Misc.getNegativeHighlightColor(), 0f);
        } else if (hasSelection()) {
            tooltip.addPara("Crystal Casino will orbit %s. Press %s to clear the selection.",
                    0f, Color.ORANGE, getSelectedEntity().getName(), "D");
        } else {
            tooltip.addPara("Select a highlighted habitable planet to construct Crystal Casino.", 0f);
        }
    }

    @Override
    protected void afterFinalize() {
        getPointer().setCanRender(false);
    }

    @Override
    public void onClick(Vector2f inUIWorldCoordinates, InputEventAPI event) {
        if (!event.isLMBUpEvent() || indicators == null) return;
        MapEntityComponent point = indicators.getPlanetAt(Global.getSettings().getMouseX(), Global.getSettings().getMouseY());
        if (!selectPlanet(point)) return;
        Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if (!event.isConsumed() && event.isKeyUpEvent() && event.getEventValue() == Keyboard.KEY_D && hasSelection()) {
                clearSelection();
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                event.consume();
            }
        }
        super.processInput(events);
    }

    @Override
    public void clearUI() {
        clearSelection();
        eligiblePlanets.clear();
        indicators = null;
        super.clearUI();
    }

    protected class CasinoPlanetIndicators extends ResizableComponent {
        protected float pulse;

        public CasinoPlanetIndicators() {
            componentPanel = Global.getSettings().createCustom(1f, 1f, this);
        }

        public float getHalfSize(MapEntityComponent component) {
            PositionAPI pos = component.getComponentPanel().getPosition();
            return Math.max(26f, Math.min(72f, Math.max(pos.getWidth(), pos.getHeight()) * 0.5f + 12f));
        }

        public MapEntityComponent getPlanetAt(float mouseX, float mouseY) {
            MapEntityComponent closest = null;
            float distance = Float.MAX_VALUE;
            for (MapEntityComponent component : eligiblePlanets) {
                PositionAPI pos = component.getComponentPanel().getPosition();
                float dx = mouseX - pos.getCenterX();
                float dy = mouseY - pos.getCenterY();
                float half = getHalfSize(component);
                if (Math.abs(dx) > half || Math.abs(dy) > half) continue;
                float d = dx * dx + dy * dy;
                if (d >= distance) continue;
                closest = component;
                distance = d;
            }
            return closest;
        }

        @Override
        public void advance(float amount) {
            pulse = (pulse + amount * 1.5f) % ((float) Math.PI * 2f);
        }

        @Override
        public void render(float alphaMult) {
            if (eligiblePlanets.isEmpty()) return;
            float brightness = 0.85f + (float) Math.sin(pulse) * 0.1f;
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                    | GL11.GL_LINE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_HINT_BIT);
            try {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                Color color = Misc.getBasePlayerColor();
                GL11.glLineWidth(1.5f);
                GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f,
                        color.getBlue() / 255f, brightness * alphaMult * color.getAlpha() / 255f);
                GL11.glBegin(GL11.GL_LINES);
                for (MapEntityComponent component : eligiblePlanets) {
                    if (component != selectedPlanet) drawCorners(component);
                }
                GL11.glEnd();
                if (selectedPlanet != null) {
                    color = Misc.getHighlightColor();
                    GL11.glLineWidth(2.2f);
                    GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f,
                            color.getBlue() / 255f, alphaMult * color.getAlpha() / 255f);
                    GL11.glBegin(GL11.GL_LINES);
                    drawCorners(selectedPlanet);
                    GL11.glEnd();
                }
            } finally {
                GL11.glPopAttrib();
            }
        }

        protected void drawCorners(MapEntityComponent component) {
            PositionAPI pos = component.getComponentPanel().getPosition();
            float x = pos.getCenterX();
            float y = pos.getCenterY();
            float half = getHalfSize(component);
            float corner = Math.min(16f, half * 0.42f);
            float left = x - half;
            float right = x + half;
            float top = y - half;
            float bottom = y + half;
            GL11.glVertex2f(left, top + corner);
            GL11.glVertex2f(left, top);
            GL11.glVertex2f(left, top);
            GL11.glVertex2f(left + corner, top);
            GL11.glVertex2f(right - corner, top);
            GL11.glVertex2f(right, top);
            GL11.glVertex2f(right, top);
            GL11.glVertex2f(right, top + corner);
            GL11.glVertex2f(left, bottom - corner);
            GL11.glVertex2f(left, bottom);
            GL11.glVertex2f(left, bottom);
            GL11.glVertex2f(left + corner, bottom);
            GL11.glVertex2f(right - corner, bottom);
            GL11.glVertex2f(right, bottom);
            GL11.glVertex2f(right, bottom);
            GL11.glVertex2f(right, bottom - corner);
        }
    }
}
