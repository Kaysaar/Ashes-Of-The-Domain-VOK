package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.BaseIndustryOptionProvider;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.campaign.listeners.IndustryOptionProvider;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

public class BaseMegastructureTestDialog extends BasePopUpDialog {
    BaseMegastructureScript mega;
    public static float width = 1250;
    public static float height = 600;
    DialogCreatorUI creatorUI;
    IndustryOptionProvider.IndustryOptionData data;
    Object panelInd;
    public BaseMegastructureTestDialog(String headerTitle, BaseMegastructureScript mega, DialogCreatorUI creatorUI,IndustryOptionProvider.IndustryOptionData data) {
        super(headerTitle);
        this.mega = mega;
        this.creatorUI = creatorUI;
        panelInd = ReflectionUtilis.invokeMethodWithAutoProjection("getIndustryPanel",ReflectionUtilis.findFieldWithMethodName(creatorUI,"getIndustryPanel"));
        this.data = data;
    }

    @Override
    public void createConfirmAndCancelSection(CustomPanelAPI mainPanel) {

    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        super.createContentForDialog(tooltip, width);
        float section1=400;
        BaseMegastructureDialogContent content = new BaseMegastructureDialogContent(width,height,mega);
        tooltip.addCustom(content.getMainPanel(),1f).getPosition().inTL(-5,1);
        tooltip.setHeightSoFar(0f);
    }

    @Override
    public void onExit() {
        Global.getSoundPlayer().restartCurrentMusic();
        ReflectionUtilis.invokeMethodWithAutoProjection("recreateOverview",panelInd);

    }
}
