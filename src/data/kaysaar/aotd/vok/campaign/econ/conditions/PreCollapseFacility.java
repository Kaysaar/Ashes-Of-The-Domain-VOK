package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.industry.ResearchFacility;
import lunalib.lunaSettings.LunaSettings;
import org.lazywizard.console.Console;

import java.util.ArrayList;
import java.util.Locale;

public class PreCollapseFacility extends BaseMarketConditionPlugin {
    @Override
    public void apply(String id) {

       market.getStats().getDynamic().getStat(ResearchFacility.researchFacilityModForDatabanks).modifyFlat("pcf",1);
    }

    @Override
    public void unapply(String id) {

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
