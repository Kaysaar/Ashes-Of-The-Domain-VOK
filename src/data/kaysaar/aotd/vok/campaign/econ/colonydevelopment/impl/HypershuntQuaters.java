package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;

import java.awt.*;

public class HypershuntQuaters extends BaseColonyDevelopment {
    @Override
    public String getName() {
        return "Hypershunt Makeshift Quarters";
    }

    @Override
    public boolean canBeAppliedOnMarket(MarketAPI market) {
        return market.hasIndustry(AoTDIndustries.HYPERSHUNT_CONTROL);
    }

    @Override
    public void apply(MarketAPI market) {

    }
    @Override
    public void generateEffects(MarketAPI market, TooltipMakerAPI tooltip, String fontForSections) {
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Decentralized Industrial Complex", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(
                "All other industries will have their output reduced by up to %s, depending on colony size.",
                3f,
                Misc.getNegativeHighlightColor(),
                "4 units"
        );
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Planetwide Transport Network", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(
                "Maintenance costs are increased by %s across the board on all industries, infrastructure, and facilities on this world.",
                3f,
                Misc.getNegativeHighlightColor(),
                "+50%"
        );

        tooltip.addPara(
                "If the %s is built and operational, this penalty is reduced to %s due to improved logistical efficiency.",
                3f,
                Misc.getPositiveHighlightColor(),
                "Maglev Network", "+30%"
        );
        tooltip.addPara("Resort Center receives %s income bonus!", 3f, Misc.getPositiveHighlightColor(), "10%");
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Scattered Settlements", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara("Lowers synergy efficiency by %s and lowers Pather interest significantly, due to being harder to establish a hidden base on this planet", 3f, Color.ORANGE, "10%");
    }
    @Override
    public void unapply(MarketAPI market) {

    }

    @Override
    public boolean canShowOnMarket(MarketAPI market) {
        return false;
    }
}
