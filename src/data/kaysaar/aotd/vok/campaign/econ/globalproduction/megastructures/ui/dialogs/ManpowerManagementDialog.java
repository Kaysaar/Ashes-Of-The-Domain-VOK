package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections.NidavelirBaseSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;

import java.awt.*;

public class ManpowerManagementDialog extends BaseSliderDialog{
    NidavelirBaseSection section;
    public ManpowerManagementDialog(String headerTitle, NidavelirBaseSection section, BaseMegastrucutreMenu menu) {
        super(menu, headerTitle, 1, NidavelirBaseSection.MaxManpowerPerSection, section.getCurrentManpowerAssigned(),0);
    }

    @Override
    public void populateTooltipBelowBar(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.addPara("Currently assigned manpower %s light years",5f, Color.ORANGE,""+(currentSegment*mult));}

    @Override
    public LabelAPI createLabelForBar(TooltipMakerAPI tooltip) {
        return super.createLabelForBar(tooltip);
    }

}
