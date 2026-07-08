package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl;

import com.fs.starfarer.api.Global;
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
        return "Hypershunt Emergency Mode";
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
        tooltip.addPara("Hypershunt Makeshift Quarters", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(
                "Colony size is capped at 2, and no industry / structure is allowed ",
                Misc.getNegativeHighlightColor(),
                3f
        );
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Hyperspecialized Mega Engineering", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(
                "Allows restoration and control over Coronal Hypershunt internal systems",
                Misc.getPositiveHighlightColor(),
                3f
        );

        tooltip.setParaFont(fontForSections);
        if(Global.getSettings().getModManager().isModEnabled("aotd_sop")){
            tooltip.addPara("Docking Shrouds", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
            tooltip.setParaFont(Fonts.DEFAULT_SMALL);
            tooltip.addPara("Market has capacity to host patrol fleets, that will guard said megastructure",Misc.getPositiveHighlightColor(),3f);
        }

    }
    @Override
    public void unapply(MarketAPI market) {

    }

    @Override
    public void generateDescriptionSection(MarketAPI market, TooltipMakerAPI tooltip) {
       tooltip.addPara("An emergency control protocol created by your scientists after the original controlling intelligence disappeared. Taking advantage of the numerous failsafes and redundant systems, a small crew of engineers and scientists keep this venerable megastructure running at a level, while quite not the same as it was in its prime, still vastly outmatches anything in the modern sector.",5f);
    }

    @Override
    public void generateOtherInfo(MarketAPI market, TooltipMakerAPI tooltip) {
        tooltip.addPara("The hypershunts were never meant to be operated like this and one slip up will probably NOT END WELL.",Misc.getNegativeHighlightColor(),5f);
    }

    @Override
    public boolean canShowOnMarket(MarketAPI market) {
        return false;
    }
}
