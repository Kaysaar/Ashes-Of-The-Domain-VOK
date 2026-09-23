package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;

import java.awt.*;
import java.util.LinkedHashMap;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.createTooltipOfResourcesForDialogConsumed;

public class AbyssalFrontier extends BaseColonyDevelopment {
    @Override
    public String getName() {
        return "Abyssal Frontier";
    }

    @Override
    public Color getBrightButtonColour(MarketAPI market) {
        return Global.getSettings().getFactionSpec(Factions.DWELLER).getBaseUIColor();
    }

    @Override
    public Color getDarkButtonColour(MarketAPI market) {
        return Global.getSettings().getFactionSpec(Factions.DWELLER).getDarkUIColor();
    }

    @Override
    public boolean canShowOnMarket(MarketAPI market) {
        return market.getContainingLocation().hasTag(Tags.SYSTEM_ABYSSAL);
    }

    @Override
    public boolean canBeAppliedOnMarket(MarketAPI market) {
        return true;
    }

    @Override
    public void apply(MarketAPI market) {

    }

    @Override
    public void generateDescriptionSection(MarketAPI market, TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "Establishing a permanent settlement this deep within the Abyss is an undertaking few would consider sane. " +
                        "The immense pressure exerted on drive fields and the near-total degradation of long-range sensors leave the colony effectively isolated from the rest of the Sector.",
                5f
        );

        tooltip.addPara(
                "What would elsewhere be crippling isolation may, however, prove useful here. Far beyond conventional trade routes, patrols, and casual observation, " +
                        "the colony can pursue lines of research and industry that would be considerably more difficult to conceal elsewhere.",
                5f
        );

        tooltip.addPara(
                "The surrounding darkness appears empty enough. Whether it is quite as uninhabited as it seems is another matter.",
                5f
        );
    }

    public void generateEffectsForMarketCondition(
            MarketAPI market,
            TooltipMakerAPI tooltip,
            String fontForSections,
            boolean forMarketCondition
    ) {
        tooltip.setParaFont(fontForSections);
        tooltip.addPara(
                "Cut Off From the Rest",
                Misc.getTooltipTitleAndLightHighlightColor(),
                5f
        );
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);

        tooltip.addPara(
                "The colony %s trade with other markets unless a reliable means of circumventing the Abyss is established.",
                3f,
                Misc.getNegativeHighlightColor(),
                "cannot"
        );

        tooltip.addPara(
                "The colony must remain largely %s to survive.",
                3f,
                Misc.getNegativeHighlightColor(),
                "self-reliant"
        );
        tooltip.addPara(
                "New unique structures will be available,in order to make this colony self-sufficient",
                Misc.getPositiveHighlightColor(),3f
        );
        tooltip.addPara(
                "Colony starts as size %s colony and %s.",
                3f,
                new Color[]{Misc.getPositiveHighlightColor(),Misc.getNegativeHighlightColor()},
                "4","can't grow any further"
        );
        tooltip.setParaFont(fontForSections);
        tooltip.addPara(
                "Incentives for the Isolated",
                Misc.getTooltipTitleAndLightHighlightColor(),
                5f
        );
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);


        tooltip.addPara(
                "%s provides no income.",
                3f,
                Misc.getNegativeHighlightColor(),
                "Population & Infrastructure"
        );

        tooltip.addPara(
                "Maintaining the population instead introduces additional monthly upkeep based on colony size:",
                3f
        );

        tooltip.setBulletedListMode(BaseIntelPlugin.BULLET);
        tooltip.addPara(
                "Size %s: %s credits",
                3f,
                Misc.getHighlightColor(),
                "4",
                "250,000"
        );

        tooltip.setBulletedListMode(null);

        tooltip.setParaFont(fontForSections);
        tooltip.addPara(
                "An Ideal Black Site",
                Misc.getTooltipTitleAndLightHighlightColor(),
                5f
        );
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);


        tooltip.addPara(
                "Black Site construction cost is reduced by %s.",
                3f,
                Misc.getPositiveHighlightColor(),
                "50%"
        );

        tooltip.addPara(
                "Black Site established here require %s.",
                3f,
                Misc.getPositiveHighlightColor(),
                "no cover-up expenditure"
        );


        tooltip.setParaFont(fontForSections);
        tooltip.addPara(
                "Island in an Endless Ocean",
                Misc.getTooltipTitleAndLightHighlightColor(),
                5f
        );
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);



        tooltip.addPara(
                "Allows further pursuit of the %s.",
                3f,
                Global.getSettings().getFactionSpec(Factions.DWELLER).getBaseUIColor(),
                "Dangerous Technologies"
        );

        tooltip.addPara(
                "Unlocks more advanced and efficient methods of %s.",
                3f,
                Misc.getPositiveHighlightColor(),
                "Tenebrium production"
        );
        if (!forMarketCondition) {
            tooltip.addSectionHeading("Additional Cost For Colonization (Available)", Alignment.MID,5f);
            LinkedHashMap<String,Integer> costs = new LinkedHashMap<>();
            costs.put(Commodities.CREW, 10000);
            costs.put(Commodities.HEAVY_MACHINERY, 1000);
            costs.put(Commodities.SUPPLIES, 2000);
            tooltip.addCustom(createTooltipOfResourcesForDialogConsumed(tooltip.getWidthSoFar()-10,45,45,costs,false),5f);
        }
    }

    @Override
    public void generateEffects(MarketAPI market, TooltipMakerAPI tooltip, String fontForSections) {
        generateEffectsForMarketCondition(
                market,
                tooltip,
                fontForSections,
                !fontForSections.equals(Fonts.ORBITRON_20AABOLD)
        );
    }
    @Override
    public void unapply(MarketAPI market) {

    }
}
