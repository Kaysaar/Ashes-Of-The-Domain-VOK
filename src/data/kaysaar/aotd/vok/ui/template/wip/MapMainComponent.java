package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.TrapezoidButtonDetector;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class MapMainComponent implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI contentPanel;
    MapZoomableComponent component;
    public float sleep = 0f;
    MouseFollowerComponent follower;
    TrapezoidButtonDetector detector = new TrapezoidButtonDetector();
    MapCornerComponent leftTop, leftBottom, rightTop, rightBottom, center;
    boolean wasRenderingEveryting = false;

    public MapCornerComponent getLeftBottom() {
        return leftBottom;
    }

    public MapCornerComponent getLeftTop() {
        return leftTop;
    }

    public MapCornerComponent getRightBottom() {
        return rightBottom;
    }

    public MapCornerComponent getRightTop() {
        return rightTop;
    }

    private static final Logger log = Global.getLogger(MapMainComponent.class);

    public MapMainComponent(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        component = new MapZoomableComponent(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), 54000, 54000, 1f);
        component.startStencil();
        component.endStencil();

        component.setCurrScale(component.minScale);
        StarSystemAPI corvus = Global.getSector().getPlayerFleet().getStarSystem();



        leftTop = new MapCornerComponent();
        leftBottom = new MapCornerComponent();
        rightTop = new MapCornerComponent();
        rightBottom = new MapCornerComponent();
        center = new MapCornerComponent();
        component.addComponent(leftTop, 0, 0);
        component.addComponent(leftBottom, 0, 54000);
        component.addComponent(rightTop, 54000, 0);
        component.addComponent(rightBottom, 54000, 54000);
        component.addComponent(center, 27000, 27000);
        MapGridRenderer gridRender = new MapGridRenderer(leftTop, rightTop, leftBottom, rightBottom, center);
        component.addComponent(gridRender, 0, 0);
        ArrayList<AsteroidAPI>asteroids = new ArrayList<>();
        for (SectorEntityToken token : corvus.getAllEntities()) {
            if (token instanceof PlanetAPI planet) {
                OrbitAPI orbit = planet.getOrbit();
                if (orbit != null) {
                    MapOrbitRenderer rendererOrb = new MapOrbitRenderer(Misc.getDistance(planet.getLocation(), orbit.getFocus().getLocation()));
                    Vector2f newLocation = translateCoordinatesToUI(orbit.getFocus().getLocation());
                    component.addComponent(rendererOrb, newLocation.x, newLocation.y);
                    rendererOrb.scale = component.currScale;
                }
            }
            if(token instanceof AsteroidAPI asteroid) {
                asteroids.add(asteroid);
            }


        }

        for (SectorEntityToken token : corvus.getAllEntities()) {
            if (token instanceof PlanetAPI planet) {
                PlanetRenderResizableComponent components = new PlanetRenderResizableComponent(planet);
                TooltipMakerAPI tooltip = components.getTooltipOnHoverPanel().createUIElement(1, 1, false);
                tooltip.addTooltipTo(new TooltipMakerAPI.TooltipCreator() {
                    @Override
                    public boolean isTooltipExpandable(Object tooltipParam) {
                        return false;
                    }

                    @Override
                    public float getTooltipWidth(Object tooltipParam) {
                        return 400;
                    }

                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        tooltip.addPara(planet.getName() + ", " + planet.getTypeNameWithWorldLowerCase(), planet.getSpec().getIconColor(), 2f);
                    }
                }, components.getTooltipOnHoverPanel(), TooltipMakerAPI.TooltipLocation.RIGHT);
                Vector2f newLocation = translateCoordinatesToUI(planet.getLocation());
                component.addComponent(components, newLocation.x - (planet.getRadius()), newLocation.y - (planet.getRadius()));
                components.scale = component.currScale;
            }
            if(token instanceof CampaignTerrainAPI ring){
                TerrainRenderV2 renderer = new TerrainRenderV2(ring,component);
                Vector2f newLocation = translateCoordinatesToUI(ring.getLocation());
                component.addComponent(renderer, 27000,27000);

            }
