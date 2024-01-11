package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import lunalib.lunaSettings.LunaSettings;

public class PreCollapseFacility extends BaseMarketConditionPlugin {
    @Override
    public void apply(String id) {
        super.apply(id);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
                tooltip.addPara("Enables the construction of research facilities that generate an additional research databank each month.", Misc.getPositiveHighlightColor(),10f);
    }
    @Override
    public boolean showIcon() {
        return true;
    }
}
