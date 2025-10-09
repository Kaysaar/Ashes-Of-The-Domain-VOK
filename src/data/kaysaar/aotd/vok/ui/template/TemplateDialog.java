package data.kaysaar.aotd.vok.ui.template;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;

public class TemplateDialog extends BasePopUpDialog {
    ExtendedUIPanelPlugin testPlugin;
    public TemplateDialog(String headerTitle,ExtendedUIPanelPlugin testPlugin) {
        super(headerTitle);
        this.testPlugin = testPlugin;
        AshMisc.initPopUpDialog(this,testPlugin.getMainPanel().getPosition().getWidth()+100,testPlugin.getMainPanel().getPosition().getHeight()+100);
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        tooltip.addCustom(testPlugin.getMainPanel(),10f).getPosition().inTL(45,10);

    }


    @Override
    public void onExit() {
        super.onExit();
        Global.getSector().setPaused(false);
    }
}
