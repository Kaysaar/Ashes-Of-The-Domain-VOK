package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreInteractionListener;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import org.lwjgl.input.Keyboard;

import java.security.Key;
import java.util.Map;

public class HolderDialog implements InteractionDialogPlugin {
    public CoreUITabId tab;
    public Object param;
    InteractionDialogAPI api;
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
        if(!Keyboard.isCreated())return;
        try {
            if(Keyboard.isKeyDown(Keyboard.KEY_5)&&!NidavelirMainPanelPlugin.isShowingUI) {
                CoreUITabId cur = Global.getSector().getCampaignUI().getCurrentCoreTab();
                NidavelirMainPanelPlugin.isShowingUI = true;
                Global.getSector().getCampaignUI().getCurrentInteractionDialog().getVisualPanel().closeCoreUI();
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
