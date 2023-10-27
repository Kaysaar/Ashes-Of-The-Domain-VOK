package data.kaysaar_aotd_vok.ui;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;

import java.util.Map;

public class ResearchUIDP implements InteractionDialogPlugin {
    public InteractionDialogAPI dialog;

    static enum OptionID {
        INIT,
        LEAVE
    }

    @Override
    public void init(com.fs.starfarer.api.campaign.InteractionDialogAPI dialog) {
        this.dialog = dialog;
        dialog.hideVisualPanel();
        dialog.hideTextPanel();
        dialog.setPromptText("");
        dialog.setOpacity(0.85f);

        optionSelected(null, ResearchUIDP.OptionID.INIT);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {

        if (optionData == ResearchUIDP.OptionID.INIT) {
            //this is where the size of the panel is set, automatically centered
            dialog.showCustomVisualDialog(1224,
                    844,
                    new ResearchUIDelegate(ResearchUIPlugin.createDefault(), dialog));
        }
    }

    @Override
    public void optionMousedOver(String optionText, Object optionData) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {

    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }
}
