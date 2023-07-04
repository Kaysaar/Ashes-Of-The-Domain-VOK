package data.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class regional_power extends BaseHazardCondition {
    public static float STABILITY= 1;
    @Override
    public void apply(String id) {
        market.getStability().modifyFlat(id,STABILITY,"Regional Power's planet");
    }


    @Override

    public void unapply(String id) {
        market.getStability().unmodify(id);
    }
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        tooltip.addPara(
                "%s stability bonus",
                10f,
                Misc.getHighlightColor(),
                "" + (int) STABILITY
        );
    }
}
