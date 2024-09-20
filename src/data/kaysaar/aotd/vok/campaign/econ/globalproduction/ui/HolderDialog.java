package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreInteractionListener;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import org.lwjgl.input.Keyboard;

import java.security.Key;
import java.util.List;
import java.util.Map;

public class HolderDialog implements InteractionDialogPlugin {
    transient CoreUITabId tab;
    transient Object param;
    transient InteractionDialogAPI api;
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
        try {
            ButtonAPI button = (ButtonAPI) ReflectionUtilis.getChildrenCopy((UIPanelAPI) ReflectionUtilis.invokeMethod("getCurrentTab",ProductionUtil.getCoreUI())).get(4);
            if(button.isChecked()&&!NidavelirMainPanelPlugin.isShowingUI){
                button.setChecked(false);
                CoreUITabId cur = Global.getSector().getCampaignUI().getCurrentCoreTab();
                NidavelirMainPanelPlugin.isShowingUI = true;
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f);
                Global.getSector().getCampaignUI().getCurrentInteractionDialog().showCustomVisualDialog(UIData.WIDTH, UIData.HEIGHT, new NidavelirMainPanelDelegate(new NidavelirMainPanelPlugin(false, cur, null), Global.getSector().getCampaignUI().getCurrentInteractionDialog()));
            }

        }
        catch (Exception e) {

        }

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
