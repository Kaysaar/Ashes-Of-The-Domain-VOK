package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreInteractionListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;

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
            dialogAPI.dismiss();
        }
    }
}
