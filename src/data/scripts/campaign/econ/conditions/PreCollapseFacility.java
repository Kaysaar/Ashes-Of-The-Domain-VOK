package data.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.Collections;

public class PreCollapseFacility extends BaseMarketConditionPlugin {
    @Override
    public void apply(String id) {
        super.apply(id);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        tooltip.addPara("Allows built there research facilities to produce +1 research databank per month", Misc.getPositiveHighlightColor(),10f);
    }
    @Override
    public boolean showIcon() {
        return true;
    }
}
