package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class CrashedRingSegments extends BaseMarketConditionPlugin {

    @Override
    public void apply(String id) {
        Industry ind = market.getIndustry(Industries.POPULATION);
        if (ind != null) {
            ind.getSupply(Commodities.METALS).getQuantity().modifyFlat(id, 3);
            ind.getSupply(Commodities.HEAVY_MACHINERY).getQuantity().modifyFlat(id, 3);
        }

        market.getHazard().modifyFlat(id, 0.25f, "Crashed Ring Segments");
    }

    @Override
    public void unapply(String id) {
        Industry ind = market.getIndustry(Industries.POPULATION);
        if (ind != null) {
            ind.getSupply(Commodities.METALS).getQuantity().unmodifyFlat(id);
            ind.getSupply(Commodities.HEAVY_MACHINERY).getQuantity().unmodifyFlat(id);
        }

        market.getHazard().unmodifyFlat(id);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        float pad = 5F;

        String metals = Global.getSettings().getCommoditySpec(Commodities.METALS).getName();
        String heavyMachinery = Global.getSettings().getCommoditySpec(Commodities.HEAVY_MACHINERY).getName();

        tooltip.addPara(
                "The market produces an additional %s %s and %s %s on its own.",
                10F,
                Misc.getHighlightColor(),
                "+3", metals,
                "+3", heavyMachinery
        );
        tooltip.addPara(
                "Reduce resource cost of %s by %s",
                pad,
                Misc.getHighlightColor(),
                 "Nexus Core",
                "90%"
        );

        tooltip.addPara(
                "Increases market hazard rating by %s.",
                pad,
                Misc.getNegativeHighlightColor(),
                "25%"
        );
        tooltip.addPara(
                "Upon restoration of Nexus Core section, this market condition will be removed!",
                Misc.getTooltipTitleAndLightHighlightColor(),
                pad
        );
    }
}