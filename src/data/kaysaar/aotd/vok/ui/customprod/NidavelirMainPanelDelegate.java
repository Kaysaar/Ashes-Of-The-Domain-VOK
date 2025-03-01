package data.kaysaar.aotd.vok.ui.customprod;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;

public class NidavelirMainPanelDelegate implements CustomVisualDialogDelegate {
    protected DialogCallbacks callbacks;
    protected NidavelirMainPanelPlugin plugin;
    protected InteractionDialogAPI dialog;
    public NidavelirMainPanelDelegate(NidavelirMainPanelPlugin panel , InteractionDialogAPI dialog){
        this.plugin = panel;
        this.dialog = dialog;
    }
    @Override
    public void init(CustomPanelAPI panel, DialogCallbacks callbacks) {
        this.callbacks = callbacks;
        plugin.init(panel, callbacks, dialog);

    }

    @Override
    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return plugin;
    }

    @Override
    public float getNoiseAlpha() {
        return 0.5f;
    }

    @Override
    public void advance(float amount) {
    }

    @Override
    public void reportDismissed(int option) {

    }
}
