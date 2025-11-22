package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections.NidavelirBaseSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastructureMenu;

public class ManpowerManagementDialog extends BaseSliderDialog{
    NidavelirBaseSection section;
    public ManpowerManagementDialog(String headerTitle, NidavelirBaseSection section, BaseMegastructureMenu menu, int availableManpower) {

        super(menu, headerTitle, 1, Math.max(availableManpower,section.getCurrentManpowerAssigned()+availableManpower), section.getCurrentManpowerAssigned(),0);
        this.section = section;
    }

    @Override
    public void populateTooltipTop(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.setParaInsigniaLarge();
        section.printMenu(tooltip,effectiveSegment+1,false);

    }

    @Override
    public void populateTooltipBelow(TooltipMakerAPI tooltip, int effectiveSegment) {
        section.printEffects(tooltip,effectiveSegment+1,false);
    }

    @Override
    public float getBarX() {
        return 5f;
    }

    @Override
    public float getBarY() {
        return 85f;
    }

    @Override
    public void applyConfirmScript() {
        section.setCurrentManpowerAssigned(currentSegment);
        menu.resetSection(section.getSpec().getSectionID());
    }

    @Override
    public LabelAPI createLabelForBar(TooltipMakerAPI tooltip) {
        return super.createLabelForBar(tooltip);
    }

}
