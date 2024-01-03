package data.kaysaar.aotd.vok.ui;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;

public class AoTDResearchUIDelegate implements CustomVisualDialogDelegate {
    protected DialogCallbacks callbacks;
    protected AoTDResearchUI plugin;
    protected InteractionDialogAPI dialog;

    public AoTDResearchUIDelegate(AoTDResearchUI panel , InteractionDialogAPI dialog){
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
