package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

public class OnHoverButtonTooltip implements TooltipMakerAPI.TooltipCreator {
    public GPMegaStructureSection section;
    public String buttonId;
    public OnHoverButtonTooltip(GPMegaStructureSection section, String buttonId) {
        this.section = section;
        this.buttonId = buttonId;
    }
    @Override
    public boolean isTooltipExpandable(Object tooltipParam) {
        return true;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 400;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        section.createTooltipForButtons(tooltip,buttonId);
    }


}
