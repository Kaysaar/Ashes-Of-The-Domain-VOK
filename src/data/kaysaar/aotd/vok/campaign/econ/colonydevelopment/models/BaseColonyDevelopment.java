package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public abstract class BaseColonyDevelopment implements ColonyDevelopmentAPI{
    public static String condIdApplier = "aotd_colony_development_cond";
    //Maybe gonna expand this later?

    @Override
    public void generateDetailingTooltip(MarketAPI market, TooltipMakerAPI tooltip) {
        tooltip.setTitleOrbitronLarge();
        tooltip.addTitle(getName());
        generateDescriptionSection(market, tooltip);
        tooltip.addSpacer(10f);
        tooltip.addSectionHeading("Effects from colony development plan", Alignment.MID,0f);
        generateEffects(market, tooltip, "graphics/fonts/orbitron20aabold.fnt");
        tooltip.addSpacer(15f);
        generateOtherInfo(market, tooltip);

    }

    @Override
    public void generateTooltipForMarketCond(MarketAPI market, TooltipMakerAPI tooltip, boolean expanded) {
        tooltip.setParaFont(Fonts.ORBITRON_12);
        tooltip.addPara(getName(), Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addSpacer(10f);
        tooltip.addSectionHeading("Effects from colony development plan", Alignment.MID,0f);
        generateEffects(market, tooltip, Fonts.ORBITRON_12);
    }

    public void generateDescriptionSection(MarketAPI market, TooltipMakerAPI tooltip) {

    }
    public void generateEffects(MarketAPI market, TooltipMakerAPI tooltip, String fontForSections) {

    }
    public void generateDrawbacksSection(MarketAPI market, TooltipMakerAPI tooltip) {

    }
    public void generateOtherInfo(MarketAPI market, TooltipMakerAPI tooltip) {

    }
}
