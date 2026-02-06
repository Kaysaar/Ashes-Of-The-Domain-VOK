package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.GPUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastructureMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;

public class RestorationDialog extends BasePopUpDialog {
    BaseMegastructureMenu menu;
    GPMegaStructureSection sectionToRestore;
    String restoration;
    public RestorationDialog(GPMegaStructureSection section, BaseMegastructureMenu menu, String headerTitle, String restoration) {
        super(headerTitle);
        this.sectionToRestore = section;
        this.menu = menu;
        this.restoration = restoration;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        tooltip.setParaFont(Fonts.INSIGNIA_LARGE);
        tooltip.addPara(restoration+" of %s will cost us %s monthly and require a constant flow of the following resources till completion:",2f, Color.ORANGE,sectionToRestore.getSpec().getName(),
                Misc.getDGSCredits(sectionToRestore.getSpec().getRenovationCost()));
        tooltip.addSectionHeading(restoration+" Cost", Alignment.MID,10f);
        tooltip.addCustom(GPUIMisc.createResourcePanel(width,40,40,sectionToRestore.getSpec().getGpRestorationCost(),Color.ORANGE),10f);
        tooltip.addSectionHeading("Available Resources", Alignment.MID,10f);
        tooltip.addCustom(GPUIMisc.createResourcePanel(width,40,40, GPManager.getInstance().getTotalResources(),null),10f);
        tooltip.addPara("If resource criteria are met, we should be able to finish this project in %s",10f,Color.ORANGE, AoTDMisc.convertDaysToString((int) sectionToRestore.getSpec().getDaysForRenovation()));
        tooltip.addPara("Estimated monthly upkeep after restoration : %s",5f,Color.ORANGE,Misc.getDGSCredits(sectionToRestore.getUpkeep(true)));
    }

    @Override
    public void applyConfirmScript() {
        super.applyConfirmScript();
        sectionToRestore.startReconstruction();
        menu.resetSection(sectionToRestore.getSpec().getSectionID());
    }
}
