package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

public class BifrostManagmentDialog extends BasePopUpDialog {
    BifrostMainUI mainUI;
    Object panelInd;
    public BifrostManagmentDialog(DialogCreatorUI creatorUI) {
        super("Manage Bifrost Network");
        panelInd = ReflectionUtilis.invokeMethodWithAutoProjection("getIndustryPanel",ReflectionUtilis.findFieldWithMethodName(creatorUI,"getIndustryPanel"));

    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        float height = tooltip.getPosition().getHeight();
         mainUI = new BifrostMainUI(width, height);
        mainUI.createUI();
        tooltip.addCustom(mainUI.getMainPanel(), 2f);
        tooltip.setHeightSoFar(0f);
    }

    @Override
    public void onExit() {
        mainUI.clearUI();
        Global.getSoundPlayer().restartCurrentMusic();
        ReflectionUtilis.invokeMethodWithAutoProjection("recreateOverview",panelInd);
    }
}