//            if(token instanceof CampaignTerrainAPI ring){
//                TerrainRenderer renderer = new TerrainRenderer(ring,component);
//                Vector2f newLocation = translateCoordinatesToUI(ring.getLocation());
//                component.addComponent(renderer,27000, 27000);
//
//            }

        }
        for (AsteroidAPI asteroid : asteroids) {
            AsteroidRenderer renderer = new AsteroidRenderer(asteroid);
            Vector2f newLocation = translateCoordinatesToUI(asteroid.getLocation());
            component.addComponent(renderer,newLocation.x,newLocation.y);
        }
        for (CustomCampaignEntityAPI token : corvus.getCustomEntities()) {
            if (token.getFleetForVisual() != null && token.hasTag(Tags.STATION)) {
                CampaignFleetAPI fleet = token.getFleetForVisual();
                FleetMemberAPI member = fleet.getFleetData().getMembersListCopy().stream().filter(FleetMemberAPI::isStation).findFirst().orElse(null);
                StationRendererComponent component1 = new StationRendererComponent(member.getVariant(),token.getRadius(),token.getFleetForVisual().getFacing());
                Vector2f newLocation = translateCoordinatesToUI(token.getLocation());
                TooltipMakerAPI tooltip = component1.getTooltipOnHoverPanel().createUIElement(1, 1, false);
                tooltip.addTooltipTo(new TooltipMakerAPI.TooltipCreator() {
                    @Override
                    public boolean isTooltipExpandable(Object tooltipParam) {
                        return false;
                    }

                    @Override
                    public float getTooltipWidth(Object tooltipParam) {
                        return 400;
                    }

                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        tooltip.addPara("Station", 2f);
                    }
                }, component1.getTooltipOnHoverPanel(), TooltipMakerAPI.TooltipLocation.RIGHT);
                component.addComponent(component1, newLocation.x - (token.getRadius()), newLocation.y - (token.getRadius()));
                component1.scale = component.currScale;
            }
            else if(token.getFleetForVisual()==null){
                CustomEntitySpecAPI spec = token.getCustomEntitySpec();

                StationRendererComponent component1 = new StationRendererComponent(spec.getSpriteName(),spec.getSpriteWidth(),spec.getSpriteHeight(),token.getRadius(),token.getFacing());
                Vector2f newLocation = translateCoordinatesToUI(token.getLocation());
                TooltipMakerAPI tooltip = component1.getTooltipOnHoverPanel().createUIElement(1, 1, false);
                component.addComponent(component1, newLocation.x - (token.getRadius()), newLocation.y - (token.getRadius()));
                component1.scale = component.currScale;
            }



        }

        follower = new MouseFollowerComponent();
        follower.scale = component.currScale;
        component.addComponent(follower, 0, 0);

        MapPointerComponent pointer = new MapPointerComponent(follower, this);
        pointer.scale = component.currScale;
        component.addComponent(pointer, 0, 0);
        pointer.setShouldRender(true);
        contentPanel = Global.getSettings().createCustom(width, height, null);
        contentPanel.addComponent(component.getPluginPanel()).inTL(0, 0);
        mainPanel.addComponent(contentPanel).inTL(0, 0);
        component.centerOnWorld(27000, 27000);
    }

    public void addGridRendering() {

    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {

    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {
        if (doesHover()) {
            Vector2f vector = component.calculateMouseToWorldCords();
            Vector2f acutallVecotr = new Vector2f(vector.x, Math.abs(vector.y));
            follower.updatePositionOfPanel(acutallVecotr);
            if (sleep >= 3) {
                sleep = 0;
                log.info("Position in UI : " + follower.getComponentPanel().getPosition().getCenterX() + "," + follower.getComponentPanel().getPosition().getCenterY());
            }
        }
        sleep += amount;

    }

    public boolean doesHover() {
        float xLeft = mainPanel.getPosition().getX();
        float xRight = mainPanel.getPosition().getX() + mainPanel.getPosition().getWidth();
        float yBot = mainPanel.getPosition().getY();
        float yTop = mainPanel.getPosition().getY() + mainPanel.getPosition().getHeight();
        return detector.determineIfHoversOverButton(xLeft, yTop, xRight, yTop, xLeft, yBot, xRight, yBot, Global.getSettings().getMouseX(), Global.getSettings().getMouseY());
    }

    @Override
    public void processInput(List<InputEventAPI> events) {


    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    public Vector2f translateCoordinatesToUI(Vector2f worldLocation) {
        float uiX = worldLocation.x + 27000f;   // shift X so -27000 → 0, +27000 → 54000
        float uiY = 27000f - worldLocation.y;   // invert Y so +27000 → 0 (top), -27000 → 54000 (bottom)
        return new Vector2f(uiX, uiY);
    }

}
