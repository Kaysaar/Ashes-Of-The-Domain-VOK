package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.onhover;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

public class GuideTootltip implements TooltipMakerAPI.TooltipCreator {
    @Override
    public boolean isTooltipExpandable(Object tooltipParam) {
        return true;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 800;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        tooltip.addTitle("Titans of industry : Guide");
        tooltip.addSectionHeading("Useful key binds", Alignment.MID,10f);
        tooltip.addPara("Shift + LMB : %s",10f, Color.ORANGE,"Increase quantity of items in order being produced at once");
        tooltip.addPara("Shift + RMB : %s",5, Color.ORANGE,"Decrease quantity of items in order being produced at once");


    }
}
