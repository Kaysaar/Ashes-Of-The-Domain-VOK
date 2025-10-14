package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui.dialog;

import ashlib.data.plugins.ui.models.resizable.map.MapEntityComponent;
import ashlib.data.plugins.ui.models.resizable.map.MapEntityOnClickHook;
import ashlib.data.plugins.ui.models.resizable.map.MapMainComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import org.lwjgl.input.Keyboard;

public class BifrostMouseOnClick implements MapEntityOnClickHook {
    BifrostOrbitComponent component;
    public BifrostMouseOnClick(BifrostOrbitComponent component) {
        this.component = component;
    }
    @Override
    public void onClick(MapEntityComponent token, InputEventAPI event, CustomPanelAPI anchor, MapMainComponent component) {
        if(anchor.getPosition().containsEvent(event)){
            if(event.isLMBUpEvent()){
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1, 1);
                this.component.setCurrentlyCenteredAround(token);
                this.component.unlockFollow();
                this.component.getCurrentlyCenteredAround().setForceHighlight(true);
                event.consume();
                return;
            }


        }
        if(event.getEventValue()==Keyboard.KEY_D&&event.isKeyUpEvent()){
            Global.getSoundPlayer().playUISound("ui_button_pressed", 1, 1);
            if(this.component.getCurrentlyCenteredAround()!=null){
                this.component.getCurrentlyCenteredAround().setForceHighlight(false);
                this.component.unlockFollow();
            }
            this.component.setCurrentlyCenteredAround(null);
            event.consume();
        }


    }

    @Override
    public boolean shouldTrigger(InputEventAPI event) {
        return event.isLMBUpEvent()||event.getEventValue()== Keyboard.KEY_D;
    }
}
