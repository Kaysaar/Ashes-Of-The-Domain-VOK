package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui.dialog;


import ashlib.data.plugins.ui.models.resizable.map.MapEntityComponent;
import ashlib.data.plugins.ui.models.resizable.map.MapMainComponent;
import ashlib.data.plugins.ui.models.resizable.map.PlanetRenderResizableComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import org.lwjgl.util.vector.Vector2f;

public class ChooseOrbitForBifrost extends MapMainComponent {
    BifrostOrbitComponent orbitComponent;

    public ChooseOrbitForBifrost(float width, float height, StarSystemAPI systemAPI) {
        super(width, height,systemAPI);
    }

    @Override
    protected void afterAddOrbits() {
        orbitComponent = new BifrostOrbitComponent(0f,this);
        getMapZoom().addComponent(orbitComponent,27000,27000);

    }

    @Override
    protected void afterAddPlanets() {
        for (MapEntityComponent allEntitiesComponent : getMapZoom().getAllEntitiesComponents()) {
            if(allEntitiesComponent instanceof PlanetRenderResizableComponent comp) {
                comp.addHook(new BifrostMouseOnClick(orbitComponent),"bifrost");
            }
        }
    }

    @Override
    public void onClick(Vector2f inUIWorldCoordinates, InputEventAPI event) {
        if(event.isLMBUpEvent()){
            if(orbitComponent.getCurrentlyCenteredAround()!=null){
                if(orbitComponent.isLocked()){
                    orbitComponent.unlockFollow();
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1, 1);

                }
                else{
                    orbitComponent.lockToCurrentFollower();
                    Global.getSoundPlayer().playUISound("ui_button_pressed", 1, 1);

                }
            }
        }
    }

    @Override
    protected void afterFinalize() {
        getPointer().setCanRender(false);
    }

    public BifrostOrbitComponent getOrbitComponent() {
        return orbitComponent;
    }
}
