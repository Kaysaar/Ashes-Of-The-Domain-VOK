package data.ui;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;

public class ResearchUIDelegate implements CustomVisualDialogDelegate {
    protected DialogCallbacks callbacks;
    protected ResearchUIPlugin plugin;
    protected InteractionDialogAPI dialog;

    public ResearchUIDelegate(ResearchUIPlugin missionPanel, InteractionDialogAPI dialog) {
        this.plugin = missionPanel;
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
        return 0;
    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void reportDismissed(int option) {

    }
}
