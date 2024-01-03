package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.LCAttractorHigh;
import com.fs.starfarer.api.impl.campaign.econ.LCAttractorLow;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap.CoronalSegment;

public class CoronalHypershunt extends LCAttractorLow implements MarketImmigrationModifier {


    @Override
    public void apply(String id) {
        super.apply(id);
        for (Industry industry : market.getIndustries()) {
            if(!industry.getId().contains("coronal")&&!industry.isHidden()){
                industry.getUpkeep().modifyFlat("penalty",1000000,"Lack of living space");
            }
        }
    }

    @Override
    public void unapply(String id) {
        for (Industry industry : market.getIndustries()) {
            if(!industry.getId().contains("coronal")){
                industry.getUpkeep().unmodifyFlat("penalty");
            }
        }
        super.unapply(id);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        tooltip.addPara("Maximum size of this colony : %s", 10f,Misc.getNegativeHighlightColor(),""+"2");
        tooltip.addPara("Building any industry / structure on this market will result in increase of upkeep by 1000%    ", Misc.getNegativeHighlightColor(),10f);
    }

    @Override
    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        incoming.getWeight().modifyFlat(getModId(),-50, Misc.ucFirst(condition.getName().toLowerCase()));
    }
}
