package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.slider;

import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseSliderDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components.SectionShowcaseSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.nidavelir.BaseNidavelirSection;

public class ManpowerManagementDialog extends BaseSliderDialog {
    BaseNidavelirSection section;
    public ManpowerManagementDialog(String headerTitle, BaseNidavelirSection section, SectionShowcaseSection menu, int availableManpower) {

        super(menu, headerTitle, 1, Math.max(availableManpower,section.getAssignedManpower()+availableManpower), section.getAssignedManpower(),0);
        this.section = section;
    }

    @Override
    public void populateTooltipTop(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.setParaInsigniaLarge();
        section.printEffectSectionPerManpowerPoint(effectiveSegment+1,tooltip);

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
        section.setAssignedManpower(currentSegment);
        menu.createUI();
    }

    @Override
    public LabelAPI createLabelForBar(TooltipMakerAPI tooltip) {
        return super.createLabelForBar(tooltip);
    }

}
