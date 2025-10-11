package data.kaysaar.aotd.vok.ui.template;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.CampaignInputListener;
import com.fs.starfarer.api.input.InputEventAPI;
import data.kaysaar.aotd.vok.ui.template.wip.MapMainComponent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class TemplateDialogEveryFrameScript implements CampaignInputListener {
    @Override
    public int getListenerInputPriority() {
        return 1000;
    }

    @Override
    public void processCampaignInputPreCore(List<InputEventAPI> events) {
        events.stream().filter(x->!x.isConsumed()).forEach(x->{
            if(x.getEventValue()== Keyboard.KEY_T&&x.isKeyDownEvent()&& Global.getSettings().isDevMode()){
                x.consume();
                // specify your ExtendedUI

                new TemplateDialog("Test",new MapMainComponent(1000,600));
            }
        });
    }

    @Override
    public void processCampaignInputPreFleetControl(List<InputEventAPI> events) {

    }

    @Override
    public void processCampaignInputPostCore(List<InputEventAPI> events) {

    }
}
