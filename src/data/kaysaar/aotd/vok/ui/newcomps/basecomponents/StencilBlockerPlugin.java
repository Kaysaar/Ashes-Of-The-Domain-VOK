package data.kaysaar.aotd.vok.ui.newcomps.basecomponents;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.List;

public class StencilBlockerPlugin implements CustomUIPanelPlugin {
    CustomPanelAPI panelToStencil;
    public StencilBlockerPlugin(CustomPanelAPI panelToStencil) {
        this.panelToStencil = panelToStencil;
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        AoTDMisc.startStencil(panelToStencil,1f);
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
}
