package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;

public class PauseRestoration extends BasePopUpDialog{
    BaseMegastrucutreMenu menu;
    GPMegaStructureSection sectionToRestore;
    public PauseRestoration(GPMegaStructureSection section, BaseMegastrucutreMenu menu, String headerTitle) {
        super(headerTitle);
        this.sectionToRestore = section;
        this.menu = menu;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        tooltip.setParaFont(Fonts.INSIGNIA_LARGE);
        tooltip.addPara("By pausing restoration efforts we wont be spending further money until project is resumed and it will free resources that is currently consuming",2f);
        tooltip.addSectionHeading("Currently consumed", Alignment.MID,10f);
        tooltip.addCustom(MegastructureUIMisc.createResourcePanel(width,40,40,sectionToRestore.getSpec().getGpRestorationCost(),Color.ORANGE),10f);

    }

    @Override
    public void applyConfirmScript() {
        super.applyConfirmScript();
        sectionToRestore.pauseReconstruction();
        menu.resetSection(sectionToRestore.getSpec().getSectionID());

    }
}