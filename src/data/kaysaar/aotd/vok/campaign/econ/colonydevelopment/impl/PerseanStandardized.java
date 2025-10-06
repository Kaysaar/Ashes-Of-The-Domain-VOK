package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;

public class PerseanStandardized extends BaseColonyDevelopment {

    @Override
    public String getName() {
        return "Persean-Standardized Protocol";
    }

    @Override
    public void generateDescriptionSection(MarketAPI market, TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "We’ll follow the standard colonial protocols first established during the early days of the Persean Sector.",
                5f
        );

        tooltip.addPara(
                "These tried-and-true methods have guided countless settlements across the stars. " +
                        "They may not grant any remarkable advantages, but their reliability and familiarity make them a safe foundation for our new colony.",
                3f
        );


    }

    @Override
    public void generateEffects(MarketAPI market, TooltipMakerAPI tooltip, String fontForSections) {
        tooltip.addPara("None",10f).setAlignment(Alignment.MID);
    }



    @Override
    public void generateOtherInfo(MarketAPI market, TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "A balanced, dependable approach — proven to work, and unlikely to fail.", Misc.getTooltipTitleAndLightHighlightColor(),
                5f
        );
    }

    @Override
    public boolean canBeAppliedOnMarket(MarketAPI market) {
        SectorEntityToken token = market.getPrimaryEntity();
        if(token instanceof PlanetAPI planet){
            return !planet.isGasGiant();
        }

        return true;
    }

    @Override
    public void apply(MarketAPI market) {

    }

    @Override
    public void unapply(MarketAPI market) {

    }
}
