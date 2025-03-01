package data.kaysaar.aotd.vok.ui.customprod.components.gatheringpoint;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate;
import com.fs.starfarer.api.ui.CustomPanelAPI;

public class AoTDGatheringPointDelegate implements CustomVisualDialogDelegate {

    public AoTDGatehringPointPlugin plugin;
    public AoTDGatheringPointDelegate(AoTDGatehringPointPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void init(CustomPanelAPI panel, DialogCallbacks callbacks) {
       plugin.init(panel, callbacks);
    }

    @Override
    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return null;
    }

    @Override
    public float getNoiseAlpha() {
        return 0;
    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void reportDismissed(int option) {

    }
}
