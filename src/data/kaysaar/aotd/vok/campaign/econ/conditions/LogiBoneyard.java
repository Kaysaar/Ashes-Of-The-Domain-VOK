package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class LogiBoneyard extends BaseMarketConditionPlugin {

    @Override
    public void apply(String id) {
        Industry ind = market.getIndustry(Industries.POPULATION);
        if (ind != null) {
            ind.getSupply(Commodities.METALS).getQuantity().modifyFlat(id, 4);
            ind.getSupply(Commodities.RARE_METALS).getQuantity().modifyFlat(id, 2);
        }

        market.getHazard().modifyFlat(id, 0.25f, "Logistical Boneyard");
    }

    @Override
    public void unapply(String id) {
        Industry ind = market.getIndustry(Industries.POPULATION);
        if (ind != null) {
            ind.getSupply(Commodities.METALS).getQuantity().unmodifyFlat(id);
            ind.getSupply(Commodities.RARE_METALS).getQuantity().unmodifyFlat(id);
        }

        market.getHazard().unmodifyFlat(id);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        float pad = 10f;

        String metals = Global.getSettings().getCommoditySpec(Commodities.METALS).getName();
        String rareMetals = Global.getSettings().getCommoditySpec(Commodities.RARE_METALS).getName();

        tooltip.addPara(
                "The market produces an additional %s %s and %s %s on its own.",
                pad,
                Misc.getHighlightColor(),
                "+4", metals,
                "+2", rareMetals
        );

        tooltip.addPara(
                "Increases market hazard rating by %s.",
                pad,
                Misc.getNegativeHighlightColor(),
                "25%"
        );
    }
}