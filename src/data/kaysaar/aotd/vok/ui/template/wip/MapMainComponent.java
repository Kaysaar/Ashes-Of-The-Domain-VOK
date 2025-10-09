package data.kaysaar.aotd.vok.ui.template.wip;

import ashlib.data.plugins.ui.models.resizable.ResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

public class MapMainComponent implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI contentPanel;
    MapZoomableComponent component;
    public MapMainComponent(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width,height,this);
        component = new MapZoomableComponent(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),54000,54000,1f);
        component.startStencil();
        component.endStencil();
        component.setCurrScale(component.minScale);
        StarSystemAPI corvus = Global.getSector().getStarSystem("Corvus");
        ResizableComponent leftTop,leftBottom,rightTop,rightBottom;
        leftTop = new MapCornerComponent();
        leftBottom = new MapCornerComponent();
        rightTop = new MapCornerComponent();
        rightBottom = new MapCornerComponent();
        component.addComponent(leftTop,0,0);
        component.addComponent(leftBottom,0,54000);
        component.addComponent(rightTop,54000,0);
        component.addComponent(rightBottom,54000,54000);

        MapGridRenderer renderer = new MapGridRenderer(leftTop,rightTop,leftBottom,rightBottom);
        component.addComponent(renderer,0,0);
        for (PlanetAPI planet : corvus.getPlanets()) {

            if(!planet.isStar()){
                OrbitAPI orbit = planet.getOrbit();
                MapOrbitRenderer rendererOrb = new MapOrbitRenderer(Misc.getDistance(planet.getLocation(),orbit.getFocus().getLocation()));
                Vector2f newLocation = translateCoordinatesToUI(orbit.getFocus().getLocation());
                component.addComponent(rendererOrb,newLocation.x,newLocation.y);
            }
        }

        for (PlanetAPI planet : corvus.getPlanets()) {
            PlanetRenderResizableComponent components = new PlanetRenderResizableComponent(planet);
            Vector2f newLocation = translateCoordinatesToUI(planet.getLocation());
            component.addComponent(components,newLocation.x,newLocation.y);


        }

        contentPanel = Global.getSettings().createCustom(width,height,null);
        contentPanel.addComponent(component.getPluginPanel()).inTL(0,0);
        mainPanel.addComponent(contentPanel).inTL(0,0);

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
