package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.conditions.GnajidAICCondition;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergySourceAPI;

public class GnajidAICSynergySource extends BaseIndustrySynergySource {
    public static final String SOURCE_KEY = "aotd_gnajid_aic_synergy_source";

    public GnajidAICSynergySource() {
        super(0f, Industries.POPULATION);
    }



    @Override
    public float calculateEfficiencyFromIndustry(Industry industry, boolean includeDemand) {
        if (industry == null) return 0f;
        MarketAPI market = industry.getMarket();
        if (market == null || !market.hasCondition(GnajidAICCondition.CONDITION_ID)
                || market.getFaction() == null || !market.getFaction().isPlayerFaction()) {
            return 0f;
        }

        float otherEfficiency = 0f;
        for (IndustrySynergySourceAPI source : IndustrySynergiesManager.getInstance().getSourcesOfSynergy().values()) {
            // Exclude ourselves so calculating the bonus never calls back into itself.
            if (source instanceof GnajidAICSynergySource) continue;
            String industryId = source.getId();
            if (industryId == null) continue;
            Industry contributor = market.getIndustry(industryId);
            if (contributor != null) {
                otherEfficiency += source.calculateEfficiencyFromIndustry(contributor, includeDemand);
            }
        }
        return Math.max(0f, otherEfficiency) * GnajidAICCondition.SYNERGY_BONUS;
    }

    @Override
    public float getBonusForImproved() {
        return 0f;
    }

    @Override
    public float getBonusForAICore(String coreId) {
        return 0f;
    }

    @Override
    public void addToTooltip(Industry industry, Industry.IndustryTooltipMode mode,
                             TooltipMakerAPI tooltip, float width, boolean expanded) {
        // AIC effects are shown by the market condition tooltip, not the host Population industry.
    }

    @Override
    public void addToTooltipForInfo(Industry industry, TooltipMakerAPI tooltip) {
        if (industry != null && industry.getMarket() != null
                && industry.getMarket().hasCondition(GnajidAICCondition.CONDITION_ID)) {
            tooltip.addPara("Advanced Industrial Controller increases synergy efficiency by %s.",
                    3f, Misc.getPositiveHighlightColor(), "+50%");
        }
    }
}
