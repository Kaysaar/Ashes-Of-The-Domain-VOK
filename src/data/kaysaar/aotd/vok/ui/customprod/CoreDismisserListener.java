package data.kaysaar.aotd.vok.ui.customprod;
import com.fs.starfarer.api.campaign.CoreInteractionListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.util.ArrayList;

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
