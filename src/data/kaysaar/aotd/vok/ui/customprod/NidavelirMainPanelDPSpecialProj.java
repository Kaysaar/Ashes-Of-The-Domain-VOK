package data.kaysaar.aotd.vok.ui.customprod;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;

import java.util.Map;

public class NidavelirMainPanelDPSpecialProj implements InteractionDialogPlugin {
    public InteractionDialogAPI dialog;

    static enum OptionID {
        INIT,
        LEAVE
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        this.dialog.getTextPanel();
        dialog.hideTextPanel();
        dialog.setPromptText("");
        dialog.getVisualPanel().finishFadeFast();
        dialog.setBackgroundDimAmount(0f);
        optionSelected(null, NidavelirMainPanelDP.OptionID.INIT);
    }


    @Override
    public void optionSelected(String optionText, Object optionData) {
        if (optionData == NidavelirMainPanelDP.OptionID.INIT) {
            //this is where the size of the panel is set, automatically centered
            dialog.showCustomVisualDialog(UIData.WIDTH, UIData.HEIGHT, new NidavelirMainPanelDelegate(new NidavelirMainPanelPlugin(true,null,null),dialog));

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
