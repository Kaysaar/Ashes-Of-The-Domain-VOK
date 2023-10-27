package data.kaysaar_aotd_vok.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class regional_power_capital_lost extends BaseHazardCondition {
    public static float STABILITY_PENALTY = -3;
    @Override
    public void apply(String id) {
        market.getStability().modifyFlat(id,STABILITY_PENALTY,"Lost Capital");
    }


    @Override

    public void unapply(String id) {
        market.getStability().unmodify(id);
    }
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        tooltip.addPara(
                "%s stability penalty",
                10f,
                Misc.getNegativeHighlightColor(),
                "" + (int) STABILITY_PENALTY
        );
    }
}
