package data.kaysaar.aotd.vok.ui.customprod;

import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import java.util.Map;

public class HolderDialog implements InteractionDialogPlugin {
    transient CoreUITabId tab;
    transient Object param;
    transient InteractionDialogAPI api;
    public static boolean sendSignalForPressing  =false;
    public IntervalUtil util = new IntervalUtil(0.1f,0.2f);
    public HolderDialog(CoreUITabId tab,Object param ) {
        this.tab = tab;
        this.param = param;

    }
    @Override
    public void init(InteractionDialogAPI dialog) {
        dialog.hideTextPanel();
        dialog.setPromptText("");
        this.api = dialog;
        dialog.getVisualPanel().finishFadeFast();
        dialog.setBackgroundDimAmount(0f);
        dialog.getVisualPanel().showCore(tab, null, param, new CoreDismisserListener(api,true));



    }

    @Override
    public void optionSelected(String optionText, Object optionData) {

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
