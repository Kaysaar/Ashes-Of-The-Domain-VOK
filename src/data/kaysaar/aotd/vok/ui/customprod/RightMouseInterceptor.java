package data.kaysaar.aotd.vok.ui.customprod;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.List;

public class RightMouseInterceptor implements CustomUIPanelPlugin {
    public boolean isInterceptingRightMouseEvent = false;
    CustomPanelAPI panelPos;

    public void setPanelPos(CustomPanelAPI panelPos) {
        this.panelPos = panelPos;
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
        if(panelPos!=null&&panelPos.getPosition()!=null){
            if(AoTDMisc.isHoveringOverButton(panelPos,0f)){
                boolean isConsuming = false;
                for (InputEventAPI event : events) {
                    if(event.isConsumed())continue;
                    if(event.isRMBDownEvent()){
                        event.consume();
                        isConsuming = true;
                    }
                }
                isInterceptingRightMouseEvent = isConsuming;
            }

        }
        else {
            isInterceptingRightMouseEvent = false;
        }

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
