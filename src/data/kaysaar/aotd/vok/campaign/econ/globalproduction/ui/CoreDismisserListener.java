package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreInteractionListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.util.ArrayList;
import java.util.List;

public class CoreDismisserListener implements CoreInteractionListener {
    boolean shouldDismissdialog = false;
    InteractionDialogAPI dialogAPI;
    public CoreDismisserListener(InteractionDialogAPI dialogAPI , boolean shouldDismissdialog) {
        this.dialogAPI = dialogAPI;
        this.shouldDismissdialog = shouldDismissdialog;

    }
    @Override
    public void coreUIDismissed() {
        if(!NidavelirMainPanelPlugin.isShowingUI){
            dialogAPI.getVisualPanel().fadeVisualOut();
            dialogAPI.dismiss();
            ArrayList<UIComponentAPI> componentAPIS  = (ArrayList<UIComponentAPI>) ReflectionUtilis.invokeMethod("getChildrenCopy",ReflectionUtilis.invokeMethod("getButtons",ProductionUtil.getCoreUI()));

        }
    }
}
