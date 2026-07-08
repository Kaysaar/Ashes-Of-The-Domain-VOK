package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDConditions;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;
import data.kaysaar.aotd.vok.campaign.econ.industry.ResearchFacility;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;

import java.awt.*;

public class ArcheoSite extends BaseColonyDevelopment {

    private String sourceIdString;

    @Override
    public String getName() {
        return "Archeo-Site";
    }

    @Override
    public void generateDescriptionSection(MarketAPI market, TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "Amongst the most enigmatic of ruins from the Domain, its original function is an ongoing debate by those working onsite. No matter how deep or far expeditions and tech excavations, crews continue to uncover troves of technology and research data with no end in sight.",
                5f
        );
    }

    @Override
    public float getOrder() {
        return 100f;
    }

    @Override
    public boolean canBeAppliedOnMarket(MarketAPI market) {
        return canShowOnMarket(market);
    }

    @Override
    public Color getBrightButtonColour(MarketAPI market) {
        return Global.getSettings().getFactionSpec(Factions.REMNANTS).getBaseUIColor();
    }

    @Override
    public Color getDarkButtonColour(MarketAPI market) {
        return Global.getSettings().getFactionSpec(Factions.REMNANTS).getDarkUIColor();
    }

    @Override
    public void generateEffects(MarketAPI market, TooltipMakerAPI tooltip, String fontForSections) {
        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Restricted Excavation Charter", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(
                "Only %s, %s, %s and %s may be constructed on this market.",
                3f,
                Misc.getHighlightColor(),
                "Tech-Mining", "Research Facility", "Ground Defenses","Orbital Station"
        );
        tooltip.addPara(
                "%s extracts %s as many databanks each month, while %s has increased efficiency by %s.",
                3f,
                Misc.getPositiveHighlightColor(),
                "Research Facility", "2x", "Tech-Mining","70%"
        );

        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Permanent Archaeological Preserve", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(
                "Colony size is capped at %s to preserve the excavation zones and prevent uncontrolled urban expansion.",
                3f,
                Misc.getNegativeHighlightColor(),
                "3"
        );

        tooltip.setParaFont(fontForSections);
        tooltip.addPara("Expeditionary Security Corps", Misc.getTooltipTitleAndLightHighlightColor(), 5f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(
                "%s also functions as a limited %s, allowing the colony to maintain local security fleets.",
                3f,
                Color.ORANGE,
                "Population and Infrastructure", "Patrol HQ"
        );
        tooltip.addPara(
                "Fleet size contribution from this market is capped at %s.",
                3f,
                Misc.getNegativeHighlightColor(),
                "20%"
        );
    }

    @Override
    public void apply(MarketAPI market) {
        if(!market.hasIndustry(Industries.PATROLHQ)){
            market.addIndustry(Industries.PATROLHQ);
            market.getIndustry(Industries.PATROLHQ).setHidden(true);
        }
        sourceIdString = "aotd_correction";
        market.getStats().getDynamic().getMod(
                Stats.MAX_MARKET_SIZE).modifyFlat(sourceIdString,-Misc.getMaxMarketSize(market)+2,"Archeo-Site");
        market.getStats().getDynamic().getStat(Stats.TECH_MINING_MULT).modifyMult(sourceIdString, 1.7f,"Archeo-Site");
        market.getStats().getDynamic().getMod(
                Stats.COMBAT_FLEET_SIZE_MULT).unmodifyFlat(sourceIdString);

        float currentMult  =   market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).getMult();
        if(currentMult>1){
            float targetMult  = 1;
            float counterTarget = targetMult/currentMult;
            market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyMult(sourceIdString, counterTarget,"Archeo-Site");
        }
        float current =   market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).computeEffective(0f);
        if(current>0.2f){
            float target = 0.2f;
            float change = current - target;
            float mult =1;
            if(currentMult<1){
                mult = 1/currentMult;
            }
            market.getStats().getDynamic().getMod(
                    Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat(sourceIdString,-change*mult,"Archeo-Site");
        }

        market.getStats().getDynamic().getStat(ResearchFacility.researchFacilityModForDatabanks).modifyMult(sourceIdString, 2f,"Archeo-Site");
    }

    @Override
    public void unapply(MarketAPI market) {
        market.getStats().getDynamic().getMod(
                Stats.MAX_MARKET_SIZE).unmodifyFlat(sourceIdString);
        market.getStats().getDynamic().getMod(
                Stats.COMBAT_FLEET_SIZE_MULT).unmodifyFlat(sourceIdString);
        market.getStats().getDynamic().getMod(
                Stats.COMBAT_FLEET_SIZE_MULT).unmodifyMult(sourceIdString);
        market.getStats().getDynamic().getMod(
                Stats.TECH_MINING_MULT).unmodifyFlat(sourceIdString);
    }

    @Override
    public boolean canShowOnMarket(MarketAPI market) {
        return Misc.hasRuins(market)||market.hasCondition(AoTDConditions.PRE_COLLAPSE_FACILITY);
    }
}
