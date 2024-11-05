package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;

public class RestorationDialog extends BasePopUpDialog{
    BaseMegastrucutreMenu menu;
    GPMegaStructureSection sectionToRestore;
    public RestorationDialog(GPMegaStructureSection section, BaseMegastrucutreMenu menu, String headerTitle) {
        super(headerTitle);
        this.sectionToRestore = section;
        this.menu = menu;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        tooltip.setParaFont(Fonts.INSIGNIA_LARGE);
        tooltip.addPara("Restoration of %s will cost us %s monthly  and require a constant flow of the following resources till completion:",2f, Color.ORANGE,sectionToRestore.getSpec().getName(),
                Misc.getDGSCredits(sectionToRestore.getSpec().getRenovationCost()));
        tooltip.addSectionHeading("Restoration Cost", Alignment.MID,10f);
        tooltip.addCustom(MegastructureUIMisc.createResourcePanel(width,40,40,sectionToRestore.getSpec().getGpRestorationCost(),Color.ORANGE),10f);
        tooltip.addSectionHeading("Available Resources", Alignment.MID,10f);
        tooltip.addCustom(MegastructureUIMisc.createResourcePanel(width,40,40, GPManager.getInstance().getTotalResources(),null),10f);
        tooltip.addPara("If resource criteria are met, we should be able to restore this section in %s",10f,Color.ORANGE, AoTDMisc.convertDaysToString((int) sectionToRestore.getSpec().getDaysForRenovation()));
    }

    @Override
    public void applyConfirmScript() {
        super.applyConfirmScript();
        sectionToRestore.startReconstruction();
        menu.resetSection(sectionToRestore.getSpec().getSectionID());
    }
}